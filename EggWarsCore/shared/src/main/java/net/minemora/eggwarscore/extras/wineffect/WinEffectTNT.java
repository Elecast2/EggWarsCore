package net.minemora.eggwarscore.extras.wineffect;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.shared.SharedHandler;

public class WinEffectTNT extends WinEffect {
	
	private static WinEffectTNT instance;

	private WinEffectTNT() {
		super("tnt-rain", 3);
	}

	@Override
	public void play(Player player) {
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
				Location ranloc = player.getLocation().clone().add(xrand, 10, zrand);
				player.getWorld().spawnEntity(ranloc, EntityType.PRIMED_TNT);
				count++;
				if(count > 36) {
					cancel();
				}
			}
		}.runTaskTimer(SharedHandler.getPlugin(), 0L, 5L);
	}

	public static WinEffectTNT getInstance() {
		if(instance == null) {
			instance = new WinEffectTNT();
		}
		return instance;
	}
}