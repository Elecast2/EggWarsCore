package net.minemora.eggwarscore.extras.wineffect;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.shared.SharedHandler;

public class WinEffectFirework extends WinEffect {
	
	private static WinEffectFirework instance;

	private WinEffectFirework() {
		super("fireworks", 1);
	}

	@Override
	public void play(Player player) {
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
				if(count == 0 || count == 8 || count == 16) {
					for(int x = -2; x<=2 ; x++) {
						for(int y = -2; y<=2 ; y++) {
							if(x==2 || x==-2 || y==2 || y==-2) {
								Location loc = player.getLocation().add(x, 0, y);
								Firework fw = (Firework) playerloc.getWorld().spawn(loc, Firework.class);
								FireworkMeta fmeta = fw.getFireworkMeta();
								fmeta.addEffect(FireworkEffect.builder().with(Type.BURST).withColor(
										Color.RED).withFlicker().withFade(Color.YELLOW).build());
								fmeta.setPower(1);
								fw.setFireworkMeta(fmeta);
							}
						}
					}
				}
				else if(count == 6 || count == 14) {
					Firework fw = (Firework) playerloc.getWorld().spawn(playerloc, Firework.class);
					FireworkMeta fmeta = fw.getFireworkMeta();
					fmeta.addEffect(FireworkEffect.builder().with(Type.BALL_LARGE).withColor
							(Color.PURPLE, Color.WHITE, Color.FUCHSIA).withFade(Color.YELLOW).build());
					fmeta.setPower(3);
					fw.setFireworkMeta(fmeta);
				}
				else {
					Firework fw = (Firework) playerloc.getWorld().spawn(playerloc, Firework.class);
					FireworkMeta fmeta = fw.getFireworkMeta();
					fmeta.addEffect(FireworkEffect.builder().with(Type.BALL).withColor
							(Color.GREEN, Color.LIME).withFade(Color.ORANGE).build());
					fmeta.setPower(2);
					fw.setFireworkMeta(fmeta);
				}
				count++;
				if(count > 16) {
					cancel();
					return;
				}
			}
		}.runTaskTimer(SharedHandler.getPlugin(), 0L, 10L);
	}

	public static WinEffectFirework getInstance() {
		if(instance == null) {
			instance = new WinEffectFirework();
		}
		return instance;
	}
}
