package net.minemora.eggwarscore.npc;

import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.config.ConfigNPC;
import net.minemora.eggwarscore.shared.SharedHandler;

public class NPC {
	
	private static int counter = 0;
	
	private final int id;
	private String value;
	private String signature;
	private Location location;
	private String path;
	
	public NPC(Location location) {
		counter++;
		this.id = counter;
		this.location = location;
	}
	
	public void spawn(Set<Player> players) {
		for(Player player : players) {
			spawn(player);
		}
	}
	
	public void spawn(Player player) {
		SharedHandler.getNmsHandler().createNPC(EggWarsCoreLobby.getPlugin(), player, id, location, value, signature);
	}
	
	public static NPC deserealize(String path) {
		Location loc = Location.deserialize(ConfigNPC.get().getConfigurationSection("npc." + path + ".location").getValues(false));
		String val = ConfigNPC.get().getString("npc." + path + ".value");
		String sig = ConfigNPC.get().getString("npc." + path + ".signature");
		if(val.isEmpty()) {
			val = "eyJ0aW1lc3RhbXAiOjE1NTQ2NTkwMjkyODYsInByb2ZpbGVJZCI6IjA2OWE3OWY0NDRlOTQ3MjZhNWJlZmNhOTBlMzhhYWY1IiwicHJvZmlsZU5hbWUiOiJOb3RjaCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjkyMDA5YTQ5MjViNThmMDJjNzdkYWRjM2VjZWYwN2VhNGM3NDcyZjY0ZTBmZGMzMmNlNTUyMjQ4OTM2MjY4MCJ9fX0=";
		}
		if(val.isEmpty()) {
			sig = "puRjXMI7CCw3ifxBUSRET2pSn4y99tEgHtZlT4ch2StxpWw3b2lt7a+bdratjdUkMHYe754XR1+OPnLoFOa7BvF/pTJY5CpJA8abxI2GEmyEvJtJKXLZm6GSuwc7MMO6c8DVJ3A+SNY4s1A+9wa4RQXQ0KzfT0AMH78MCtEdWzMYwRaOMXKsKqskOGCweur82uXLb8uC3Dvu+Voc0s+EHNxP5vWY/xdwO77mmfUBkyYNkUV1sJ3Y5P4qFqPv+yyTsklr8cHQ/WDvdshurLbiH9VgSdCeUXcQGNibeHo8arOSZxU+nde4OZ6cJgRuo7X4PInqj9C8MpDZAHulXIk9xXut6tfVcvuI8nWEsG26VcUsqqXbysc0JNeNVyJYrfnxoIohNyERtOG3+wQuKxirqkPjzrGmsWSc/+1TOBcBHdlFotuVKD6Dz/iIVxUpzVQqkjYmUhOGkydDM1knddf02uMSTqsY6K5LE8P6YtHIecZ9F7EMT8VVNXS4Cr6eZm8qAEQMniHWf61YDPoGdlUM44fkeXNUQLrJvfgHon6ohwP97nPramW984JnllwYC/TBBHUslOgMADEeWCUX+LEUBNERgqQGWclkLPYLx0EkTWbABFL/XbpeDgo9gnFhav+feAdmIqf6DK2EZ4vp0PiEjuykKmFDIYOssqr4VPNhmXs=";
		}
		NPC npc = new NPC(loc);
		npc.setValue(val);
		npc.setSignature(sig);
		npc.setPath(path);
		return npc;
	}
	
	public void serealize() {
		ConfigurationSection npcs = null;
		if (ConfigNPC.get().get("npc") == null) {
			npcs = ConfigNPC.get().createSection("npc");
		}
		else {
			npcs = ConfigNPC.get().getConfigurationSection("npc");
		}
		if (!npcs.contains(path)) {
			npcs.createSection(path);
		}
		ConfigurationSection npcSection = npcs.getConfigurationSection(path);
		
		npcSection.set("value", value);
		npcSection.set("signature", signature);
		if (!npcSection.contains("location")) {
			npcSection.createSection("location");
		}
		npcSection.set("location.world", location.getWorld().getName());
		npcSection.set("location.x", location.getX());
		npcSection.set("location.y", location.getY());
		npcSection.set("location.z", location.getZ());
		npcSection.set("location.pitch", location.getPitch());
		npcSection.set("location.yaw", location.getYaw());
		ConfigNPC.getInstance().save();
	}

	public int getId() {
		return id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
