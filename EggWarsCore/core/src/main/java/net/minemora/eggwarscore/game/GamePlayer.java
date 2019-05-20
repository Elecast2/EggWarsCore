package net.minemora.eggwarscore.game;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.bungee.BungeeHandler;
import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.database.Stat;
import net.minemora.eggwarscore.game.GameLobby;
import net.minemora.eggwarscore.network.NetworkClient;
import net.minemora.eggwarscore.network.NetworkManager;
import net.minemora.eggwarscore.network.PacketQuickGame;
import net.minemora.eggwarscore.scoreboard.ScoreboardManager;
import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.shared.VaultManager;
import net.minemora.eggwarscore.team.TeamManager;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.Utils;

public class GamePlayer extends PlayerStats {

	private GameLobby gameLobby;
	private GameTeam gameTeam;
	private Game game;
	private boolean dead;
	private int currentKills = 0;
	private int currentDeaths = 0;
	private String lastDamager = null;
	private String lastKiller = null;
	private boolean invulnerable = false;
	private String currentVotedMap;
	private String currentVotedTime;
	private boolean joining;
	private long lastRespawnTime = 0;
	private int spamKillCount = 0;
	private int mapVotesMultiplier = 1;

	public GamePlayer(Player player) {
		super(player);
	}
	
	@Override
	public void loadPlayer(Player player) {
		
		if(!NetworkClient.getRegisteredPlayers().containsKey(player.getName())) {
			player.kickPlayer("Player not registered to this game");
			return;
		}
		
		if (!EggWarsCore.getPlugin().hasArenas()) {
			player.sendMessage(ChatUtils.format(ConfigLang.get().getString("arenas-setup.no-arenas")));
			return;
		}
		loadMultipliers(player);
		loadPlayerFromNetwork(player);
	}
	
	private void loadMultipliers(Player player) {  //TODO CONFIG
		if(VaultManager.hasPermission(player, "ewc.vip1")) {
			setMapVotesMultiplier(2);
		}
		if(VaultManager.hasPermission(player, "ewc.vip2")) {
			setMapVotesMultiplier(3);
		}
		if(VaultManager.hasPermission(player, "ewc.vip3")) {
			setMapVotesMultiplier(4);
		}
	}
	
	public void loadPlayerFromNetwork(Player player) {
		GameLobby gLobby = NetworkClient.getRegisteredPlayers().get(player.getName());
		NetworkClient.getRegisteredPlayers().remove(player.getName());
		if(gLobby.getGame()!=null) {
			gLobby.getGame().addSpectator(player);
		}
		else {
			gLobby.addPlayer(player);
		}
	}
	
	public void respawn(DamageCause cause) {
		updateCurrentDeaths();
		Player player = getPlayer();
		boolean hasKiller = false;
		boolean spamKill = false;
		if(lastDamager != null) {
			if(GamePlayer.get(lastDamager) != null) {
				Player killer = Bukkit.getPlayer(lastDamager);
				if(getLastKiller() != null) {
					if(getLastKiller().equals(lastDamager) && (System.currentTimeMillis() - getLastRespawnTime()) <= 
							(6000 + 1000*ConfigMain.get().getInt("general.respawn-protection-time"))) { //TODO FROM CONFIG
						setSpamKillCount(getSpamKillCount() + 1);
						if(getSpamKillCount() > 2) {
							spamKill = true;
							if(killer!=null) {
								//TODO LANG
								killer.sendMessage(ChatUtils.format("&c¡No hagas spam kill! &7(Las estadísticas de este asesinato no contarán)"));
							}
						}
					}
					else {
						setSpamKillCount(0);
					}
				}
				if(!spamKill) {
					GamePlayer.get(lastDamager).updateCurrentKills();
					game.broadcastKill(cause, player.getName(), lastDamager, false);
					if(killer!=null) {
						GamePlayer.get(lastDamager).addKill();
						GamePlayer.get(lastDamager).addExp(1); //TODO cantidad de exp configurable
					}
					getGame().updateTopKills(lastDamager);
				}
				hasKiller = true;
				setLastKiller(lastDamager);
				if(cause == DamageCause.ENTITY_ATTACK) {
					player.getWorld().playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
				}
			}
		}
		if(!hasKiller) {
			game.broadcastKill(cause, player.getName(), false);
		}
		addDeath();
		restore();
		player.playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1, 0.1f); //TODO CONFIG
		if(getDeathEffect() != null) {
			getDeathEffect().play(player);
		}
		if(getGame().getRespawnTime() == 0) {
			sendToTeamSpawn(player);
		}
		else {
			player.setGameMode(GameMode.SPECTATOR);
			if(cause == DamageCause.VOID) {
				if(hasKiller) {
					player.teleport(Bukkit.getPlayer(lastDamager).getLocation());	
				}
				else {
					player.teleport(game.getGameArena().getSpawnPoints().get(gameTeam.getTeam().getId()));
				}
			}
			new BukkitRunnable() {
				int count = 0;
				@Override
				public void run() {
					if(!player.isOnline()) {
						cancel();
						return;
					}
					if(getGame()==null) {
						cancel();
						return;
					}
					if(count == getGame().getRespawnTime()) {
						player.setGameMode(GameMode.SURVIVAL);
						sendToTeamSpawn(player);
						cancel();
						return;
					}
					else {
						SharedHandler.getNmsHandler().sendTitleToPlayer(player, 0, 25, 0, 
								ChatUtils.format("&3&l" + (getGame().getRespawnTime() - count)), "");
						//TODO LANG CONFIG
						//TODO SOUND CONFIG
					}
					count++;
				}
			}.runTaskTimer(EggWarsCore.getPlugin(), 5, 20);
		}
		setLastRespawnTime(System.currentTimeMillis());
		setLastDamager(null);
	}
	
	private void sendToTeamSpawn(Player player) {
		player.setVelocity(new Vector(0,0,0));
		player.setFallDistance(0);
		player.teleport(game.getGameArena().getSpawnPoints().get(gameTeam.getTeam().getId()));
		player.setFireTicks(0);
		if(getKit()!= null) {
			if(getCurrentDeaths() <= getKit().getResendTimes() || getKit().getResendTimes() == -1) {
				getKit().equip(player, getColor());
			}
		}
		if(ConfigMain.get().getBoolean("general.respawn-protection")) {
			setInvulnerable(true);
			new BukkitRunnable() {
				int seconds = ConfigMain.get().getInt("general.respawn-protection-time");
				@Override
				public void run() {
					if(seconds==0) {
						setInvulnerable(false);
						player.sendMessage(ChatUtils.format(ConfigLang.get().getString("game.respawn.time-over")));
						cancel();
						return;
					}
					player.sendMessage(ChatUtils.format(ConfigLang.get().getString("game.respawn.timer")
							.replaceAll("%seconds%", String.valueOf(seconds))));
					player.setFireTicks(0);
					seconds--;
				}
			}.runTaskTimer(EggWarsCore.getPlugin(), 0, 20L);
		}
	}
	
	public void setDead(DamageCause cause) {
		setDead(true);
		Player player = getPlayer();
		player.setGameMode(GameMode.SPECTATOR);
		restore();
		boolean hasKiller = false;
		if(lastDamager != null) {
			if(GamePlayer.get(lastDamager) != null) {
				GamePlayer.get(lastDamager).updateCurrentKills();
				game.broadcastKill(cause, player.getName(), lastDamager, true);
				if(Bukkit.getPlayer(lastDamager)!=null) {
					GamePlayer.get(lastDamager).addKill();
					GamePlayer.get(lastDamager).addExp(3); //TODO cantidad de exp configurable
				}
				getGame().updateTopKills(lastDamager);
				hasKiller = true;
			}
		}
		if(getDeathEffect() != null) {
			getDeathEffect().play(player);
		}
		if(cause == DamageCause.VOID) {
			if(hasKiller) {
				player.teleport(Bukkit.getPlayer(lastDamager).getLocation());	
			}
			else {
				player.teleport(game.getGameArena().getSpawnPoints().get(gameTeam.getTeam().getId()));
			}
		}
		if(!hasKiller) {
			game.broadcastKill(cause, player.getName(), true);
		}
		ScoreboardManager.getGameScoreboard().update(game, "team-alive-" + getGameTeam().getTeam().getId(), 
				String.valueOf(getGameTeam().getAliveCount()));
		addDeath();
		SharedHandler.getNmsHandler().sendTitleToPlayer(player, 20, 40, 20, 
				ChatUtils.format("&4&lHAS MUERTO"), hasKiller ? ChatUtils.format("&7Asesinado por: &c" + lastDamager) : "");
		//TODO LANG CONFIG
		//TODO SOUND CONFIG
		setLastDamager(null);
		sendGameClick();
		player.playSound(player.getLocation(), Sound.ENDERDRAGON_HIT, 1, 0.1f); //TODO CONFIG
		game.checkForWinners();
	}
	
	public void sendGameClick() { //TODO LANG
		Player player = getPlayer();
		String[] up = {
				"",
				"&6&l&m-----------------------------------------",
				"   &7Has click en alguna de las siguientes opciones:",
				""
		};		
		player.sendMessage(ChatUtils.format(up));
		TextComponent salir = new TextComponent(ChatUtils.format("        &c&lSALIR     "));
		salir.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/leave"));
		TextComponent nueva = new TextComponent(ChatUtils.format("     &a&lNUEVA PARTIDA"));
		nueva.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join"));
		player.spigot().sendMessage(salir, nueva);
		String[] down = {
				"",
				"",
				"&6&l&m-----------------------------------------"
		};		
		player.sendMessage(ChatUtils.format(down));
	}

	public void remove() {
		if(getMulticast() instanceof Game) {
			Game game = getGame();
			removeFromGame();
			if(!isDead()) {
				ScoreboardManager.getGameScoreboard().update(game, "team-alive-" + getGameTeam().getTeam().getId(), 
						String.valueOf(getGameTeam().getAliveCount()));
				game.broadcast(getPlayerName() + " &7ha abandonado la partida!"); //TODO LANG
			}
			game.checkForWinners();
		}
		else {
			if(gameLobby != null) {
				removeFromTeam();
				gameLobby.getPlayers().remove(getPlayerName());
				ScoreboardManager.getLobbyScoreboard().update(gameLobby, "players", String.valueOf(gameLobby.getPlayers().size()));
				gameLobby.broadcast(ConfigLang.get().getString("lobby.player-quit").replaceAll("%player%", getPlayerName())
						.replaceAll("%players%", String.valueOf(gameLobby.getPlayersCount()))
						.replaceAll("%max-players%", String.valueOf(TeamManager.getMaxPlayers()*TeamManager.getTeams().size())));
				if(getCurrentVotedMap() != null) {
					gameLobby.updateMapVotes(getCurrentVotedMap(), -1*getMapVotesMultiplier());
				}
				if(getCurrentVotedTime() != null) {
					gameLobby.updateTimeVotes(getCurrentVotedTime(), -1);
				}
			}
		}
		removePlayer();
		//TODO remover de todas listas necesarias aqui
	}
	
	public void removeFromGame() {
		removeFromTeam();
		game.getPlayers().remove(getPlayerName());
		game.getOpenEnderChests().remove(getPlayerName());
		setGame(null);
	}
	
	private void removeFromTeam() {
		if(gameTeam!=null) {
			gameTeam.removeTag(getPlayerName());
			gameTeam.getPlayers().remove(getPlayerName());
			if (getMulticast() instanceof GameLobby) {
				int fromTeamSlot = getGameTeam().getTeam().getId() - 1;
				gameLobby.getTeamMenu().updateItem(fromTeamSlot);
			}
			else {
				if(gameTeam.getPlayers().isEmpty()) {
					if(getGame() != null) {
						getGame().teamDisconnected(gameTeam.getTeam().getId());
					}
				}
			}
		}
	}
	
	public void restore() {
		Player player = Bukkit.getPlayer(getPlayerName());
		player.setFoodLevel(20);
		player.setExhaustion(0);
		player.setSaturation(20);
		player.setHealth(player.getMaxHealth());
		player.setFireTicks(0);
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		for (PotionEffect pe : player.getActivePotionEffects()) {
			player.removePotionEffect(pe.getType());
		}
		SharedHandler.getNmsHandler().removeArrows(player);
	}
	
	public void addKill() {
		setKills(getKills()+1);
		Database.add(Stat.KILLS, getPlayer(), 1);
	}
	
	public void addDeath() {
		setDeaths(getDeaths()+1);
		Database.add(Stat.DEATHS, getPlayer(), 1);
	}
	
	public void addWin() {
		setWins(getWins() +1);
		Database.add(Stat.WINS, getPlayer(), 1);	
	}
	
	public void addEggDestroyed() {
		setDestroyedEggs(getDestroyedEggs() +1);
		Database.add(Stat.DESTROYED_EGGS, getPlayer(), 1);
	}
	
	public void updateCurrentKills() {
		setCurrentKills(getCurrentKills() + 1);
		ScoreboardManager.getGameScoreboard().update(getPlayer(), "current-kills", String.valueOf(getCurrentKills()));
	}
	
	public void updateCurrentDeaths() {
		setCurrentDeaths(getCurrentDeaths() + 1);
	}
	
	public Multicast getMulticast() {
		if(game!=null) {
			return game;
		}
		else {
			return gameLobby;
		}
	}
	
	public static void sendToLobby(Player player) {
		Set<String> lobbies =  ConfigMain.get().getConfigurationSection("network.lobbies").getValues(false).keySet();
		String lobby = Utils.choice(lobbies);
		BungeeHandler.sendPlayer(player, ConfigMain.get().getString("network.lobbies." + lobby + ".bungee-name"));
	}
	
	public static void sendToQuickGame(Player player) {
		player.sendMessage(ChatUtils.format("&aBuscando partida...")); //TODO LANG
		NetworkClient client = Utils.choice(NetworkManager.getConnections());
		new PacketQuickGame(client.getWriter(), player.getName()).send();
	}
	
	public Color getColor() {
		return Utils.chatColorToColor(getGameTeam().getTeam().getColor());
	}
	
	public String getColouredName() {
		if(gameTeam == null) {
			EggWarsCore.getPlugin().getLogger().warning(getPlayerName() + " doesn't have team and is in a game!");
			return getPlayerName();
		}
		return gameTeam.getTeam().getColor() + getPlayerName();
	}

	public GameLobby getGameLobby() {
		return gameLobby;
	}

	public void setGameLobby(GameLobby gameLobby) {
		this.gameLobby = gameLobby;
	}
	
	public static GamePlayer get(String name) {
		return (GamePlayer) PlayerStats.get(name);
	}

	public GameTeam getGameTeam() {
		return gameTeam;
	}

	public void setGameTeam(GameTeam gameTeam) {
		this.gameTeam = gameTeam;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public int getCurrentKills() {
		return currentKills;
	}

	public void setCurrentKills(int currentKills) {
		this.currentKills = currentKills;
	}

	public String getLastDamager() {
		return lastDamager;
	}

	public void setLastDamager(String lastDamager) {
		this.lastDamager = lastDamager;
	}

	public boolean isInvulnerable() {
		return invulnerable;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public String getCurrentVotedMap() {
		return currentVotedMap;
	}

	public void setCurrentVotedMap(String currentVotedMap) {
		this.currentVotedMap = currentVotedMap;
	}

	public int getCurrentDeaths() {
		return currentDeaths;
	}

	public void setCurrentDeaths(int currentDeaths) {
		this.currentDeaths = currentDeaths;
	}

	public boolean isJoining() {
		return joining;
	}

	public void setJoining(boolean joining) {
		this.joining = joining;
	}

	@Override
	public void updateMoneySB(Player player) {
		if(getMulticast() instanceof GameLobby) {
			ScoreboardManager.getLobbyScoreboard().update(player, "money", String.valueOf(getMoney()));
		}
	}

	public String getLastKiller() {
		return lastKiller;
	}

	public void setLastKiller(String lastKiller) {
		this.lastKiller = lastKiller;
	}

	public long getLastRespawnTime() {
		return lastRespawnTime;
	}

	public void setLastRespawnTime(long lastRespawnTime) {
		this.lastRespawnTime = lastRespawnTime;
	}

	public int getSpamKillCount() {
		return spamKillCount;
	}

	public void setSpamKillCount(int spamKillCount) {
		this.spamKillCount = spamKillCount;
	}

	public String getCurrentVotedTime() {
		return currentVotedTime;
	}

	public void setCurrentVotedTime(String currentVotedTime) {
		this.currentVotedTime = currentVotedTime;
	}

	public int getMapVotesMultiplier() {
		return mapVotesMultiplier;
	}

	public void setMapVotesMultiplier(int mapVotesMultiplier) {
		this.mapVotesMultiplier = mapVotesMultiplier;
	}
}