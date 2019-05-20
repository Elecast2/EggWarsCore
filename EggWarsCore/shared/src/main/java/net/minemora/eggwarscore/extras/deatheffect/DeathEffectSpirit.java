package net.minemora.eggwarscore.extras.deatheffect;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minemora.eggwarscore.asa.Animation;
import net.minemora.eggwarscore.utils.Utils;


public class DeathEffectSpirit extends DeathEffect {
	
	private static DeathEffectSpirit instance;

	private DeathEffectSpirit() {
		super("spirit", 5);
	}

	@Override
	public void play(Player player) {
		ItemStack head = Utils.getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2UxZGViYzczMjMxZjhlZDRiNjlkNWMzYWMxYjFmMThmMzY1NmE4OTg4ZTIzZjJlMWJkYmM0ZTg1ZjZkNDZhIn19fQ==");
		Animation anim = new Animation("angel");
		anim.play(player.getLocation(), 3, 1, head);
	}
	
	public static DeathEffectSpirit getInstance() {
		if(instance == null) {
			instance = new DeathEffectSpirit();
		}
		return instance;
	}

}
