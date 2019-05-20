package net.minemora.eggwarscore.config.extras;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigTrails extends ExtraConfig {
	
	private ConfigTrails() {
		super("trails.yml");
	}
	
	private static ConfigTrails instance;

	@Override
	public void load(boolean firstCreated) {
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	public static FileConfiguration get() {
		return getInstance().config;
	}
	
	public static ConfigTrails getInstance() {
        if (instance == null) {
            instance = new ConfigTrails();
        }
        return instance;
    }
}