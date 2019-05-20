package net.minemora.eggwarscore.extras.deatheffect;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minemora.eggwarscore.asa.Animation;
import net.minemora.eggwarscore.utils.Utils;

public class DeathEffectClap extends DeathEffect {
	
	private static DeathEffectClap instance;

	private DeathEffectClap() {
		super("clap", 6);
	}

	@Override
	public void play(Player player) {
		ItemStack head = Utils.getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2JhYWJlNzI0ZWFlNTljNWQxM2Y0NDJjN2RjNWQyYjFjNmI3MGMyZjgzMzY0YTQ4OGNlNTk3M2FlODBiNGMzIn19fQ==");
		Animation anim = new Animation("claps");
		anim.play(player.getLocation(), 1, 3, head);
	}
	
	public static DeathEffectClap getInstance() {
		if(instance == null) {
			instance = new DeathEffectClap();
		}
		return instance;
	}

}
