package net.minemora.eggwarscore.config.extras;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigKits extends ExtraConfig {
	
	private ConfigKits() {
		super("kits.yml");
	}
	
	private static ConfigKits instance;

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
	
	public static ConfigKits getInstance() {
        if (instance == null) {
            instance = new ConfigKits();
        }
        return instance;
    }
}