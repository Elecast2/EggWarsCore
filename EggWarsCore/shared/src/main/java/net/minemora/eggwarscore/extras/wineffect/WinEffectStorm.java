package net.minemora.eggwarscore.extras.wineffect;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.shared.SharedHandler;

public class WinEffectStorm extends WinEffect {
	
	private static WinEffectStorm instance;

	private WinEffectStorm() {
		super("storm", 2);
	}

	@Override
	public void play(Player player) {
		player.getWorld().setStorm(true);
		player.getWorld().setThundering(true);
		new BukkitRunnable() {
			int count = 0;
			@Override
			public void run() {
				if(player.getWorld().equals(Bukkit.getWorlds().get(0))) {
					cancel();
					return;
				}
				int xrand = ThreadLocalRandom.current().nextInt(-16,16);
				int zrand = ThreadLocalRandom.current().nextInt(-16,16);
				Location ranloc = player.getLocation().clone().add(xrand, 0, zrand);
				player.getWorld().strikeLightningEffect(ranloc);
				int horserand = ThreadLocalRandom.current().nextInt(10);
				if(horserand < 3) {
					Location horseloc = ranloc.clone();
					horseloc.setY(player.getWorld().getHighestBlockYAt(ranloc));
					Entity horse = player.getWorld().spawnEntity(horseloc, EntityType.HORSE);
					((Horse) horse).setVariant(Variant.SKELETON_HORSE);
				}
				count++;
				if(count > 36) {
					cancel();
				}
			}
		}.runTaskTimer(SharedHandler.getPlugin(), 0L, 5L);
	}
	
	public static WinEffectStorm getInstance() {
		if(instance == null) {
			instance = new WinEffectStorm();
		}
		return instance;
	}

}
