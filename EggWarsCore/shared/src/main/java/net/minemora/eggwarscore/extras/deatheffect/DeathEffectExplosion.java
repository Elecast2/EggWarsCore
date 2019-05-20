package net.minemora.eggwarscore.extras.deatheffect;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.utils.Utils;

public class DeathEffectExplosion extends DeathEffect {
	
	private static DeathEffectExplosion instance;

	private DeathEffectExplosion() {
		super("explosion", 4);
	}

	@Override
	public void play(Player player) {
		new BukkitRunnable(){
			int count = 1;
			Location playerloc = player.getLocation();
			@Override
			public void run() {
				for(Block block : Utils.getBlocksInRadius(playerloc, count, true)) {
					if(block.getType().equals(Material.AIR)) {
						new BukkitRunnable() {
							@Override
							public void run() {
								block.getWorld().createExplosion(block.getLocation(), 0);
							}
						}.runTaskLater(SharedHandler.getPlugin(), ThreadLocalRandom.current().nextInt(7));
					}
				}
				count++;
				if(count > 3) {
					cancel();
					return;
				}
			}
		}.runTaskTimer(SharedHandler.getPlugin(), 0L, 7L);
	}
	
	public static DeathEffectExplosion getInstance() {
		if(instance == null) {
			instance = new DeathEffectExplosion();
		}
		return instance;
	}

}
