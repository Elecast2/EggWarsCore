package net.minemora.eggwarscore.npc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;

import net.minemora.eggwarscore.config.ConfigNPC;

public final class NPCManager {
	
	private static Map<Integer, NPC> npcs = new HashMap<>();
	
	public static final String DEF_VALUE = "eyJ0aW1lc3RhbXAiOjE1NTQ2NTkwMjkyODYsInByb2ZpbGVJZCI6IjA2OWE3OWY0NDRlOTQ3MjZhNWJlZmNhOTBlMzhhYWY1IiwicHJvZmlsZU5hbWUiOiJOb3RjaCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjkyMDA5YTQ5MjViNThmMDJjNzdkYWRjM2VjZWYwN2VhNGM3NDcyZjY0ZTBmZGMzMmNlNTUyMjQ4OTM2MjY4MCJ9fX0=";
	public static final String DEF_SIGN = "puRjXMI7CCw3ifxBUSRET2pSn4y99tEgHtZlT4ch2StxpWw3b2lt7a+bdratjdUkMHYe754XR1+OPnLoFOa7BvF/pTJY5CpJA8abxI2GEmyEvJtJKXLZm6GSuwc7MMO6c8DVJ3A+SNY4s1A+9wa4RQXQ0KzfT0AMH78MCtEdWzMYwRaOMXKsKqskOGCweur82uXLb8uC3Dvu+Voc0s+EHNxP5vWY/xdwO77mmfUBkyYNkUV1sJ3Y5P4qFqPv+yyTsklr8cHQ/WDvdshurLbiH9VgSdCeUXcQGNibeHo8arOSZxU+nde4OZ6cJgRuo7X4PInqj9C8MpDZAHulXIk9xXut6tfVcvuI8nWEsG26VcUsqqXbysc0JNeNVyJYrfnxoIohNyERtOG3+wQuKxirqkPjzrGmsWSc/+1TOBcBHdlFotuVKD6Dz/iIVxUpzVQqkjYmUhOGkydDM1knddf02uMSTqsY6K5LE8P6YtHIecZ9F7EMT8VVNXS4Cr6eZm8qAEQMniHWf61YDPoGdlUM44fkeXNUQLrJvfgHon6ohwP97nPramW984JnllwYC/TBBHUslOgMADEeWCUX+LEUBNERgqQGWclkLPYLx0EkTWbABFL/XbpeDgo9gnFhav+feAdmIqf6DK2EZ4vp0PiEjuykKmFDIYOssqr4VPNhmXs=";
	
	private NPCManager() {}
	
	public static void loadNPCs() {
		if (getNPCList().isEmpty()) {
			return;
		}
		npcs.clear();
		for (String path : getNPCList()) {
			NPC npc = NPC.deserealize(path);
			npcs.put(npc.getId(), npc);
		}
	}
	
	public static void addNPC(String path, Location loc) {
		NPC npc = new NPC(loc);
		npc.setPath(path);
		npc.setValue(DEF_VALUE);
		npc.setSignature(DEF_SIGN);
		npc.serealize();
		npcs.put(npc.getId(), npc);
	}
	
	public static Set<String> getNPCList() {
		Set<String> npcList = new HashSet<>();
		if (ConfigNPC.get().get("npc") != null) {
			for (String id : ConfigNPC.get().getConfigurationSection("npc").getValues(false).keySet()) {
				npcList.add(id);
			}
		}
		return npcList;
	}

	public static Map<Integer, NPC> getNPCs() {
		return npcs;
	}
	
	public static NPC getNPC(String path) {
		for(NPC npc : npcs.values()) {
			if(npc.getPath().equals(path)) {
				return npc;
			}
		}
		return null;
	}

}
