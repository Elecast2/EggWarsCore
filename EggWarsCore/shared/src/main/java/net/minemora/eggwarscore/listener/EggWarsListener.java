package net.minemora.eggwarscore.listener;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public abstract class EggWarsListener implements Listener {

	private final Plugin plugin;

	public EggWarsListener(Plugin plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	protected Plugin getPlugin() {
		return plugin;
	}
}