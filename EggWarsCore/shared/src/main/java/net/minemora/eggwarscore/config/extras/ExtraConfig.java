package net.minemora.eggwarscore.config.extras;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.YamlConfiguration;

import com.google.common.io.ByteStreams;

import net.minemora.eggwarscore.config.Config;
import net.minemora.eggwarscore.shared.SharedHandler;

public abstract class ExtraConfig extends Config {

	protected ExtraConfig(String fileName) {
		super(fileName);
	}
	
	@Override
	public void setup() {
		if (!SharedHandler.getPlugin().getDataFolder().exists()) {
			SharedHandler.getPlugin().getDataFolder().mkdir();
		}
		File extrasFolder = new File(SharedHandler.getPlugin().getDataFolder(), "Extras");
		if (!extrasFolder.exists()) {
			extrasFolder.mkdir();
		}
		pdfile = new File(extrasFolder, fileName);
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

}
