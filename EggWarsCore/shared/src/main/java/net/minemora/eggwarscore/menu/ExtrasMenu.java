package net.minemora.eggwarscore.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.ItemConfig;

public class ExtrasMenu extends Menu {
	
	public static ExtrasMenu menu;
	
	private ItemStack trailItem;
	private ItemStack deathEffectItem;
	private ItemStack winEffectItem;

	private ExtrasMenu(int bars) {
		super(bars);
		Inventory inv = Bukkit.createInventory(null, 9*(getBars()), ChatUtils.format(ConfigMain.get().getString("extras.menu-title")));
		for (String id : ConfigMain.get().getConfigurationSection("extras.items").getValues(false).keySet()) {
			if(ConfigMain.get().getBoolean("extras.items." + id + ".enabled")) {
				ItemStack item = new ItemConfig(ConfigMain.getInstance(), "extras.items." + id ).getItem();
				inv.setItem(ConfigMain.get().getInt("extras.items." + id + ".slot"), item);
				switch(id) {
				case "trails":
					trailItem = item;
					break;
				case "death-effects":
					deathEffectItem = item;
					break;
				case "win-effects":
					winEffectItem = item;
					break;
				}
			}
		}
		setInventory(inv);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getClickedInventory() == null) {
			return;
		}
	    if (event.getClick().isShiftClick()) {
	        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {
	        	InventoryView iv = event.getWhoClicked().getOpenInventory();
	        	if(iv.getTopInventory() != null) {
	        		if(iv.getTopInventory().equals(getInventory())) {
		        		event.setCancelled(true);
		        	}
	        	}
	        }
	    }
		if (event.getClickedInventory().equals(getInventory())) {
			if(event.getCurrentItem() == null) {
				return;
			}
			if(event.getCurrentItem().getType() == Material.AIR) {
				return;
			}
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			PlayerStats ps = PlayerStats.get(player.getName());
			if (event.getCurrentItem().equals(trailItem)) {
				player.playSound(player.getLocation(), Sound.valueOf(ConfigMain.get().getString("extras.open-menu-sound")), 0.5f, 1);
				ps.getTrailMenu().open(player);
			}
			else if (event.getCurrentItem().equals(deathEffectItem)) {
				player.playSound(player.getLocation(), Sound.valueOf(ConfigMain.get().getString("extras.open-menu-sound")), 0.5f, 1);
				ps.getDeathEffectMenu().open(player);
			}
			else if (event.getCurrentItem().equals(winEffectItem)) {
				player.playSound(player.getLocation(), Sound.valueOf(ConfigMain.get().getString("extras.open-menu-sound")), 0.5f, 1);
				ps.getWinEffectMenu().open(player);
			}
		}
	}
	
	
	public static ExtrasMenu getMenu() {
		if(menu == null) {
			menu = new ExtrasMenu(ConfigMain.get().getInt("extras.bars"));
		}
		return menu;
	}
}