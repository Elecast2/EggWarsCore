package net.minemora.eggwarscore.shared;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.minemora.eggwarscore.NMSCheck;
import net.minemora.eggwarscore.chat.ChatManager;
import net.minemora.eggwarscore.commands.Stats;
import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.config.ConfigScoreboard;
import net.minemora.eggwarscore.config.extras.ConfigDeathEffects;
import net.minemora.eggwarscore.config.extras.ConfigKits;
import net.minemora.eggwarscore.config.extras.ConfigTrails;
import net.minemora.eggwarscore.config.extras.ConfigWinEffects;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.extras.ExtraManager;
import net.minemora.eggwarscore.lobby.Lobby;
import net.minemora.eggwarscore.lobby.LobbyProtection;
import net.minemora.eggwarscore.nms.NMS;

public final class SharedHandler {
	
	private static Plugin plugin;
	private static NMS nmsHandler;
	
	public static void load(JavaPlugin plug) {
		plugin = plug;
		nmsHandler = NMSCheck.getNMS(plugin);
		ConfigMain.getInstance().setup();
		ConfigLang.getInstance().setup();
		ConfigScoreboard.getInstance().setup();
		VaultManager.setup(plug);
		ConfigKits.getInstance().setup();
		ConfigTrails.getInstance().setup();
		ConfigDeathEffects.getInstance().setup();
		ConfigWinEffects.getInstance().setup();
		ChatManager.setup();
		ExtraManager.loadExtras();
		Lobby.getLobby().setup();
		Database.getDatabase().setup();
		new LobbyProtection(plugin);
		plug.getCommand("stats").setExecutor(new Stats());
	}

	public static Plugin getPlugin() {
		return plugin;
	}
	
	public static NMS getNmsHandler() {
		return nmsHandler;
	}
}