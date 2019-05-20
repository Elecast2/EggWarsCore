package net.minemora.eggwarscore.extras.wineffect;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.asa.Animation;
import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.utils.Utils;

public class WinEffectDancers extends WinEffect {

	private static WinEffectDancers instance;

	private WinEffectDancers() {
		super("dancers", 8);
	}

	@Override
	public void play(Player player) {
		for(int i = 0; i < 30; i++) {
			int xrand = ThreadLocalRandom.current().nextInt(-16,16);
			int zrand = ThreadLocalRandom.current().nextInt(-16,16);
			Location ranloc = player.getLocation().clone().add(xrand, 0, zrand);
			ranloc.setY(player.getWorld().getHighestBlockYAt(ranloc));
			ranloc.setYaw(ThreadLocalRandom.current().nextInt(0,360));
			boolean dance = ThreadLocalRandom.current().nextBoolean();
			if(dance) {
				ItemStack head = Utils.getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM4OWNiMDAwNThmMWQxYjEwZDFiNGE5NjMzM2NhYjY5ODgzZTk0OGE1ZjcxMjEzYzQ5OGRlOTQzNTViNjIyZiJ9fX0=");
				Animation anim = new Animation("hilo");
				anim.play(ranloc, 2, 5, head);
			}
			else {
				ItemStack head = Utils.getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdjYmU0MjM3ZDRkNzgyNDRjMzZjZTk1Mjg0NjZhMDVkYWM0NzJmOGEzZmE0YzBmNjc5NzdhZTE4Y2NmMzNhYyJ9fX0=");
				Animation anim = new Animation("hype");
				anim.play(ranloc, 2, 5, head);
			}
		}
		new BukkitRunnable() {
			int count = 0;
			int rythm = 0;
			@Override
			public void run() {
				if(player.getWorld().equals(Bukkit.getWorlds().get(0))) {
					cancel();
					return;
				}
				for(Player lp : player.getWorld().getPlayers()) {
					if(rythm == 0) {
						lp.playSound(lp.getLocation(), Sound.NOTE_BASS_DRUM, 10, 1);
					}
					else if(rythm == 1) {
						lp.playSound(lp.getLocation(), Sound.NOTE_STICKS, 10, 1.5f);
					}
					else if(rythm == 2) {
						lp.playSound(lp.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
					}
					else if(rythm == 3) {
						lp.playSound(lp.getLocation(), Sound.NOTE_STICKS, 10, 1.5f);
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
	
	public static WinEffectDancers getInstance() {
		if(instance == null) {
			instance = new WinEffectDancers();
		}
		return instance;
	}
}
