package net.minemora.eggwarscore.config;

import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigGenerators extends Config {

	private ConfigGenerators() {
		super("generators.yml");
	}

	private static ConfigGenerators instance;

	@Override
	public void load(boolean firstCreate) {

	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	public static FileConfiguration get() {
		return getInstance().config;
	}
	
	public static ConfigGenerators getInstance() {
        if (instance == null) {
            instance = new ConfigGenerators();
        }
        return instance;
    }
}