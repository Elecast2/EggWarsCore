package net.minemora.eggwarscore.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigTournament extends Config {
	
	private static ConfigTournament instance;

	protected ConfigTournament() {
		super("tournament.yml");
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
	
	public static ConfigTournament getInstance() {
		if(instance == null) {
			instance = new ConfigTournament();
		}
		return instance;
	}

}