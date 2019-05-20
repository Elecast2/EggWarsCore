package net.minemora.eggwarscore.extras.kit;

import net.minemora.eggwarscore.config.extras.ConfigKits;
import net.minemora.eggwarscore.extras.Extra;
import net.minemora.eggwarscore.extras.ExtraManager;

public class KitManager extends ExtraManager {
	
	private static KitManager instance;

	private KitManager() {
		super(ConfigKits.getInstance(), "kits", "kits", "kit", true); //TODO store configurable
	}
	
	public static KitManager getInstance() {
		if(instance == null) {
			instance = new KitManager();
		}
		return instance;
	}

	@Override
	public Extra getNewExtra(int id) {
		return new Kit(id);
	}
}
