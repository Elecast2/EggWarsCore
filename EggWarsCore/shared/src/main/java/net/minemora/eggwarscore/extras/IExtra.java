package net.minemora.eggwarscore.extras;

import net.minemora.eggwarscore.config.Config;

public abstract class IExtra extends Extra {
	
	private final String extraPath;

	public IExtra(ExtraManager manager, Config config, String listPath, String extraPath, int id) {
		super(manager, config, listPath + "." + extraPath, id);
		this.extraPath = extraPath;
	}

	public String getExtraPath() {
		return extraPath;
	}
	
	public void load(IExtraManager manager) {
		manager.getIExtras().put(extraPath, this);
	}
}