package net.minemora.eggwarscore.config.extras;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigDeathEffects extends ExtraConfig {
	
	private ConfigDeathEffects() {
		super("deatheffects.yml");
	}
	
	private static ConfigDeathEffects instance;

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
	
	public static ConfigDeathEffects getInstance() {
        if (instance == null) {
            instance = new ConfigDeathEffects();
        }
        return instance;
    }
}