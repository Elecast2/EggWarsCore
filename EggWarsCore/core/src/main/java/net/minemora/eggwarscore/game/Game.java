package net.minemora.eggwarscore.game;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.lobby.Lobby;
import net.minemora.eggwarscore.network.PacketGameUpdate;
import net.minemora.eggwarscore.reportsystem.ReportSystemHook;
import net.minemora.eggwarscore.scoreboard.ScoreboardManager;
import net.minemora.eggwarscore.shop.Offer;
import net.minemora.eggwarscore.shop.Shop;
import net.minemora.eggwarscore.shop.ShopManager;
import net.minemora.eggwarscore.shop.ShopSection;
import net.minemora.eggwarscore.team.TeamManager;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.Utils;
import net.minemora.reportsystem.ReportSystemAPI;
import net.minemora.reportsystem.util.ParticleEffect;
import net.minemora.reportsystem.util.UtilParticles;

public class Game extends Multicast {
	
	private Set<Block> placedBlocks = new HashSet<>();
	private String dateString;
	private GameLobby gameLobby;

	private static int id = 0;

	private GameArena gameArena;
	private Shop shop;
	private int gameId;
	private Map<Integer,GameTeam> gameTeams = new HashMap<>();
	private boolean ending = false;
	private Map<String,Integer> topKills = new HashMap<>();
	
	private Map<String,Location> openEnderChests = new HashMap<>();
	
	private int timeElapsed = 0;
	private int respawnTime = 0;
	
	private static Set<Game> games = new HashSet<>();;

	public Game(GameLobby gameLobby, Set<String> players, String worldName, String shopType, String votedTime) {
		Date date = Calendar.getInstance().getTime();
        DateFormat formatter = new SimpleDateFormat(ConfigMain.get().getString("general.date-format"));
        dateString = formatter.format(date);
		setPlayers(players);
		this.gameLobby = gameLobby;
		gameId = id;
		id++;
		if(id == 160) { //TODO CONFIGURABLE
			GameManager.setSoftRestarting(true);
		}
		gameArena = new GameArena(this, worldName, votedTime);
		shop = ShopManager.getShops().get(shopType);
		gameTeams.putAll(gameLobby.getGameTeams());
		for(Player player : getBukkitPlayers()) {
			GamePlayer gp = GamePlayer.get(player.getName());
			gp.setGame(this);
			gp.setCurrentKills(0);
			gp.setCurrentDeaths(0);
			gp.setLastDamager(null);
			if(gp.getGameTeam() == null) {
				addToRandomTeam(player.getName());
			}
		}
		teleportPlayers();
		destroyEmptyTeams();
		ScoreboardManager.setGameScoreboard(this);
		for(GameTeam team : gameTeams.values()) {
			for(String name : team.getPlayers()) {
				team.setTag(name);
			}
		}
		games.add(this);
		startTimer();
	}
	
	private void teleportPlayers() {
		Map<Integer,Player> enPlayers = new HashMap<>();
		int i = 0;
		for(Player player : getBukkitPlayers()) {
			enPlayers.put(i, player);
			i++;
		}
		new BukkitRunnable() {
        	int i2 = 0;
			@Override
            public void run() {
				teleportPlayer(enPlayers.get(i2));
				i2++;
				if(i2 == enPlayers.size()) {
					cancel();
					return;
				}
			}
		}.runTaskTimer(EggWarsCore.getPlugin(), 5, 2);
	}
	
	private void teleportPlayer(Player player) {
		if(player == null) {
			return;
		}
		GamePlayer gp = GamePlayer.get(player.getName());
		if(gp == null) {
			return;
		}
		Location loc = gameArena.getSpawnPoints().get(gp.getGameTeam().getTeam().getId());
		World world = loc.getWorld();
		if (!world.isChunkLoaded(world.getChunkAt(loc))) {
			world.loadChunk(world.getChunkAt(loc));
		}
		player.teleport(loc);
		boolean gameMode = true;
		if(ReportSystemHook.isEnabled()) {
			if(ReportSystemAPI.isSpy(player.getName())) {
				gameMode = false;
			}
		}
		if(gameMode) {
			player.setGameMode(GameMode.SURVIVAL);
		}
		gp.restore();
		if(gp.getKit()!= null) {
			gp.getKit().equip(player, gp.getColor());
		}
	}
	
	private void addToRandomTeam(String playerName) {
		GameTeam toAdd = null;
		int minPlayers = 0;
		
		for(GameTeam gameTeam : gameTeams.values()) {
			if(gameTeam.getPlayers().size() >= TeamManager.getMaxPlayers()) {
				continue;
			}
			if(toAdd == null) {
				toAdd = gameTeam;
				minPlayers = toAdd.getPlayers().size();
			}
			if(gameTeam.getPlayers().size() < toAdd.getPlayers().size()) {
				toAdd = gameTeam;
				minPlayers = toAdd.getPlayers().size();
			}
		}
		
		Set<GameTeam> toSelect = new HashSet<>();
		for(GameTeam gameTeam : gameTeams.values()) {
			if(gameTeam.getPlayers().size() == minPlayers) {
				toSelect.add(gameTeam);
			}
		}
		
		if(toSelect.size() <= 1) {
			if(toAdd != null) {
				toAdd.addPlayerOnly(playerName);
				return;
			}
		}
		
		GameTeam team = Utils.choice(toSelect);
		
		if(team == null) {
			Utils.choice(gameTeams.values()).addPlayerOnly(playerName);
			return;
		}
		if(team.getPlayers().size() < TeamManager.getMaxPlayers()) {
			team.addPlayerOnly(playerName);
		}
		else {
			Utils.choice(gameTeams.values()).addPlayerOnly(playerName);
		}	
	}
	
	private void startTimer() {
		int firstAnnounceShop = 0;
		for(ShopSection section : shop.getTimedOffers().keySet()) {
			for(Offer offer : shop.getTimedOffers().get(section)) {
				if(firstAnnounceShop == 0) {
					firstAnnounceShop = offer.getTime();
				}
				if(offer.getTime() < firstAnnounceShop) {
					firstAnnounceShop = offer.getTime();
				}
			}
		}
		int firstAnnounceRespawn = 0;
		for(int time : GameManager.getRespawnTimes().keySet()) {
			if(firstAnnounceRespawn == 0) {
				firstAnnounceRespawn = time;
			}
			if(time < firstAnnounceRespawn) {
				firstAnnounceRespawn = time;
			}
		}
		final int fas = firstAnnounceShop - 60;
		final int far = firstAnnounceRespawn - 60;
		new BukkitRunnable() {
			@Override
			public void run() {
				if(ending) {
					cancel();
					return;
				}
				timeElapsed++;
				ScoreboardManager.getGameScoreboard().updateTitle(getBukkitPlayers(), "timer", 
						ChatUtils.formatTime(timeElapsed));
				
				if(timeElapsed == fas) {
					sendTitle("",ChatUtils.format("&7Próxima actualización de la tienda: &f" + ChatUtils.formatTime(60))); //TODO LANG
					playSound(Sound.NOTE_PIANO, 10, 1.3f); //TODO CONFIG
				}
				
				if(timeElapsed == far) {
					sendTitle("",ChatUtils.format("&7Siguiente cambio en la reaparición: &f" + ChatUtils.formatTime(60))); //TODO LANG
					playSound(Sound.NOTE_PIANO, 10, 1.3f); //TODO CONFIG
				}
				
				boolean announceShop = false;
				for(ShopSection section : shop.getTimedOffers().keySet()) {
					for(Offer offer : shop.getTimedOffers().get(section)) {
						if(timeElapsed == offer.getTime()) {
							broadcastMessage(ChatUtils.format("&e&lTIENDA ACTUALIZADA &aSe han añadido &b" + offer.getName() 
							+ " &aa la seccion " + section.getName() + " &apor " + offer.getFormattedPrice()));//TODO LANG
							//TODO from CONFIG
							playSound(Sound.LEVEL_UP, 10, 1.3f);
							for(Location loc : gameArena.getShops()) {
								loc.getWorld().playSound(loc, Sound.VILLAGER_YES, 2, 1.3f);
								UtilParticles.display(ParticleEffect.VILLAGER_HAPPY, loc);
							}
							announceShop = true;
						}
					}
				}
				if(announceShop) { //TODO CONFIG IF ANNOUNCE
					Offer nextOffer = null;
					for(ShopSection section : shop.getTimedOffers().keySet()) {
						for(Offer offer : shop.getTimedOffers().get(section)) {
							if(nextOffer == null) {
								if(offer.getTime() > timeElapsed) {
									nextOffer = offer;
								}
							}
							if((offer.getTime() > timeElapsed) && (offer.getTime() < nextOffer.getTime())) {
								nextOffer = offer;
							}
						}
					}
					if(nextOffer != null) {
						sendTitle(ChatUtils.format("&eTienda Actualizada"),ChatUtils.format("&7Próxima actualización de la tienda: &f" 
								+ ChatUtils.formatTime(nextOffer.getTime() - timeElapsed))); //TODO LANG
					}
					else {
						sendTitle(ChatUtils.format(ChatUtils.format("&eTienda Actualizada")),""); //TODO LANG
					}
				}
				boolean announceTime = false;
				for(int time : GameManager.getRespawnTimes().keySet()) {
					if(timeElapsed == time) {
						respawnTime = GameManager.getRespawnTimes().get(time);
						broadcast(ChatUtils.format("&c&l¡AVISO! &6&LSe ha cambiado el tiempo de reaparición a &c&l" 
								+ GameManager.getRespawnTimes().get(time) +" &6&lsegundos"));//TODO LANG
						//TODO SOUND CONFIG
						playSound(Sound.NOTE_PIANO, 10, 1.3f);
						announceTime = true;
					}
				}
				if(announceTime) { //TODO CONFIG IF ANNOUNCE
					int nextTime = 0;
					for(int time : GameManager.getRespawnTimes().keySet()) {
						if(nextTime == 0) {
							if(time > timeElapsed) {
								nextTime = time;
							}
						}
						if((time > timeElapsed) && (time < nextTime)) {
							nextTime = time;
						}
					}
					if(nextTime != 0) {
						sendTitle(ChatUtils.format("&6Cambio en la reaparición"),ChatUtils.format("&7Siguiente cambio en la reaparición: &f" 
								+ ChatUtils.formatTime(nextTime - timeElapsed))); //TODO LANG
					}
					else {
						sendTitle(ChatUtils.format("&eTienda Actualizada"),""); //TODO LANG
					}
				}
				for(Player player : getBukkitPlayers()) {
					Player np = getNearestPlayer(player);
    				if(np!=null) {
    					player.setCompassTarget(np.getLocation()); //TODO IF ENABLED ON CONFIG
    				}
				}
			}
		}.runTaskTimerAsynchronously(EggWarsCore.getPlugin(), 20, 20);
	}
	
	public void checkForWinners() {
		if(ending) {
			return;
		}
		GameTeam teamWinner = null;
		int teamsAlive = 0;
		for(GameTeam gameTeam : gameTeams.values()) {
			for(String playerName : gameTeam.getPlayers()) {
				GamePlayer gp = GamePlayer.get(playerName);
				if(gp != null && !gp.isDead()) {
					teamWinner = gameTeam;
					teamsAlive++;
					break;
				}
			}
		}
		if(teamsAlive > 1) {
			return;
		}
		endGame(teamWinner);
	}
	
	private void endGame(GameTeam teamWinner) {
		ending = true;
		if(teamWinner==null) {
			removeGame();
			return;
		}
		broadcast("&f¡El equipo " + teamWinner.getTeam().getFinalName() + "&f ha ganado la partida!");//TODO lang
		for(String playerName : teamWinner.getPlayers()) {
			GamePlayer gp = GamePlayer.get(playerName);
			if(gp==null) {
				continue;
			}
			gp.addWin();
			gp.addExp(10);
			if(!gp.isDead()) {
				if(gp.getWinEffect()!=null) {
					gp.getWinEffect().play(gp.getPlayer()); //TODO algun bug causa que esto se ejctue en alguien en partida sin terminar
				}
			}
		}
		if(GameManager.isTournamentMode()) {
			for(String playerName : teamWinner.getPlayers()) {
				TournamentManager.sendGameUpdate(PacketGameUpdate.StatType.TEAM_WIN, playerName, "null");
				break;
			}
		}
		broadcastTopKills(); //TODO IF TOP IS ENABLED IN CONFIG THEN LOAD AND SHOW TOP
		new BukkitRunnable() {
			int count = 10; //TODO from config
			@Override
			public void run() {
				if(count==0) {
					cancel();
					removeGame();
					return;
				}
				broadcast("&7Una nueva partida iniciara en " + count);//TODO lang
				count--;
			}
		}.runTaskTimer(EggWarsCore.getPlugin(), 20L, 20L);
	}
	
	private void broadcastTopKills() {
		LinkedHashMap<String, Integer> sortedTopKills = Utils.sort(topKills);
		broadcastMessage(ChatUtils.format("&bo&3------------&7[ &b&lTOP ASESINATOS &7]&3------------&bo")); //TODO HEADER FROM LANG
		int range = 3; //TODO FROM CONFIG
		if(sortedTopKills.size()<range) {
			range = sortedTopKills.size();
		}
		for(int i = 0; i < range; i++) { //TODO max top from config
			String name = (String) sortedTopKills.keySet().toArray()[i];
			int kills = (int) sortedTopKills.values().toArray()[i];
			broadcastMessage(ChatUtils.format("  &e&lLugar #" + (i+1) + ": &f" + name + " &8- &c" + kills + " &7Asesinatos")); //TODO LANG
		}
		broadcastMessage(ChatUtils.format("&bo&3------------------------------------------&bo")); //TODO FOOTER FROM LANG
	}
	
	private void removeGame() {
		placedBlocks.clear();
		openEnderChests.clear();
		games.remove(this);
		gameLobby.setGame(null);
		Set<String> players = new HashSet<>(getPlayers());
		if(GameManager.isSoftRestarting()) {
			for(String playerName : players) {
				if(Bukkit.getPlayer(playerName) == null) {
					continue;
				}
				Bukkit.getPlayer(playerName).teleport(Lobby.getLobby().getSpawn());
				GamePlayer.sendToLobby(Bukkit.getPlayer(playerName));;
			}
			boolean restart = true;
			for(GameLobby gameLobby : GameManager.getGames().values()) {
				if(gameLobby.getGame() != null) {
					restart = false;
				}
			}
			if(restart) {
				Bukkit.shutdown();
			}
		}
		else {
			for(String playerName : players) {
				if(Bukkit.getPlayer(playerName) == null) {
					continue;
				}
				if(GameManager.isTournamentMode()) {
					Bukkit.getPlayer(playerName).teleport(Lobby.getLobby().getSpawn());
					GamePlayer.sendToLobby(Bukkit.getPlayer(playerName));;
				}
				else {
					GamePlayer.get(playerName).setGame(null);
					gameLobby.addPlayer(Bukkit.getPlayer(playerName));
				}
			}
		}
		gameArena.unloadWorld();
	}
	
	public void addSpectator(Player player) {
		if(GameManager.isTournamentMode()) {
			for(GameTeam team : gameTeams.values()) {
				if(team.getDisconnectedPlayers().contains(player.getName())) {
					if(team.getAliveCount() > 0) {
						team.getDisconnectedPlayers().remove(player.getName());
						GamePlayer gp = GamePlayer.get(player.getName());
						getPlayers().add(player.getName());
						gp.setGame(this);
						gp.setCurrentKills(0);
						gp.setCurrentDeaths(0);
						gp.setLastDamager(null);
						ScoreboardManager.setGameScoreboard(player, this);
						GameTeam.showTags(player);
						team.addPlayerOnly(player.getName());
						team.setTag(player.getName());
						teleportPlayer(player);
						revealPlayersToPlayer(player);
						for(Player lp : getBukkitPlayers()) {
							if(lp.equals(player)) {
								continue;
							}
							lp.showPlayer(player);
						}
						return;
					}
				}
			}
			
		}
		GamePlayer gp = GamePlayer.get(player.getName());
		getPlayers().add(player.getName());
		player.setGameMode(GameMode.SPECTATOR);
		boolean teleport = true;
		if(ReportSystemHook.isEnabled()) {
			ReportSystemAPI.processQueue(player);
			if(ReportSystemAPI.isSpy(player.getName())) {
				teleport = false;
			}
		}
		if(teleport) {
			player.teleport(gameArena.getSpecSpawn());
		}
		//TODO mensaje de bienvenida como espectador
		gp.restore();
		gp.setDead(true);
		gp.setGame(this);
		gp.setCurrentKills(0);
		ScoreboardManager.setGameScoreboard(player, this);
		revealPlayersToPlayer(player);
		GameTeam.showTags(player);
		gp.sendGameClick();
	}
	
	public void destroyBlock(int id, String destroyer) {
		if(gameTeams.get(id).getPlayers().contains(destroyer)) {
			Bukkit.getPlayer(destroyer).sendMessage(ChatUtils.format(ConfigLang.get().getString("game.block.own-block")));
			return;
		}
		Block block = getGameArena().getBlocksToDestroy().get(id).getBlock();
		block.setType(Material.AIR);
		GameTeam gameTeam = getGameTeams().get(id);
		gameTeam.setEggDestroyed(true);
		gameTeam.sendTitle(ChatUtils.format("&c&lTU HUEVO HA SIDO ROTO"), ChatUtils.format("&7Ahora eres vulnerable")); //TODO LANG
		gameTeam.playSound(Sound.WITHER_SPAWN, 1, 1); //TODO CONFIG
		broadcast(ConfigLang.get().getString("game.block.destroyed")
			.replaceAll("%player%", destroyer)
			.replaceAll("%enemy-team%", gameTeam.getTeam().getColor() + gameTeam.getTeam().getName())
			.replaceAll("%player-team%", GamePlayer.get(destroyer).getGameTeam().getTeam().getColor() 
					+ GamePlayer.get(destroyer).getGameTeam().getTeam().getName()));
		playSound(Sound.ENDERDRAGON_GROWL, 1, 1);//TODO configurable
		block.getWorld().strikeLightningEffect(block.getLocation()); //TODO check if enabled on config
		ScoreboardManager.getGameScoreboard().update(this, "symbol-" + id, ScoreboardManager.getDeathSymbol());
		GamePlayer.get(destroyer).addEggDestroyed();
		GamePlayer.get(destroyer).addExp(5); //TODO cantidad de exp configurable
		if(GameManager.isTournamentMode()) {
			TournamentManager.sendGameUpdate(PacketGameUpdate.StatType.DESTROY_EGG, destroyer, "null");
		}
	}
	
	public void teamDisconnected(int id) {
		if(ending) {
			return;
		}
		Block block = getGameArena().getBlocksToDestroy().get(id).getBlock();
		block.setType(Material.AIR);
		getGameTeams().get(id).setEggDestroyed(true);
		ScoreboardManager.getGameScoreboard().update(this, "symbol-" + id, ScoreboardManager.getDeathSymbol());
	}
	
	public static boolean evaluateDamage(EntityDamageEvent event, Player victim, Player damager) {
		if(GamePlayer.get(damager.getName()).isInvulnerable()) {
			event.setCancelled(true);
			return true;
		}
		GamePlayer gp = GamePlayer.get(victim.getName());
		if(gp.isInvulnerable()) {
			event.setCancelled(true);
			return true;
		}
		if(gp.getGameTeam().equals(GamePlayer.get(damager.getName()).getGameTeam())) {
			if(damager.getName().equals(victim.getName())) {
				return false;
			}
			event.setCancelled(true);
			return true;
		}
		else {
			gp.setLastDamager(damager.getName());
			gp.setLastTimeDamagedByPlayer(System.currentTimeMillis());
			return false;
		}
	}
	
	public void broadcastKill(DamageCause cause, String victim, boolean ultimate) {
		String add = "";
		if(ultimate) {
			add = " " + ConfigLang.get().getString("game.death-messages.final-death");
		}
		String text = null;
		switch(cause) {
		case BLOCK_EXPLOSION:
			text = ConfigLang.get().getString("game.death-messages.explosion-self") + add;
			break;
		case ENTITY_EXPLOSION:
			text = ConfigLang.get().getString("game.death-messages.explosion-self") + add;
			break;
		case DROWNING:
			text = ConfigLang.get().getString("game.death-messages.drowning-self") + add;
			break;
		case FALL:
			text = ConfigLang.get().getString("game.death-messages.fall-self") + add;
			break;
		case FIRE:
			text = ConfigLang.get().getString("game.death-messages.fire-self") + add;
			break;
		case FIRE_TICK:
			text = ConfigLang.get().getString("game.death-messages.fire-self") + add;
			break;
		case LAVA:
			text = ConfigLang.get().getString("game.death-messages.lava-self") + add;
			break;
		case VOID:
			text = ConfigLang.get().getString("game.death-messages.void-self") + add;
			break;
		default:
			text = ConfigLang.get().getString("game.death-messages.default-self") + add;
			break;
		}
		broadcast(text.replaceAll("%victim%", GamePlayer.get(victim).getColouredName()));
	}
	
	public void broadcastKill(DamageCause cause, String victim, String killer, boolean ultimate) {
		String add = "";
		if(ultimate) {
			add = " " + ConfigLang.get().getString("game.death-messages.final-death");
		}
		String text = null;
		switch(cause) {
		case BLOCK_EXPLOSION:
			text = ConfigLang.get().getString("game.death-messages.explosion") + add;
			break;
		case ENTITY_EXPLOSION:
			text = ConfigLang.get().getString("game.death-messages.explosion") + add;
			break;
		case DROWNING:
			text = ConfigLang.get().getString("game.death-messages.drowning") + add;
			break;
		case ENTITY_ATTACK:
			text = ConfigLang.get().getString("game.death-messages.attack") + add;
			break;
		case FALL:
			text = ConfigLang.get().getString("game.death-messages.fall") + add;
			break;
		case FIRE:
			text = ConfigLang.get().getString("game.death-messages.fire") + add;
			break;
		case FIRE_TICK:
			text = ConfigLang.get().getString("game.death-messages.fire") + add;
			break;
		case LAVA:
			text = ConfigLang.get().getString("game.death-messages.lava") + add;
			break;
		case MAGIC:
			text = ConfigLang.get().getString("game.death-messages.magic") + add;
			break;
		case POISON:
			text = ConfigLang.get().getString("game.death-messages.poison") + add;	
			break;
		case PROJECTILE:
			text = ConfigLang.get().getString("game.death-messages.projectile") + add;
			break;
		case VOID:
			text = ConfigLang.get().getString("game.death-messages.void") + add;
			break;
		default:
			text = ConfigLang.get().getString("game.death-messages.default") + add;
			break;
		}
		broadcast(text.replaceAll("%victim%", GamePlayer.get(victim).getColouredName())
				.replaceAll("%killer%", GamePlayer.get(killer).getColouredName()));
	}
	
	private void destroyEmptyTeams() {
		for(GameTeam team : getGameTeams().values()) {
			if(team.getPlayers().isEmpty()) {
				team.setEggDestroyed(true);
				getGameArena().getBlocksToDestroy().get(team.getTeam().getId()).getBlock().setType(Material.AIR);
			}
		}
	}
	
	public static Player getNearestPlayer(Player checkNear) {
	    Player nearest = null;
	    for (Player p : checkNear.getWorld().getPlayers()) {
	    	if(!p.equals(checkNear)) {
	    		if(GamePlayer.get(p.getName())!=null) {
	    			if(!GamePlayer.get(p.getName()).isDead()) {
	    				if(GamePlayer.get(p.getName()).getGameTeam()==null) {
	    					return null;
	    				}
		    			if(!GamePlayer.get(p.getName()).getGameTeam().getPlayers().contains(checkNear.getName())) {
		    				if (nearest == null) { 
					        	nearest = p;
					        }
					        else if (p.getLocation().distance(checkNear.getLocation()) < 
					        		nearest.getLocation().distance(checkNear.getLocation())) {
					        	nearest = p;
					        }
		    			}
			    	}
	    		}
	    	}
	    }
	    return nearest;
	}
	
	public static Game getByWorldName(String worldName) {
		for(Game game : games) {
			if(game.getGameArena().getLoadedWorldName().equals(worldName)) {
				return game;
			}
		}
		return null;
	}
	
	public void updateTopKills(String killer) {
		if(getTopKills().containsKey(killer)) {
			getTopKills().put(killer, getTopKills().get(killer)+1);
		}
		else {
			getTopKills().put(killer, 1);
		}
	}
	
	public GameArena getGameArena() {
		return gameArena;
	}

	public void setGameArena(GameArena gameArena) {
		this.gameArena = gameArena;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	
	public boolean isEnding() {
		return ending;
	}

	public void setEnding(boolean ending) {
		this.ending = ending;
	}

	public Map<Integer,GameTeam> getGameTeams() {
		return gameTeams;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}
	
	public Set<Block> getPlacedBlocks() {
		return placedBlocks;
	}
	
	public String getDateString() {
		return dateString;
	}

	public static Set<Game> getGames() {
		return games;
	}

	public GameLobby getGameLobby() {
		return gameLobby;
	}

	public void setGameLobby(GameLobby gameLobby) {
		this.gameLobby = gameLobby;
	}

	public int getTimeElapsed() {
		return timeElapsed;
	}

	public void setTimeElapsed(int timeElapsed) {
		this.timeElapsed = timeElapsed;
	}

	public int getRespawnTime() {
		return respawnTime;
	}

	public void setRespawnTime(int respawnTime) {
		this.respawnTime = respawnTime;
	}

	public Map<String,Location> getOpenEnderChests() {
		return openEnderChests;
	}

	public Map<String,Integer> getTopKills() {
		return topKills;
	}
}