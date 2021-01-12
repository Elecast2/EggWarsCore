package net.minemora.eggwarscore.moraparty;

import net.minemora.eggwarscore.EggWarsCoreLobby;

public class MoraPartyHook {
	
	private static boolean enabled = false;
	
	public static void setup() {
		if(EggWarsCoreLobby.getPlugin().getServer().getPluginManager().getPlugin("MoraParty") == null) {
			return;
		}
		enabled = true;
	}
	
	public static boolean isEnabled() {
		return enabled;
	}

}
