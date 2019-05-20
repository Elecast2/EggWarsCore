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
			loadTopPlaceholders(limit);
			new BukkitRunnable() {
				@Override
				public void run() {
					int i = 0;
					LinkedHashMap<String, Integer> top = Database.getTopKills();
					for(String name : top.keySet()) {
						topKillsName[i] = name;
						topKillsNumber[i] = top.get(name);
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
	
	private static void loadTopPlaceholders(int limit) {		
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

	public static boolean isEnabled() {
		return enabled;
	}
}