package net.minemora.eggwarscore.extras.deatheffect;

import net.minemora.eggwarscore.config.extras.ConfigDeathEffects;
import net.minemora.eggwarscore.extras.IExtraManager;

public class DeathEffectManager extends IExtraManager {
	
	private static DeathEffectManager instance;

	private DeathEffectManager() {
		super(ConfigDeathEffects.getInstance(), "death-effects", "deathEffects", "deathEffect", false); //TODO store configurable
	}

	@Override
	public void loadIExtras() {
		DeathEffectLightning.getInstance().load(this);
		DeathEffectCat.getInstance().load(this);
		DeathEffectFirework.getInstance().load(this);
		DeathEffectExplosion.getInstance().load(this);
		DeathEffectSpirit.getInstance().load(this);
		DeathEffectClap.getInstance().load(this);
		DeathEffectDab.getInstance().load(this);
		DeathEffectFloss.getInstance().load(this);
		DeathEffectHype.getInstance().load(this);
		DeathEffectGodDeath.getInstance().load(this);
	}
	
	public static DeathEffectManager getInstance() {
		if(instance == null) {
			instance = new DeathEffectManager();
		}
		return instance;
	}
}
