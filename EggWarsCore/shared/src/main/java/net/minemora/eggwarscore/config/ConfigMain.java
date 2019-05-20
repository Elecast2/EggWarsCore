package net.minemora.eggwarscore.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigMain extends Config {
	
	private static ConfigMain instance;

	private ConfigMain() {
		super("config.yml");
	}

	@Override
	public void load(boolean firstCreate) {
		if (firstCreate) {
			config.set("lobby.spawn.x", Bukkit.getWorlds().get(0).getSpawnLocation().getX() + 0.5);
			config.set("lobby.spawn.y", Bukkit.getWorlds().get(0).getSpawnLocation().getY());
			config.set("lobby.spawn.z", Bukkit.getWorlds().get(0).getSpawnLocation().getZ() + 0.5);
			save();
		}
	}
	
	@Override
	public void update() {
		updateSection("general");
		updateSection("team");
		updateSection("extras");
	}
	
	public static FileConfiguration get() {
		return getInstance().config;
	}

	public static ConfigMain getInstance() {
		if (instance == null) {
            instance = new ConfigMain();
        }
        return instance;
	}
}