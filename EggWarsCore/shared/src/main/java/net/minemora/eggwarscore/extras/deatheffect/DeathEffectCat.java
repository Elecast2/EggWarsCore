package net.minemora.eggwarscore.extras.deatheffect;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.shared.SharedHandler;

public class DeathEffectCat extends DeathEffect {
	
	private static DeathEffectCat instance;

	private DeathEffectCat() {
		super("cat", 2);
	}

	@Override
	public void play(Player player) {
		player.getWorld().playSound(player.getLocation(), Sound.CAT_MEOW, 0.5f, 1.0f);
		Ocelot ocelot = (Ocelot) player.getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.OCELOT);
		Ocelot.Type types[] = {Ocelot.Type.BLACK_CAT,Ocelot.Type.RED_CAT,Ocelot.Type.SIAMESE_CAT};
		ocelot.setCatType(types[ThreadLocalRandom.current().nextInt(types.length)]);
		new BukkitRunnable() {
			@Override
			public void run() {
				ocelot.getWorld().createExplosion(ocelot.getLocation(), 0);
				ocelot.remove();
			}
		}.runTaskLater(SharedHandler.getPlugin(), 40L);
	}
	
	public static DeathEffectCat getInstance() {
		if(instance == null) {
			instance = new DeathEffectCat();
		}
		return instance;
	}

}
