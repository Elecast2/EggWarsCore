package net.minemora.eggwarscore.extras.deatheffect;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minemora.eggwarscore.asa.Animation;
import net.minemora.eggwarscore.utils.InstantFirework;
import net.minemora.eggwarscore.utils.Utils;

public class DeathEffectGodDeath extends DeathEffect {
	
	private static DeathEffectGodDeath instance;

	private DeathEffectGodDeath() {
		super("goddeath", 10);
	}

	@Override
	public void play(Player player) {
		player.getWorld().strikeLightningEffect(player.getLocation());
		new InstantFirework(FireworkEffect.builder().with(Type.BALL).withColor(Color.YELLOW, Color.WHITE).withFade(Color.ORANGE).build(), player.getLocation());
		ItemStack head = Utils.getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzY4ZWExM2ZiOGYzNGU0NDdkYmFiZGNjYzhmNzMzODU1MGQ4MTk1NjEzOTQ5NWFkZTA1MWQ0ZjM4ZGQ2NjJlIn19fQ==");
		Animation anim = new Animation("angel");
		anim.play(player.getLocation(), 4, 1, head);
		player.getWorld().playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1, 0.2f);
	}
	
	public static DeathEffectGodDeath getInstance() {
		if(instance == null) {
			instance = new DeathEffectGodDeath();
		}
		return instance;
	}

}
