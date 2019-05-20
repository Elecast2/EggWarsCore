package net.minemora.eggwarscore.extras.trail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.config.extras.ConfigTrails;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.database.Stat;
import net.minemora.eggwarscore.extras.Extra;
import net.minemora.eggwarscore.shared.SharedHandler;

public class Trail extends Extra {
	
	private Set<TrailEffect> trailEffects = new HashSet<>();	

	public Trail(int id) {
		super(TrailManager.getInstance(), ConfigTrails.getInstance(), "trails." + id, id);
		
		for (String idString : ConfigTrails.get().getConfigurationSection("trails." + id + ".particles")
				.getValues(false).keySet()) {
			int particleId = Integer.parseInt(idString);
			trailEffects.add(TrailEffect.deserealize(id, particleId));
		}
	}

	public void play(Projectile projectile) {
		new BukkitRunnable() {
			
			Map<TrailEffect,Integer> trailEffectCounter = getStartCounter();
			int count = 0;
			
			@Override
			public void run() {
				count++;
				if(projectile.isOnGround()) {
					cancel();
					return;
				}
				if(projectile.isDead()) {
					cancel();
					return;
				}
				if(!projectile.isValid()) {
					cancel();
					return;
				}
				if(projectile.getVelocity().length() == 0.0) {
					cancel();
					return;
				}
				for(TrailEffect trailEffect : trailEffectCounter.keySet()) {
					if(trailEffectCounter.get(trailEffect) == 0) {
						trailEffect.display(projectile);
						trailEffectCounter.put(trailEffect, trailEffect.getPeriod());
					}
					trailEffectCounter.put(trailEffect, trailEffectCounter.get(trailEffect)-1);
				}	
				if(count >= 60) {
					cancel();
				}
			}
		}.runTaskTimerAsynchronously(SharedHandler.getPlugin(), 3L, 1L);
	}
	
	private Map<TrailEffect,Integer> getStartCounter() {
		Map<TrailEffect,Integer> trailEffectCounter = new HashMap<>();
		for(TrailEffect trailEffect : trailEffects) {
			trailEffectCounter.put(trailEffect, trailEffect.getDelay());
		}
		return trailEffectCounter;
	}

	@Override
	public void giveToPlayer(PlayerStats playerStats) {
		playerStats.setTrail(this);
		playerStats.setRandomTrail(false);
		Database.set(Stat.TRAIL, playerStats.getPlayer(), getId());
	}
}