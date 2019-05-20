package net.minemora.eggwarscore.extras.wineffect;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.shared.SharedHandler;

public class WinEffectGhosts extends WinEffect {

	private static WinEffectGhosts instance;

	private WinEffectGhosts() {
		super("ghosts", 7);
	}

	@Override
	public void play(Player player) {
		Location loc = player.getLocation();
		new BukkitRunnable() {
    		int count = 15;
			@Override
    		public void run() {
				if(player.getWorld().equals(Bukkit.getWorlds().get(0))) {
					cancel();
					return;
				}
    			count--;
    			int xrand = ThreadLocalRandom.current().nextInt(-16,16);
				int zrand = ThreadLocalRandom.current().nextInt(-16,16);
				Location ranloc = loc.clone().add(xrand, 10, zrand);
				ranloc.getWorld().spawnEntity(ranloc, EntityType.GHAST);
    	    	if(count==0) {
    	    		cancel();
    	    	}
    		}
    	}.runTaskTimer(SharedHandler.getPlugin(), 0L, 3L);
	}
	
	public static WinEffectGhosts getInstance() {
		if(instance == null) {
			instance = new WinEffectGhosts();
		}
		return instance;
	}
}
