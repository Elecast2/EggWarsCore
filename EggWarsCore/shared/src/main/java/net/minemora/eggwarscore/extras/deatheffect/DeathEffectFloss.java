package net.minemora.eggwarscore.extras.deatheffect;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.minemora.eggwarscore.asa.Animation;
import net.minemora.eggwarscore.utils.Utils;

public class DeathEffectFloss extends DeathEffect {
	
	private static DeathEffectFloss instance;

	private DeathEffectFloss() {
		super("floss", 8);
	}

	@Override
	public void play(Player player) {
		ItemStack head = Utils.getCustomTextureHead("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTM4OWNiMDAwNThmMWQxYjEwZDFiNGE5NjMzM2NhYjY5ODgzZTk0OGE1ZjcxMjEzYzQ5OGRlOTQzNTViNjIyZiJ9fX0=");
		Animation anim = new Animation("hilo");
		anim.play(player.getLocation(), 2, 1, head);
	}
	
	public static DeathEffectFloss getInstance() {
		if(instance == null) {
			instance = new DeathEffectFloss();
		}
		return instance;
	}

}
