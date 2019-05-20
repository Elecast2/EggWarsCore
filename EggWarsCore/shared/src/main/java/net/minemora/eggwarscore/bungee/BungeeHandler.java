package net.minemora.eggwarscore.bungee;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.minemora.eggwarscore.shared.SharedHandler;

public final class BungeeHandler {
	
	private BungeeHandler() {}
	
	public static void setup(Plugin plugin) {
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
		plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "RedisBungee"); //TODO only if enabled
	}
	
	public static void sendPlayer(Player player, String serverName) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(serverName);
		player.sendPluginMessage(SharedHandler.getPlugin(), "BungeeCord", out.toByteArray());
	}
	
	public static void getPlayerCount(String serverName, String channel) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PlayerCount");
        out.writeUTF(serverName);
        for(Player player : Bukkit.getOnlinePlayers()) {
        	player.sendPluginMessage(SharedHandler.getPlugin(), channel, out.toByteArray());
        	break;
        }
	}
}