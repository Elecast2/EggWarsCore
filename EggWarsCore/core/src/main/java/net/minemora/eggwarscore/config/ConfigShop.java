package net.minemora.eggwarscore.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import net.minemora.eggwarscore.utils.ChatUtils;

public class ConfigShop extends Config {

	private ConfigShop() {
		super("shop.yml");
	}
	
	private static ConfigShop instance;

	@Override
	public void load(boolean firstCreate) {
		if (config.get("shops") == null) {
			Bukkit.getServer().getLogger().severe(ChatUtils
					.format("&cThere are no shops configured in the shop.yml file"));
		}
	}
	
	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}

	public static FileConfiguration get() {
		return getInstance().config;
	}
	
	public static ConfigShop getInstance() {
        if (instance == null) {
            instance = new ConfigShop();
        }
        return instance;
    }
}