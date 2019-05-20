package net.minemora.eggwarscore.config;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.utils.ChatUtils;

public final class ConfigArenas extends Config {

	private ConfigArenas() {
		super("arenas.yml");
	}

	private static ConfigArenas instance;

	@Override
	public void load(boolean firstCreate) {
		File arenasFolder = new File(EggWarsCore.getPlugin().getDataFolder(), "Arenas");
		if (!arenasFolder.exists()) {
			arenasFolder.mkdir();
		}
		if (config.get("arenas") == null) {
			Bukkit.getServer().getLogger().severe(ChatUtils
					.format("&cThere are no arenas configured in the arenas.yml file, " + "please use /ewc create"));
			EggWarsCore.getPlugin().setHasArenas(false);
		}
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	public static FileConfiguration get() {
		return getInstance().config;
	}
	
	public static ConfigArenas getInstance() {
		if (instance == null) {
            instance = new ConfigArenas();
        }
        return instance;
	}
}