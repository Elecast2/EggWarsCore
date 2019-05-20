package net.minemora.eggwarscore.game;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.arena.ArenaManager;
import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.lobby.Lobby;
import net.minemora.eggwarscore.lobby.LobbyItem;
import net.minemora.eggwarscore.menu.MapVoteMenu;
import net.minemora.eggwarscore.menu.TeamMenu;
import net.minemora.eggwarscore.menu.TimeVoteMenu;
import net.minemora.eggwarscore.scoreboard.ScoreboardManager;
import net.minemora.eggwarscore.shared.VaultManager;
import net.minemora.eggwarscore.team.Team;
import net.minemora.eggwarscore.team.TeamManager;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.Utils;

public class GameLobby extends Multicast {

	private final int id;
	private String dateString;
	private Game game;
	private Map<Integer,GameTeam> gameTeams = new HashMap<>();
	private boolean counting;
	private TeamMenu teamMenu;
	
	private Map<String, Integer> mapVotes = new HashMap<>();
	private MapVoteMenu mapVoteMenu;
	
	private Map<String, Integer> timeVotes = new HashMap<>();
	private TimeVoteMenu timeVoteMenu;
	
	private boolean started;

	public GameLobby(int id) {
		this.id = id;
		reset();
	}
	
	public void reset() {
		Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat(ConfigMain.get().getString("general.date-format"));
        dateString = formatter.format(date);
        mapVotes.clear();
        timeVotes.clear();
        gameTeams.clear();
        for(Team team : TeamManager.getTeams().values()) {
			gameTeams.put(team.getId(), new GameTeam(team));
		}
        if(ArenaManager.getArenas().size() <= ConfigMain.get().getInt("general.max-votable-maps")) {
        	for(String worldName : ArenaManager.getArenas().keySet()) {
            	mapVotes.put(worldName, 0);
    		}
        }
        else {
        	while(mapVotes.size() < ConfigMain.get().getInt("general.max-votable-maps")) {
            	mapVotes.put(Utils.choice(ArenaManager.getArenas().keySet()), 0);
    		}
        }
        timeVotes.put("day", 0); //TODO CONF
        timeVotes.put("night", 0);
        timeVotes.put("morning", 0);
        if(teamMenu != null) {
        	HandlerList.unregisterAll(teamMenu);
        }
        teamMenu = new TeamMenu(this);
        
        if(mapVoteMenu != null) {
        	HandlerList.unregisterAll(mapVoteMenu);
        }
        mapVoteMenu = new MapVoteMenu(this);
        
        if(timeVoteMenu != null) {
        	HandlerList.unregisterAll(timeVoteMenu);
        }
        timeVoteMenu = new TimeVoteMenu(this);
        
        started = false;
	}

	public void addPlayer(Player player) {
		if(getPlayersCount() == TeamManager.getMaxPlayers()*TeamManager.getTeams().size() || isStarted()) {
			player.kickPlayer("This game is full");
			return;
		}
		getPlayers().add(player.getName());
		player.setGameMode(GameMode.ADVENTURE);
		player.teleport(Lobby.getLobby().getSpawn());
		player.sendMessage(ChatUtils.format(ConfigLang.get().getStringList("lobby.motd")));
		broadcast(player, ConfigLang.get().getString("lobby.player-join").replaceAll("%player%", player.getName())
				.replaceAll("%players%", String.valueOf(getPlayersCount()))
				.replaceAll("%max-players%", String.valueOf(TeamManager.getMaxPlayers()*TeamManager.getTeams().size())));
		GamePlayer gp = GamePlayer.get(player.getName());
		if(gp.getGame()!=null) {
			gp.removeFromGame();
		}
		gp.setGameLobby(this);
		gp.setGameTeam(null);
		gp.restore();
		gp.setDead(false);
		gp.setCurrentVotedMap(null);
		gp.setCurrentVotedTime(null);
		gp.setDatabaseSets(0);
		gp.setJoining(false);
		gp.setSpamKillCount(0);
		gp.setLastKiller(null);
		ScoreboardManager.getLobbyScoreboard().update(this, "players", String.valueOf(getPlayers().size()));
		ScoreboardManager.setLobbyScoreboard(player, this);
		for(LobbyItem lobbyItem : Lobby.getLobby().getLobbyItems().values()) {
			player.getInventory().setItem(lobbyItem.getSlot(), lobbyItem.getItem());
		}
		GameTeam.showTags(player);
		if(VaultManager.isEnabled()) {
			loadTags(player); //TODO CONFIG
		}
		if(!isCounting()) {
			if(getPlayersCount() >= ConfigMain.get().getInt("game.min-players-to-start")) {
				startCountDown();
			}
		}
		showPlayer(player);
	}
	
	private void loadTags(Player player) {
		
		boolean hasGroup = false;
		String pgroup = VaultManager.getPlayerGroup(player);
		if(!pgroup.equals(VaultManager.getDefaultGroup())) {
			hasGroup = true;
		}

		for(Player lp : getBukkitPlayers()) {
			if(hasGroup) {
				Utils.setTagPrefix(lp, player.getName(), pgroup, VaultManager.getTagGroupPrefix(pgroup));
			}
			if(GamePlayer.get(lp.getName()).getGameTeam() != null) {
				continue;
			}
			String group = VaultManager.getPlayerGroup(lp);
			if(group.equals(VaultManager.getDefaultGroup())) {
				continue;
			}
			Utils.setTagPrefix(player, lp.getName(), group, VaultManager.getTagGroupPrefix(group));
		}
	}
	
	public void startCountDown() {
		if(isCounting()) {
			return;
		}
		setCounting(true);
		new BukkitRunnable() {
			int count = ConfigMain.get().getInt("game.time-to-start");
			@Override
			public void run() {
				if(count == -1) {
					setCounting(false);
					cancel();
					return;
				}
				if(count == 0) {
					if(getPlayersCount() >= ConfigMain.get().getInt("game.min-players-to-start")) {
						playSound(Sound.NOTE_PLING, 2, (float) 1.6); //TODO CONFIG
						startGame();
						count--;
					}
					else {
						broadcast(ConfigLang.get().getString("lobby.need-more-players")
								.replaceAll("%required%", String.valueOf(ConfigMain.get().getInt("game.min-players-to-start"))));
						playSound(Sound.NOTE_BASS, 2, (float) 0.8); //TODO CONFIG
					}
					setCounting(false);
					cancel();
					return;
				}
				if(getPlayersCount() == TeamManager.getMaxPlayers()*TeamManager.getTeams().size() && 
						count > ConfigMain.get().getInt("game.time-to-start-full")) {
					broadcast(ConfigLang.get().getString("lobby.time-reduced")
							.replaceAll("%seconds%", String.valueOf(ConfigMain.get().getInt("game.time-to-start-full"))));
					count = ConfigMain.get().getInt("game.time-to-start-full");
				}
				if(count == 60 || count == 30 || count == 15 || count == 10) {
					broadcast(ConfigLang.get().getString("lobby.time-to-start").replaceAll("%seconds%", String.valueOf(count)));
				} 
				else if(count <=5) {
					broadcast(ConfigLang.get().getString("lobby.time-to-start-short").replaceAll("%seconds%", String.valueOf(count)));
					playSound(Sound.NOTE_PLING, 2, (float) 1.05); //TODO CONFIG
				}
				count--;
			}
		}.runTaskTimer(EggWarsCore.getPlugin(), 0L, 20L);
	}

	public void startGame() {
		started = true;
		game = new Game(this, getPlayers(), getVotedMap(), "clasic", getVotedTime()); //TODO cambiar classic por getVotedShop
		reset();
	}

	public String getVotedMap() {
		String votedMap = null;
		int votes = 0;
		for (String map : mapVotes.keySet()) {
			if (mapVotes.get(map) >= votes) {
				votes = mapVotes.get(map);
				votedMap = map;
			}
		}
		return votedMap;
	}
	
	public void updateMapVotes(GamePlayer gp, String mapName, int add) {
		Player player = gp.getPlayer();
		if(player == null) {
			return;
		}
		if(gp.getCurrentVotedMap() != null) {
			if(mapName.equals(gp.getCurrentVotedMap())) {
				player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1); //TODO CONF
				player.sendMessage(ChatUtils.format("&c¡Ya votaste por ese mapa!"));
				return;
			}
			else {
				updateMapVotes(gp.getCurrentVotedMap(), -add);
				gp.setCurrentVotedMap(mapName);
				updateMapVotes(mapName, add);
				player.playSound(player.getLocation(), Sound.CLICK, 10, 1); //TODO CONF
			}
		}
		else {
			gp.setCurrentVotedMap(mapName);
			updateMapVotes(mapName, add);
			player.playSound(player.getLocation(), Sound.CLICK, 10, 1); //TODO CONF
		}
		player.sendMessage(ChatUtils.format("&f¡Has votado por el mapa &b" + mapName + " &fy ahora tiene &a" 
				+ mapVotes.get(mapName) + " &fvotos!")); //TODO LANG
		if(gp.getMapVotesMultiplier() == 2) { //TODO CONFIG AND LANG
			player.sendMessage(ChatUtils.format("&e¡Tu voto ha valido &b&lx2 &egracias a tu rango &3&lMM&e!"));
		}
		else if(gp.getMapVotesMultiplier() == 3) {
			player.sendMessage(ChatUtils.format("&e¡Tu voto ha valido &b&lx3 &egracias a tu rango &b&lMMC&e!"));
		}
		else if(gp.getMapVotesMultiplier() == 4) {
			player.sendMessage(ChatUtils.format("&e¡Tu voto ha valido &b&lx4 &egracias a tu rango &a&lMMC&e&l+&e!"));
		}
	}
	
	public void updateMapVotes(String mapName, int add) {
		if(!mapVotes.containsKey(mapName)) {
			return;
		}
		if((mapVotes.get(mapName) + add) < 0) {
			return;
		}
		mapVotes.put(mapName, mapVotes.get(mapName) + add);
		List<String> mapsIndexed = new ArrayList<>(mapVotes.keySet());
		ScoreboardManager.getLobbyScoreboard().update(this, "map-votes-" + (mapsIndexed.indexOf(mapName)+1), 
				String.valueOf(mapVotes.get(mapName)));
		getMapVoteMenu().updateItem(mapName);
	}
	
	public String getVotedTime() {
		String votedTime = null;
		int votes = 0;
		for (String time : timeVotes.keySet()) {
			if (timeVotes.get(time) >= votes) {
				votes = timeVotes.get(time);
				votedTime = time;
			}
		}
		if(votes==0) {
			return "day";
		}
		return votedTime;
	}
	
	public void updateTimeVotes(GamePlayer gp, String timeName, int add) {
		if(!VaultManager.hasPermission(gp.getPlayer(), "ewc.vip2")) { //TODO CONFIG
			gp.getPlayer().sendMessage(ChatUtils.format("&c¡Debes ser &b&lMMC &co &a&lMMC&e&l+ &cpara poder votar por la Hora!" +
					" &fPuedes adquirir rango en &btienda.minemora.net")); //TODO LANG
			gp.getPlayer().playSound(gp.getPlayer().getLocation(), Sound.NOTE_BASS, 10, 1); //TODO CONF
			return;
		}
		if(gp.getCurrentVotedTime() != null) {
			if(timeName.equals(gp.getCurrentVotedTime())) {
				gp.getPlayer().playSound(gp.getPlayer().getLocation(), Sound.NOTE_BASS, 10, 1); //TODO CONF
				return;
			}
			else {
				updateTimeVotes(gp.getCurrentVotedTime(), -add);
				gp.setCurrentVotedTime(timeName);
				updateTimeVotes(timeName, add);
				gp.getPlayer().playSound(gp.getPlayer().getLocation(), Sound.CLICK, 10, 1); //TODO CONF
			}
		}
		else {
			gp.setCurrentVotedTime(timeName);
			updateTimeVotes(timeName, add);
			gp.getPlayer().playSound(gp.getPlayer().getLocation(), Sound.CLICK, 10, 1); //TODO CONF
		}
		//TODO añadir mensajes de lang.yml a cada caso
	}
	
	public void updateTimeVotes(String timeName, int add) {
		if(!timeVotes.containsKey(timeName)) {
			return;
		}
		if((timeVotes.get(timeName) + add) < 0) {
			return;
		}
		timeVotes.put(timeName, timeVotes.get(timeName) + add);
		getTimeVoteMenu().updateItem(timeName);
	}
	
	public boolean isFull() {
		int maxPlayers = TeamManager.getMaxPlayers()*TeamManager.getTeams().size();
		if(game!=null) {
			if(game.getPlayersCount() >= maxPlayers) {
				return true;
			}
		}
		else {
			if(getPlayersCount() >= maxPlayers) {
				return true;
			}
		}
		return false;
	}

	public Map<String, Integer> getMapVotes() {
		return mapVotes;
	}

	public String getDateString() {
		return dateString;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public boolean isCounting() {
		return counting;
	}

	private void setCounting(boolean counting) {
		this.counting = counting;
	}

	public Map<Integer,GameTeam> getGameTeams() {
		return gameTeams;
	}

	public TeamMenu getTeamMenu() {
		return teamMenu;
	}

	public MapVoteMenu getMapVoteMenu() {
		return mapVoteMenu;
	}

	public int getId() {
		return id;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public TimeVoteMenu getTimeVoteMenu() {
		return timeVoteMenu;
	}

	public Map<String, Integer> getTimeVotes() {
		return timeVotes;
	}
}