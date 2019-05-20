package net.minemora.eggwarscore.uc;

import org.bukkit.entity.Player;

import be.isach.ultracosmetics.UltraCosmeticsData;
import net.minemora.eggwarscore.EggWarsCoreLobby;

public final class UltraCosmeticsHook {
	
	private static boolean enabled = false;
	
	private UltraCosmeticsHook() {}
	
	public static void setup() {
		
		if (EggWarsCoreLobby.getPlugin().getServer().getPluginManager().getPlugin("UltraCosmetics") == null) {
			return;
		}
		
		//TODO FROM CONFIG
		/*
		if(!ConfigMain.get().getBoolean("general.ultracosmetics")) {
			return;
		}
		*/
		enabled = true;
	}
	
	public static void giveItem(Player player) {
		if(!enabled) {
			return;
		}
		UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(player).giveMenuItem();
	}
	
	public static void removeItem(Player player) {
		if(!enabled) {
			return;
		}
		UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(player).removeMenuItem();
	}
	
	public static void clear(Player player) {
		if(!enabled) {
			return;
		}
		UltraCosmeticsData.get().getPlugin().getPlayerManager().getUltraPlayer(player).clear();
	}

	public static boolean isEnabled() {
		return enabled;
	}

}
