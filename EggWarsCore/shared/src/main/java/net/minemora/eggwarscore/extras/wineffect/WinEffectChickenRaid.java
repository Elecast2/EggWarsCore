package net.minemora.eggwarscore.extras.wineffect;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.utils.ChatUtils;

public class WinEffectChickenRaid extends WinEffect {

	private static WinEffectChickenRaid instance;

	private WinEffectChickenRaid() {
		super("chicken-raid", 6);
	}

	@Override
	public void play(Player player) {
		new BukkitRunnable() {
			int count = 0;
			int rythm = 0;
			@Override
			public void run() {
				if(player.getWorld().equals(Bukkit.getWorlds().get(0))) {
					cancel();
					return;
				}
				for(int i = 0; i<4; i++) {
					int xrand = ThreadLocalRandom.current().nextInt(-16,16);
					int zrand = ThreadLocalRandom.current().nextInt(-16,16);
					Location ranloc = player.getLocation().clone().add(xrand, 0, zrand);
					ranloc.setY(player.getWorld().getHighestBlockYAt(ranloc)+3);
					Entity ent = player.getWorld().spawnEntity(ranloc, EntityType.CHICKEN);
					ent.setCustomName(ChatUtils.format("&bPollito de " + player.getName())); //TODO LANG
					Projectile egg = (Projectile) ent.getWorld().spawnEntity(ent.getLocation().add(0, 1, 0), EntityType.EGG);
					egg.setVelocity(new Vector(0,2,0));
				}
				for(Player lp : player.getWorld().getPlayers()) {
					if(rythm == 0) {
						lp.playSound(lp.getLocation(), Sound.NOTE_BASS_DRUM, 10, 1);
						lp.playSound(lp.getLocation(), Sound.CHICKEN_HURT, 10, 1.2f);
						lp.playSound(lp.getLocation(), Sound.CHICKEN_EGG_POP, 10, 2.0f);
					}
					else if(rythm == 1) {
						lp.playSound(lp.getLocation(), Sound.NOTE_STICKS, 10, 1.5f);
						lp.playSound(lp.getLocation(), Sound.CHICKEN_EGG_POP, 10, 2.0f);
					}
					else if(rythm == 2) {
						lp.playSound(lp.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
						lp.playSound(lp.getLocation(), Sound.CHICKEN_HURT, 10, 0.8f);
						lp.playSound(lp.getLocation(), Sound.CHICKEN_EGG_POP, 10, 2.0f);
					}
					else if(rythm == 3) {
						lp.playSound(lp.getLocation(), Sound.NOTE_STICKS, 10, 1.5f);
						lp.playSound(lp.getLocation(), Sound.CHICKEN_EGG_POP, 10, 2.0f);
					}	
        		}
				rythm++;
				if(rythm == 4) {
					rythm = 0;
				}
				count++;
				if(count > 36) {
					cancel();
				}
			}
		}.runTaskTimer(SharedHandler.getPlugin(), 0L, 5L);
	}
	
	public static WinEffectChickenRaid getInstance() {
		if(instance == null) {
			instance = new WinEffectChickenRaid();
		}
		return instance;
	}
}
