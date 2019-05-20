package net.minemora.eggwarscore.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigLang extends Config {

	private ConfigLang() {
		super("lang.yml");
	}

	private static ConfigLang instance;

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
	
	public static ConfigLang getInstance() {
        if (instance == null) {
            instance = new ConfigLang();
        }
        return instance;
    }
}