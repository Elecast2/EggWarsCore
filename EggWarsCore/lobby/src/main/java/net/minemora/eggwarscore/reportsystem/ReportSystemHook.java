package net.minemora.eggwarscore.reportsystem;

import net.minemora.eggwarscore.EggWarsCoreLobby;


public class ReportSystemHook {
	
	private static boolean enabled = false;
	
	public static void setup() {
		if(EggWarsCoreLobby.getPlugin().getServer().getPluginManager().getPlugin("ReportSystem") == null) {
			return;
		}
		enabled = true;
	}
	
	public static boolean isEnabled() {
		return enabled;
	}

}
