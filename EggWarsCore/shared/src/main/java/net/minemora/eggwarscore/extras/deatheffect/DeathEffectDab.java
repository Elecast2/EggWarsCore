package net.minemora.eggwarscore.extras.deatheffect;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minemora.eggwarscore.asa.Animation;
import net.minemora.eggwarscore.utils.Utils;

public class DeathEffectDab extends DeathEffect {
	
	private static DeathEffectDab instance;

	private DeathEffectDab() {
		super("dab", 7);
	}

	@Override
	public void play(Player player) {
		ItemStack head = Utils.getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjkzYjU3YmE0YWM2ZjA0MzE1ZmM0ODFkZTdhMjFlZjI3ZWNiNDhiYWIyNDM1OGEyZWM3MjIwMjBkYzVmYzg1ZSJ9fX0=");
		Animation anim = new Animation("dab");
		anim.play(player.getLocation(), 3, 1, head);
	}
	
	public static DeathEffectDab getInstance() {
		if(instance == null) {
			instance = new DeathEffectDab();
		}
		return instance;
	}

}
