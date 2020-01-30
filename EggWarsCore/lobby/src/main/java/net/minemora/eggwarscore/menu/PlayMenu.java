package net.minemora.eggwarscore.menu;

import java.util.HashMap;
import java.util.Map;

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
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.ItemConfig;

public class PlayMenu extends Menu {
	
	public static PlayMenu menu;
	
	private Map<ItemStack,String> modes = new HashMap<>();
	private Map<ItemStack,String> modesQuick = new HashMap<>();

	private PlayMenu(int bars) {
		super(bars);
		Inventory inv = Bukkit.createInventory(null, 9*(getBars()), ChatUtils.format(ConfigMain.get().getString("play-menu.title")));
		for(String mode : ConfigMain.get().getConfigurationSection("modes").getValues(false).keySet()) {
			ItemStack item = new ItemConfig(ConfigMain.getInstance(), "modes." + mode  + ".item").getItem();
			inv.setItem(ConfigMain.get().getInt("modes." + mode + ".slot"), item);
			modes.put(item,mode);
			
			ItemStack quickItem = new ItemConfig(ConfigMain.getInstance(), "modes." + mode  + ".quick-join-item").getItem();
			inv.setItem(ConfigMain.get().getInt("modes." + mode + ".quick-join-item.slot"), quickItem);
			modesQuick.put(quickItem, mode);
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
			for(ItemStack item : modes.keySet()) {
				if(event.getCurrentItem().equals(item)) {
					if(!GameManager.getGamesMenus().containsKey(modes.get(item))) {
						player.closeInventory();
						player.sendMessage("no hay juegos disponibles"); //TODO from lang
						player.playSound(player.getLocation(), Sound.NOTE_BASS, 0.5f, 1);
						return;
					}
					GameManager.getGamesMenus().get(modes.get(item)).open(player);
					player.playSound(player.getLocation(), Sound.CLICK, 0.5f, 1);
					return;
				}
			}
			for(ItemStack item : modesQuick.keySet()) {
				if(event.getCurrentItem().equals(item)) {
					GameManager.attemptToSendPlayer(player, GameManager.getQuickGame(modesQuick.get(item)));
					player.playSound(player.getLocation(), Sound.CLICK, 0.5f, 1);
					return;
				}
			}
		}
	}
	
	
	public static PlayMenu getMenu() {
		if(menu == null) {
			menu = new PlayMenu(ConfigMain.get().getInt("play-menu.bars"));
		}
		return menu;
	}
}
