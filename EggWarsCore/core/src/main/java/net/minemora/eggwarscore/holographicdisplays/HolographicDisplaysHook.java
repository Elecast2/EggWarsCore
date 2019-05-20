package net.minemora.eggwarscore.holographicdisplays;

import org.bukkit.Location;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.game.GameArena;
import net.minemora.eggwarscore.utils.ChatUtils;

public final class HolographicDisplaysHook {
	
	private static boolean enabled = false;
	
	private HolographicDisplaysHook() {}
	
	public static void register() {
		if (EggWarsCore.getPlugin().getServer().getPluginManager().getPlugin("HolographicDisplays") == null) {
			return;
		}
		if(!ConfigMain.get().getBoolean("general.holographic-displays")) {
			return;
		}
		enabled = true;
	}
	
	public static void loadShopHolograms(GameArena gameArena) {
		if(!enabled) {
			return;
		}
		for(Location loc : gameArena.getShops()) {
			Hologram hologram = HologramsAPI.createHologram(EggWarsCore.getPlugin(), loc.add(0, 2.7, 0));
			hologram.appendTextLine(ChatUtils.format("&6&lTIENDA"));  //TODO LANG
			hologram.appendTextLine(ChatUtils.format("&7(Click derecho)"));
		}
	}

	public static boolean isEnabled() {
		return enabled;
	}
}