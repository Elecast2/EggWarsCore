package net.minemora.eggwarscore.shared;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import net.minemora.eggwarscore.config.ConfigMain;

public final class VaultManager {
	
	private static boolean enabled;
	
	private static Permission perms = null;
	private static Chat chat = null;
	
	private static String defaultGroup = "Usuario"; //TODO CONFIG y setear en setup()
	
	private VaultManager() {}
	
	public static void setup(Plugin plugin) {
		enabled = ConfigMain.get().getBoolean("general.use-vault");
		if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
			if(enabled) {
				plugin.getLogger().severe("Vault not found! disabling EggWarsCore...");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
			}
            return;
        }
		setupPermissions(plugin);
		setupChat(plugin);
	}
	
	public static boolean hasPermission(Player player, String permission) {
		if(enabled) {
			return getPermissions().has(player, permission);
		}
		else {
			return player.hasPermission(permission);
		}
	}
	
	public static String getPlayerPrefix(Player player){
		if(enabled) {
			return getChat().getPlayerPrefix(player);
		}
		else {
			return "";
		}
	}
	
	public static String getPlayerSuffix(Player player){
		if(enabled) {
			return getChat().getPlayerSuffix(player);
		}
		else {
			return " ";
		}
	}
	
	public static String getPlayerGroup(Player player) {
		if(enabled) {
			return getChat().getPrimaryGroup(player);
		}
		else {
			return "";
		}
	}
	
	public static String getGroupPrefix(String group) {
		if(enabled) {
			return getChat().getGroupPrefix(Bukkit.getWorlds().get(0), group);
		}
		else {
			return "";
		}
	}
	
	public static String getTagGroupPrefix(String group) {
		String prefix = getGroupPrefix(group);
		if(prefix.length()>16) {		
			prefix = prefix.replaceAll("\\s","").substring(0, 12);
			if(prefix.endsWith("§") || prefix.endsWith("&")) {
				prefix = prefix.substring(0, (prefix.length() - 1));
			}
			prefix = prefix + " &f";
		}
		return prefix;
	}
	
	public static String[] getGroups() {
		return getPermissions().getGroups();
	}
	
	public static String getDefaultGroup() {
		return defaultGroup;
	}
	
	private static boolean setupPermissions(Plugin plugin) {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
	
	private static boolean setupChat(Plugin plugin) {
        RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }
	
	public static Permission getPermissions() {
        return perms;
    }
	
	public static Chat getChat() {
        return chat;
    }
	
	public static boolean isEnabled() {
		return enabled;
	}

}
