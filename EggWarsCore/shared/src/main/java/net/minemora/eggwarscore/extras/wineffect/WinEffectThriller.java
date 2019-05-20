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

public class WinEffectThriller extends WinEffect {

	private static WinEffectThriller instance;

	private WinEffectThriller() {
		super("thriller", 9);
	}

	@Override
	public void play(Player player) {
		for(int i = 0; i < 8; i++) {
			int xrand = ThreadLocalRandom.current().nextInt(-4,4);
			int zrand = ThreadLocalRandom.current().nextInt(-4,4);
			Location ranloc = player.getLocation().clone().add(xrand, 0, zrand);
			ranloc.setY(player.getWorld().getHighestBlockYAt(ranloc));
			ItemStack head = Utils.getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzNmYjRlNWRiOTdmNDc5YzY2YTQyYmJkOGE3ZDc4MWRhZjIwMWE4ZGRhZjc3YWZjZjRhZWY4Nzc3OWFhOGI0In19fQ==");
			Animation anim = new Animation("thriller");
			anim.play(ranloc, 3, 7, head);
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
						lp.playSound(lp.getLocation(), Sound.NOTE_BASS_GUITAR, 10, 1.0f);
					}
					else if(rythm == 1) {
						lp.playSound(lp.getLocation(), Sound.NOTE_STICKS, 10, 1.5f);
						lp.playSound(lp.getLocation(), Sound.NOTE_BASS_GUITAR, 10, 0.75f);
					}
					else if(rythm == 2) {
						lp.playSound(lp.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
						lp.playSound(lp.getLocation(), Sound.NOTE_BASS_GUITAR, 10, 0.9f);
					}
					else if(rythm == 3) {
						lp.playSound(lp.getLocation(), Sound.NOTE_STICKS, 10, 1.5f);
						lp.playSound(lp.getLocation(), Sound.NOTE_BASS_GUITAR, 10, 1.0f);
					}
					else if(rythm == 4) {
						lp.playSound(lp.getLocation(), Sound.NOTE_BASS_DRUM, 10, 1);
						lp.playSound(lp.getLocation(), Sound.NOTE_BASS_GUITAR, 10, 0.9f);
					}
					else if(rythm == 5) {
						lp.playSound(lp.getLocation(), Sound.NOTE_STICKS, 10, 1.5f);
						lp.playSound(lp.getLocation(), Sound.NOTE_BASS_GUITAR, 10, 0.75f);
					}
					else if(rythm == 6) {
						lp.playSound(lp.getLocation(), Sound.NOTE_SNARE_DRUM, 10, 1);
						lp.playSound(lp.getLocation(), Sound.NOTE_BASS_GUITAR, 10, 0.67f);
					}
					else if(rythm == 7) {
						lp.playSound(lp.getLocation(), Sound.NOTE_STICKS, 10, 1.5f);
						lp.playSound(lp.getLocation(), Sound.NOTE_BASS_GUITAR, 10, 0.75f);
					}	
        		}
				rythm++;
				if(rythm == 8) {
					rythm = 0;
				}
				count++;
				if(count > 36) {
					cancel();
				}
			}
		}.runTaskTimer(SharedHandler.getPlugin(), 0L, 5L);
	}
	
	public static WinEffectThriller getInstance() {
		if(instance == null) {
			instance = new WinEffectThriller();
		}
		return instance;
	}
}
