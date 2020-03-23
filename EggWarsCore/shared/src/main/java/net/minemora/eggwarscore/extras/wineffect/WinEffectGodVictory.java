package net.minemora.eggwarscore.extras.wineffect;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.asa.Animation;
import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.utils.Utils;

public class WinEffectGodVictory extends WinEffect {
	
	private static WinEffectGodVictory instance;

	private WinEffectGodVictory() {
		super("godvictory", 10);
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
				if(count > 6) {
					cancel();
				}
			}
		}.runTaskTimer(SharedHandler.getPlugin(), 10L, 30L);
		new BukkitRunnable(){
			int count = 0;
			Location playerloc = player.getLocation();
			@Override
			public void run() {
				if(player.getWorld().equals(Bukkit.getWorlds().get(0))) {
					cancel();
					return;
				}
				if(player.isOnline()) {
					playerloc = player.getLocation();
				}
				Firework fw = (Firework) playerloc.getWorld().spawn(playerloc, Firework.class);
				FireworkMeta fmeta = fw.getFireworkMeta();
				fmeta.addEffect(FireworkEffect.builder().with(Type.BALL).withColor
						(Color.YELLOW, Color.WHITE).withFade(Color.ORANGE).build());
				fmeta.setPower(2);
				fw.setFireworkMeta(fmeta);
				count++;
				if(count > 5) {
					cancel();
					return;
				}
			}
		}.runTaskTimer(SharedHandler.getPlugin(), 0L, 40L);
		for(int i = 0; i < 30; i++) {
			int xrand = ThreadLocalRandom.current().nextInt(-20,20);
			int zrand = ThreadLocalRandom.current().nextInt(-20,20);
			Location ranloc = player.getLocation().clone().add(xrand, 0, zrand);
			ranloc.setY(player.getWorld().getHighestBlockYAt(ranloc));
			ranloc.setYaw(ThreadLocalRandom.current().nextInt(0,360));
			ItemStack head = Utils.getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzY4ZWExM2ZiOGYzNGU0NDdkYmFiZGNjYzhmNzMzODU1MGQ4MTk1NjEzOTQ5NWFkZTA1MWQ0ZjM4ZGQ2NjJlIn19fQ==");
			Animation anim = new Animation("angel");
			anim.play(ranloc, 4, 2, head);
		}
	}
	
	public static WinEffectGodVictory getInstance() {
		if(instance == null) {
			instance = new WinEffectGodVictory();
		}
		return instance;
	}

}
