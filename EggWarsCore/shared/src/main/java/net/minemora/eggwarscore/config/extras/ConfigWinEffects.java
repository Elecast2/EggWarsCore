package net.minemora.eggwarscore.config.extras;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigWinEffects extends ExtraConfig {
	
	private ConfigWinEffects() {
		super("wineffects.yml");
	}
	
	private static ConfigWinEffects instance;

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
	
	public static ConfigWinEffects getInstance() {
        if (instance == null) {
            instance = new ConfigWinEffects();
        }
        return instance;
    }
}