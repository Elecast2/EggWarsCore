package net.minemora.eggwarscore.chat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.shared.VaultManager;

public final class ChatManager {
	
	private static Map<String,PlaceholderReplacer> placeholders = new HashMap<>();
	
	private ChatManager() {}
	
	public static void setup() {
		loadPlaceholder("player-name", new PlaceholderReplacer() {
			@Override
			public String getReplace(Player player) {
				return player.getName();
			}
		});
		loadPlaceholder("player-prefix", new PlaceholderReplacer() {
			@Override
			public String getReplace(Player player) {
				return VaultManager.getPlayerPrefix(player).replace("\\", "\\\\").replace("$", "\\$");
			}
		});
		loadPlaceholder("player-suffix", new PlaceholderReplacer() {
			@Override
			public String getReplace(Player player) {
				return VaultManager.getPlayerSuffix(player);
			}
		});
		loadPlaceholder("level-prefix", new PlaceholderReplacer() {
			@Override
			public String getReplace(Player player) {
				int finalRange = 0;
				for(String range : ConfigMain.get().getConfigurationSection("chat-format.level-prefix").getValues(false).keySet()) {
					int rangeFrom = Integer.parseInt(range);
					if(PlayerStats.get(player.getName()).getLevel() >= rangeFrom) {
						finalRange = rangeFrom;
					}
				}
				return ConfigMain.get().getString("chat-format.level-prefix." + finalRange)
						.replaceAll("%level%", String.valueOf(PlayerStats.get(player.getName()).getLevel()));
			}
		});
	}
	
	public static void loadPlaceholder(String placeholder, PlaceholderReplacer replacer) {
		placeholders.put(placeholder, replacer);
	}
	
	public static String replace(Player player, String text, String placeholder) {
		if(!placeholders.containsKey(placeholder)) {
			return text;
		}
		return text.replaceAll("%" + placeholder + "%", placeholders.get(placeholder).getReplace(player));
	}
	
	public static String replace(Player player, String text, String[] placeholders) {
		for(String placeholder : placeholders) {
			text = replace(player, text, placeholder);
		}
		return text;
	}
	
	public static String replaceBasic(Player player, String text) {
		return replace(player, text, new String[] {"player-name","player-prefix","player-suffix","level-prefix"});
	}
}