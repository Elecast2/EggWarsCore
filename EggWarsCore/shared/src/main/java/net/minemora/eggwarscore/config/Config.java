package net.minemora.eggwarscore.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.ByteStreams;

import net.minemora.eggwarscore.shared.SharedHandler;


public abstract class Config {
	
	protected File pdfile;
	protected FileConfiguration config;
	protected String fileName;
	
	protected Config(String fileName) {
		this.fileName = fileName;
	}
	
	public void setup() {
		if (!SharedHandler.getPlugin().getDataFolder().exists()) {
			SharedHandler.getPlugin().getDataFolder().mkdir();
		}
		pdfile = new File(SharedHandler.getPlugin().getDataFolder(), fileName);
		boolean firstCreate = false;
		if (!pdfile.exists()) {
			firstCreate = true;
			try {
				pdfile.createNewFile();
				try (InputStream is = SharedHandler.getPlugin().getResource(fileName);
						OutputStream os = new FileOutputStream(pdfile)) {
					ByteStreams.copy(is, os);
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to create the file: " + fileName, e);
			}
		}
		config = YamlConfiguration.loadConfiguration(pdfile);
		load(firstCreate);
		update();
	}
	
	public abstract void load(boolean firstCreate);
	
	public abstract void update();
	
	public void updateSection(String path) {
		InputStream is = SharedHandler.getPlugin().getResource(fileName);
		Reader reader = new InputStreamReader(is);
		YamlConfiguration defConf = YamlConfiguration.loadConfiguration(reader);
		ConfigurationSection section = config.getConfigurationSection(path);
		updateSection(defConf, section);
	}
	
	public void updateSection(FileConfiguration defConfig, ConfigurationSection section) {
		boolean changes = false;
		if(section == null) {
			return;
		}
		if(!defConfig.contains(section.getCurrentPath())) {
			section.set(section.getCurrentPath(), null);
			return;
		}
		ConfigurationSection defSec = defConfig.getConfigurationSection(section.getCurrentPath());
        for(String key : defSec.getKeys(false)) {
            if (!section.contains(key)) {
            	section.set(key, defSec.get(key));
            	changes = true;
            }
        }
        for(String key : section.getKeys(false)) {
            if (!defSec.contains(key)) {
            	section.set(key, null);
            	changes = true;
            }
        }
        if(changes) {
        	save();
        	reload();
        	SharedHandler.getPlugin().getLogger().info(fileName + " updated!");
        }
	}

	public void save() {
		try {
			config.save(pdfile);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe("Could not save " + fileName + "!");
		}
	}	
	
	public void reload() {
		config = YamlConfiguration.loadConfiguration(pdfile);
	}
	
	public FileConfiguration getConfig() {
		return config;
	}

	public String getFileName() {
		return fileName;
	}
}