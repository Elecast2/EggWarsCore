package net.minemora.eggwarscore.config;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigTournamentStats extends Config {
	
	private static ConfigTournamentStats instance;

	protected ConfigTournamentStats() {
		super("tournament-stats.yml");
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
	
	public static ConfigTournamentStats getInstance() {
		if(instance == null) {
			instance = new ConfigTournamentStats();
		}
		return instance;
	}

}