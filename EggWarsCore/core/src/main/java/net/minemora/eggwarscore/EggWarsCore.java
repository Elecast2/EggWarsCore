package net.minemora.eggwarscore;

import org.bukkit.plugin.java.JavaPlugin;

import net.minemora.eggwarscore.arena.ArenaManager;
import net.minemora.eggwarscore.bungee.BungeeHandler;
import net.minemora.eggwarscore.commands.Debug;
import net.minemora.eggwarscore.commands.EggWarsCommand;
import net.minemora.eggwarscore.commands.JoinCommand;
import net.minemora.eggwarscore.commands.LeaveCommand;
import net.minemora.eggwarscore.config.ConfigArenas;
import net.minemora.eggwarscore.config.ConfigGenerators;
import net.minemora.eggwarscore.config.ConfigShop;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.generator.GeneratorManager;
import net.minemora.eggwarscore.holographicdisplays.HolographicDisplaysHook;
import net.minemora.eggwarscore.listener.PlayerListener;
import net.minemora.eggwarscore.listener.WorldListener;
import net.minemora.eggwarscore.lobby.LobbyItemManager;
import net.minemora.eggwarscore.network.NetworkManager;
import net.minemora.eggwarscore.scoreboard.ScoreboardManager;
import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.shop.ShopManager;
import net.minemora.eggwarscore.team.TeamManager;

public class EggWarsCore extends JavaPlugin {

	private static EggWarsCore plugin;

	private boolean hasArenas = true;

	@Override
	public void onEnable() {
		plugin = this;
		SharedHandler.load(this);
		ConfigGenerators.getInstance().setup();
		ConfigArenas.getInstance().setup();
		ConfigShop.getInstance().setup();
		TeamManager.loadTeams();
		GeneratorManager.loadGenerators();
		ShopManager.loadShops();
		ArenaManager.setup();
		ArenaManager.loadArenas();
		ScoreboardManager.loadPlaceholders();
		ScoreboardManager.loadScoreboards();
		LobbyItemManager.setup();
		BungeeHandler.setup(this);
		new PlayerListener(this);
		new WorldListener(this);
		getCommand("ewc").setExecutor(new EggWarsCommand());
		getCommand("leave").setExecutor(new LeaveCommand());
		getCommand("join").setExecutor(new JoinCommand());
		getCommand("debug").setExecutor(new Debug());	
		GameManager.setup();
		NetworkManager.setup();
		HolographicDisplaysHook.register();
	}

	@Override
	public void onDisable() {
		Database.getDatabase().close();
		NetworkManager.disconnectAll();
	}

	public static EggWarsCore getPlugin() {
		return plugin;
	}

	public boolean hasArenas() {
		return hasArenas;
	}

	public void setHasArenas(boolean hasArenas) {
		this.hasArenas = hasArenas;
	}

}