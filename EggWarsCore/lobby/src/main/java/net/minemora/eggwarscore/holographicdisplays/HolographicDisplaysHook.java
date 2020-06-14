package net.minemora.eggwarscore.holographicdisplays;

import java.util.LinkedHashMap;

import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.game.Game;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.network.GamesConnection;

public final class HolographicDisplaysHook {
	
	private static String[] topKillsName;
	private static int[] topKillsNumber;
	
	private static String[] topDeathsName;
	private static int[] topDeathsNumber;
	
	private static String[] topWinsName;
	private static int[] topWinsNumber;
	
	private static String[] topEggsName;
	private static int[] topEggsNumber;
	
	
	private static boolean enabled = false;
	
	private HolographicDisplaysHook() {}
	
	public static void register() {
		if (EggWarsCoreLobby.getPlugin().getServer().getPluginManager().getPlugin("HolographicDisplays") == null) {
			return;
		}
		if(!ConfigMain.get().getBoolean("general.holographic-displays")) {
			return;
		}
		enabled = true; 
		if(ConfigMain.get().getBoolean("general.hd-top-kills")) {
			int limit = ConfigMain.get().getInt("general.top-limit");
			topKillsName = new String[limit];
			topKillsNumber = new int[limit];
			for(int i = 0; i < limit; i++) {
				topKillsName[i] = "loading...";
				topKillsNumber[i] = 0;
			}
			loadTopKillsPlaceholders(limit);
			new BukkitRunnable() {
				@Override
				public void run() {
					int i = 0;
					LinkedHashMap<String, Integer> top = Database.getTopKills(limit);
					for(String name : top.keySet()) {
						topKillsName[i] = name;
						topKillsNumber[i] = top.get(name);
						i++;
					}
				}
			}.runTaskTimerAsynchronously(EggWarsCoreLobby.getPlugin(), 200, 1200*ConfigMain.get().getInt("general.top-update-time"));
		}
		if(ConfigMain.get().getBoolean("general.hd-top-kills")) {//TODO ADD NEW CONFIG
			int limit = 5;
			topDeathsName = new String[limit];
			topDeathsNumber = new int[limit];
			for(int i = 0; i < limit; i++) {
				topDeathsName[i] = "loading...";
				topDeathsNumber[i] = 0;
			}
			loadTopDeathsPlaceholders(limit);
			new BukkitRunnable() {
				@Override
				public void run() {
					int i = 0;
					LinkedHashMap<String, Integer> top = Database.getTopDeaths(limit);
					for(String name : top.keySet()) {
						topDeathsName[i] = name;
						topDeathsNumber[i] = top.get(name);
						i++;
					}
				}
			}.runTaskTimerAsynchronously(EggWarsCoreLobby.getPlugin(), 200, 1200*ConfigMain.get().getInt("general.top-update-time"));
		}
		if(ConfigMain.get().getBoolean("general.hd-top-kills")) {//TODO ADD NEW CONFIG
			int limit = 5;
			topWinsName = new String[limit];
			topWinsNumber = new int[limit];
			for(int i = 0; i < limit; i++) {
				topWinsName[i] = "loading...";
				topWinsNumber[i] = 0;
			}
			loadTopWinsPlaceholders(limit);
			new BukkitRunnable() {
				@Override
				public void run() {
					int i = 0;
					LinkedHashMap<String, Integer> top = Database.getTopWins(limit);
					for(String name : top.keySet()) {
						topWinsName[i] = name;
						topWinsNumber[i] = top.get(name);
						i++;
					}
				}
			}.runTaskTimerAsynchronously(EggWarsCoreLobby.getPlugin(), 200, 1200*ConfigMain.get().getInt("general.top-update-time"));
		}
		if(ConfigMain.get().getBoolean("general.hd-top-kills")) { //TODO ADD NEW CONFIG
			int limit = 5;
			topEggsName = new String[limit];
			topEggsNumber = new int[limit];
			for(int i = 0; i < limit; i++) {
				topEggsName[i] = "loading...";
				topEggsNumber[i] = 0;
			}
			loadTopEggsPlaceholders(limit);
			new BukkitRunnable() {
				@Override
				public void run() {
					int i = 0;
					LinkedHashMap<String, Integer> top = Database.getTopEggs(limit);
					for(String name : top.keySet()) {
						topEggsName[i] = name;
						topEggsNumber[i] = top.get(name);
						i++;
					}
				}
			}.runTaskTimerAsynchronously(EggWarsCoreLobby.getPlugin(), 200, 1200*ConfigMain.get().getInt("general.top-update-time"));
		}
		
		loadPlayerCountPlaceholders();
	}
	
	private static void loadPlayerCountPlaceholders() {
		for(String mode : GameManager.getModes()) {
			HologramsAPI.registerPlaceholder(EggWarsCoreLobby.getPlugin(), "{" + mode + "-players}", 3, new PlaceholderReplacer() {
				@Override
				public String update() {
					int players = 0;
					if(GameManager.getGames().containsKey(mode)) {
						for(GamesConnection gconn : GameManager.getGames().get(mode)) {
							for(Game game : gconn.getGames().values()) {
								players = players + game.getPlayerCount();
							}
						}
					}
					return String.valueOf(players);
				}
			});
		}
	}
	
	private static void loadTopKillsPlaceholders(int limit) {		
		for(int i = 0; i < limit; i++) {
			final int index = i;
			HologramsAPI.registerPlaceholder(EggWarsCoreLobby.getPlugin(), "{top-kills-name-" + (i+1) + "}", 60, new PlaceholderReplacer() {
				@Override
				public String update() {
					return topKillsName[index];
				}
			});
			HologramsAPI.registerPlaceholder(EggWarsCoreLobby.getPlugin(), "{top-kills-number-" + (i+1) + "}", 60, new PlaceholderReplacer() {
				@Override
				public String update() {
					return String.valueOf(topKillsNumber[index]);
				}
			});
		}
	}
	
	private static void loadTopDeathsPlaceholders(int limit) {		
		for(int i = 0; i < limit; i++) {
			final int index = i;
			HologramsAPI.registerPlaceholder(EggWarsCoreLobby.getPlugin(), "{top-deaths-name-" + (i+1) + "}", 60, new PlaceholderReplacer() {
				@Override
				public String update() {
					return topDeathsName[index];
				}
			});
			HologramsAPI.registerPlaceholder(EggWarsCoreLobby.getPlugin(), "{top-deaths-number-" + (i+1) + "}", 60, new PlaceholderReplacer() {
				@Override
				public String update() {
					return String.valueOf(topDeathsNumber[index]);
				}
			});
		}
	}
	
	private static void loadTopWinsPlaceholders(int limit) {		
		for(int i = 0; i < limit; i++) {
			final int index = i;
			HologramsAPI.registerPlaceholder(EggWarsCoreLobby.getPlugin(), "{top-wins-name-" + (i+1) + "}", 60, new PlaceholderReplacer() {
				@Override
				public String update() {
					return topWinsName[index];
				}
			});
			HologramsAPI.registerPlaceholder(EggWarsCoreLobby.getPlugin(), "{top-wins-number-" + (i+1) + "}", 60, new PlaceholderReplacer() {
				@Override
				public String update() {
					return String.valueOf(topWinsNumber[index]);
				}
			});
		}
	}
	
	private static void loadTopEggsPlaceholders(int limit) {		
		for(int i = 0; i < limit; i++) {
			final int index = i;
			HologramsAPI.registerPlaceholder(EggWarsCoreLobby.getPlugin(), "{top-eggs-name-" + (i+1) + "}", 60, new PlaceholderReplacer() {
				@Override
				public String update() {
					return topEggsName[index];
				}
			});
			HologramsAPI.registerPlaceholder(EggWarsCoreLobby.getPlugin(), "{top-eggs-number-" + (i+1) + "}", 60, new PlaceholderReplacer() {
				@Override
				public String update() {
					return String.valueOf(topEggsNumber[index]);
				}
			});
		}
	}

	public static boolean isEnabled() {
		return enabled;
	}
}