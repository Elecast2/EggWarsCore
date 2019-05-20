package net.minemora.eggwarscore.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigNPC extends Config {
	
	private ConfigNPC() {
		super("npc.yml");
	}

	private static ConfigNPC instance;

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
	
	public static ConfigNPC getInstance() {
        if (instance == null) {
            instance = new ConfigNPC();
        }
        return instance;
    }

}
