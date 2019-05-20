package net.minemora.eggwarscore.extras.wineffect;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.shared.SharedHandler;

public class WinEffectCatRain extends WinEffect {

	private static WinEffectCatRain instance;

	private WinEffectCatRain() {
		super("cat-rain", 5);
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
				int xrand = ThreadLocalRandom.current().nextInt(-16,16);
				int zrand = ThreadLocalRandom.current().nextInt(-16,16);
				Location ranloc = player.getLocation().clone().add(xrand, 10, zrand);
				Ocelot ocelot = (Ocelot) player.getWorld().spawnEntity(ranloc, EntityType.OCELOT);
				Ocelot.Type types[] = {Ocelot.Type.BLACK_CAT,Ocelot.Type.RED_CAT,Ocelot.Type.SIAMESE_CAT};
				ocelot.setCatType(types[ThreadLocalRandom.current().nextInt(types.length)]);
				for(Player lp : player.getWorld().getPlayers()) {
					if(rythm == 0) {
						lp.playSound(lp.getLocation(), Sound.NOTE_BASS_DRUM, 10, 1);
						lp.playSound(lp.getLocation(), Sound.CAT_MEOW, 10, 1.2f);
						lp.playSound(lp.getLocation(), Sound.CAT_MEOW, 0.5f, 2.0f);
					}
					else if(rythm == 1) {
						lp.playSound(lp.getLocation(), Sound.NOTE_STICKS, 10, 1.5f);
						lp.playSound(lp.getLocation(), Sound.CAT_MEOW, 0.5f, 2.0f);
					}
					else if(rythm == 2) {
						lp.playSound(lp.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
						lp.playSound(lp.getLocation(), Sound.CAT_MEOW, 10, 0.8f);
						lp.playSound(lp.getLocation(), Sound.CAT_MEOW, 0.5f, 2.0f);
					}
					else if(rythm == 3) {
						lp.playSound(lp.getLocation(), Sound.NOTE_STICKS, 10, 1.5f);
						lp.playSound(lp.getLocation(), Sound.CAT_MEOW, 0.5f, 2.0f);
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
	
	public static WinEffectCatRain getInstance() {
		if(instance == null) {
			instance = new WinEffectCatRain();
		}
		return instance;
	}

}
