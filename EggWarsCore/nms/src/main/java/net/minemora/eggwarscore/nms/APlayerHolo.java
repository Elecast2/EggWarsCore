package net.minemora.eggwarscore.nms;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class APlayerHolo {
	
	private Location location;
	private String playerName;
	private String text;
	
	public APlayerHolo(Player player, Location location, String text) {
		this.location = location;
		this.text = text;
		this.playerName = player.getName();
	}
	
	public void updateText(String text) {
		this.text = text;
		updateTextNMS(text);
	}
	
	protected abstract void updateTextNMS(String text);

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getText() {
		return text;
	}
}
