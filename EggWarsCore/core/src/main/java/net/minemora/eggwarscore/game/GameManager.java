package net.minemora.eggwarscore.game;

import java.util.HashMap;
import java.util.Map;

import net.minemora.eggwarscore.config.ConfigMain;

public final class GameManager {
	
	private static Map<Integer, GameLobby> games = new HashMap<>();
	
	private static Map<Integer, Integer> respawnTimes = new HashMap<>();
	
	private static boolean softRestarting = false;
	
	private GameManager() {}
	
	public static void setup() {
		int maxGames = ConfigMain.get().getInt("general.max-simultaneous-games");
		for(int i = 0; i < maxGames; i++) {
			games.put(i, new GameLobby(i));
		}
		if(ConfigMain.get().getConfigurationSection("game.respawn-times").getValues(false).isEmpty()) {
			return;
		}
		for(String id : ConfigMain.get().getConfigurationSection("game.respawn-times").getValues(false).keySet()) {
			respawnTimes.put(ConfigMain.get().getInt("game.respawn-times." + id + ".start-time"), 
					ConfigMain.get().getInt("game.respawn-times." + id + ".respawn-time"));
		}
	}
	
	public static Map<Integer, GameLobby> getGames() {
		return games;
	}

	public static Map<Integer, Integer> getRespawnTimes() {
		return respawnTimes;
	}

	public static boolean isSoftRestarting() {
		return softRestarting;
	}

	public static void setSoftRestarting(boolean softRestarting) {
		GameManager.softRestarting = softRestarting;
	}

}