package net.minemora.eggwarscore.extras.trail;

import net.minemora.eggwarscore.config.extras.ConfigTrails;
import net.minemora.eggwarscore.extras.Extra;
import net.minemora.eggwarscore.extras.ExtraManager;

public final class TrailManager extends ExtraManager {
	
	private static TrailManager instance;

	private TrailManager() {
		super(ConfigTrails.getInstance(), "trails", "trails", "trail", false); //TODO store configurable
	}
	
	public static TrailManager getInstance() {
		if(instance == null) {
			instance = new TrailManager();
		}
		return instance;
	}

	@Override
	public Extra getNewExtra(int id) {
		return new Trail(id);
	}
}