package net.minemora.eggwarscore.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigScoreboard extends Config {
	
	private ConfigScoreboard() {
		super("scoreboard.yml");
	}

	private static ConfigScoreboard instance;

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

	public static ConfigScoreboard getInstance() {
        if (instance == null) {
            instance = new ConfigScoreboard();
        }
        return instance;
    }
}
