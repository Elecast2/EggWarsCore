package net.minemora.eggwarscore.extras.deatheffect;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minemora.eggwarscore.asa.Animation;
import net.minemora.eggwarscore.utils.Utils;

public class DeathEffectHype extends DeathEffect {
	
	private static DeathEffectHype instance;

	private DeathEffectHype() {
		super("hype", 9);
	}

	@Override
	public void play(Player player) {
		ItemStack head = Utils.getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDdjYmU0MjM3ZDRkNzgyNDRjMzZjZTk1Mjg0NjZhMDVkYWM0NzJmOGEzZmE0YzBmNjc5NzdhZTE4Y2NmMzNhYyJ9fX0=");
		Animation anim = new Animation("hype");
		anim.play(player.getLocation(), 2, 1, head);
	}
	
	public static DeathEffectHype getInstance() {
		if(instance == null) {
			instance = new DeathEffectHype();
		}
		return instance;
	}

}
