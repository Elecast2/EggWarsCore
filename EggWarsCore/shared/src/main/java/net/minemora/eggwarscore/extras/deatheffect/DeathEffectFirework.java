package net.minemora.eggwarscore.extras.deatheffect;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class DeathEffectFirework extends DeathEffect {
	
	private static DeathEffectFirework instance;

	private DeathEffectFirework() {
		super("firework", 3);
	}

	@Override
	public void play(Player player) {
		Firework fw = (Firework) player.getWorld().spawn(player.getLocation(), Firework.class);
		FireworkMeta fmeta = fw.getFireworkMeta();
		fmeta.addEffect(FireworkEffect.builder().with(Type.BALL).withColor
				(Color.GREEN, Color.LIME).withFade(Color.ORANGE).build());
		fmeta.setPower(0);
		fw.setFireworkMeta(fmeta);
	}
	
	public static DeathEffectFirework getInstance() {
		if(instance == null) {
			instance = new DeathEffectFirework();
		}
		return instance;
	}

}
