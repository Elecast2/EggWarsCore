package net.minemora.eggwarscore.extras.wineffect;

import net.minemora.eggwarscore.config.extras.ConfigWinEffects;
import net.minemora.eggwarscore.extras.IExtraManager;

public class WinEffectManager extends IExtraManager {
	
	private static WinEffectManager instance;

	private WinEffectManager() {
		super(ConfigWinEffects.getInstance(), "win-effects", "winEffects", "winEffect", false); //TODO store configurable
	}	

	@Override
	public void loadIExtras() { //TODO setear las ID aqui en vez de en el constructor
		WinEffectFirework.getInstance().load(this);
		WinEffectStorm.getInstance().load(this);
		WinEffectTNT.getInstance().load(this);
		WinEffectCrazyBlocks.getInstance().load(this);
		WinEffectCatRain.getInstance().load(this);
		WinEffectChickenRaid.getInstance().load(this);
		WinEffectGhosts.getInstance().load(this);
		WinEffectDancers.getInstance().load(this);
		WinEffectThriller.getInstance().load(this);
	}
	
	public static WinEffectManager getInstance() {
		if(instance == null) {
			instance = new WinEffectManager();
		}
		return instance;
	}
}
