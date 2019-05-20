package net.minemora.eggwarscore.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import net.minemora.eggwarscore.listener.EggWarsListener;
import net.minemora.eggwarscore.shared.SharedHandler;

public class Menu extends EggWarsListener {
	
	private Inventory inventory;
	private int bars;
	
	public Menu(int bars) {
		super(SharedHandler.getPlugin());
		this.setBars(bars);
	}
	
	public void open(Player player) {
		player.openInventory(inventory);
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public int getBars() {
		return bars;
	}

	public void setBars(int bars) {
		this.bars = bars;
	}
	
	public static int numberToBars(int i) {
		return ((i-1)/9)+1;
	}
}