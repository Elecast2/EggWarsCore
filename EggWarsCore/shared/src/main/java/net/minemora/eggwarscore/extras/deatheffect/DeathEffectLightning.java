package net.minemora.eggwarscore.extras.deatheffect;

import org.bukkit.entity.Player;

public class DeathEffectLightning extends DeathEffect {
	
	private static DeathEffectLightning instance;

	private DeathEffectLightning() {
		super("lightning", 1);
	}

	@Override
	public void play(Player player) {
		player.getWorld().strikeLightningEffect(player.getLocation());
	}
	
	public static DeathEffectLightning getInstance() {
		if(instance == null) {
			instance = new DeathEffectLightning();
		}
		return instance;
	}

}
