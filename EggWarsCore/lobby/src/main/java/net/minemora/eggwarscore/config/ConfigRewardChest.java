package net.minemora.eggwarscore.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigRewardChest extends Config {
	
	private static ConfigRewardChest instance;

	protected ConfigRewardChest() {
		super("reward-chest.yml");
	}

	@Override
	public void load(boolean firstCreate) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
	
	public static FileConfiguration get() {
		return getInstance().config;
	}
	
	public static ConfigRewardChest getInstance() {
		if(instance == null) {
			instance = new ConfigRewardChest();
		}
		return instance;
	}

}