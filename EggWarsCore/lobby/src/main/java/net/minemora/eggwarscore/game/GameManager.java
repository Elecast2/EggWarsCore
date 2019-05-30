package net.minemora.eggwarscore.game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.bukkit.entity.Player;

import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.menu.GamesMenu;
import net.minemora.eggwarscore.network.GamesConnection;
import net.minemora.eggwarscore.network.PacketSendPlayer;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.Utils;

public final class GameManager {
	
	//TODO que cada modo sea un objeto separado
	private static Map<String,SortedSet<GamesConnection>> games = new HashMap<>();
	private static Map<String,GamesMenu> gamesMenus = new HashMap<>();
	private static Map<String,Game> sendQueue = new HashMap<>();
	private static Map<String,Integer> maxPlayersPerMode = new HashMap<>();
	private static Map<String,Integer> modesId = new HashMap<>();
	private static Map<String,String> modesDisplayName = new HashMap<>();
	
	private static Map<String,GamesConnection> quickPlayersFrom = new HashMap<>();
	
	private GameManager() {}
	
	public static void setup() {
		for(String mode : getModes()) {
			maxPlayersPerMode.put(mode, ConfigMain.get().getInt("modes." + mode + ".max-players"));
			modesId.put(mode, ConfigMain.get().getInt("modes." + mode + ".id"));
			modesDisplayName.put(mode, ChatUtils.format(ConfigMain.get().getString("modes." + mode + ".item.display-name"))); //TODO FROM other CONFIG
		}
	}
	
	public static Set<String> getModes(){
		return ConfigMain.get().getConfigurationSection("modes").getValues(false).keySet();
	}
	
	public static int getMaxPlayers(String mode) {
		if(maxPlayersPerMode.containsKey(mode)) {
			return maxPlayersPerMode.get(mode);
		}
		else {
			return 16;
		}
	}
	
	public static void attemptToSendPlayer(Player player, Game game) {
		if(game == null) {
			return;
		}
		if(game.isRestarting()) {
			player.sendMessage("Esta partida se esta reiniciando..."); //TODO mensaje configurable
			return;
		}
		if(game.getPlayerCount() >= GameManager.getMaxPlayers(game.getMode())) {
			player.sendMessage("Partida llena"); //TODO mensaje configurable
			return;
		}
		sendQueue.put(player.getName(), game);
		new PacketSendPlayer(game.getConnection().getWriter(), player.getName(), game.getId()).send();
	}
	
	public static Game getQuickGame(String mode) {
		Game togame = null;
		int pn = 0;
		if(!games.containsKey(mode)) {
			return null;
		}
		for(GamesConnection connection : games.get(mode)) {
			for(Game game : connection.getGames().values()) {
				if(game.isInGame()) {
					continue;
				}
				if(game.isRestarting()) {
					continue;
				}
				int pnl = game.getPlayerCount();
				if(pnl < game.getMaxPlayers() && pnl >= 0 && pnl >= pn) {
					pn = pnl;
					togame = game;
				}
			}
		}
		
		if(togame == null) {
			return null;
		}

		if(togame.getPlayerCount()==0) {
			Set<Game> randomgames = new HashSet<>();
			for(GamesConnection connection : games.get(mode)) {
				for(Game game : connection.getGames().values()) {
					if(game.getPlayerCount() == 0) {
		  				if(!game.isRestarting()) {
		  					randomgames.add(game);
		  				}
					}
				}
			}
			togame = Utils.choice(randomgames);
		}
		return togame;
		
	}
	
	public static String getModeDisplayName(int mode) {
		return modesDisplayName.get(getMode(mode));
	}
	
	public static String getMode(int i) {
		for(String mode : modesId.keySet()) {
			if(modesId.get(mode) == i) {
				return mode;
			}
		}
		return null;
	}

	public synchronized static Map<String,SortedSet<GamesConnection>> getGames() {
		return games;
	}

	public static Map<String,Game> getSendQueue() {
		return sendQueue;
	}

	public static Map<String,GamesMenu> getGamesMenus() {
		return gamesMenus;
	}

	public static Map<String,Integer> getMaxPlayersPerMode() {
		return maxPlayersPerMode;
	}

	public static Map<String,GamesConnection> getQuickPlayersFrom() {
		return quickPlayersFrom;
	}

	public static Map<String,Integer> getModesId() {
		return modesId;
	}	
}