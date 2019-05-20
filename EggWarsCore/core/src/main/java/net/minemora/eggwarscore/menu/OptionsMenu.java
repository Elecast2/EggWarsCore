package net.minemora.eggwarscore.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.utils.ChatUtils;

public final class OptionsMenu extends Menu {
	
	private static OptionsMenu instance;
	
	private ItemStack timeItem;

	private OptionsMenu() {
		super(3);
		Inventory inv = Bukkit.createInventory(null, 9*getBars(), ChatUtils.format("&6&lOpciones")); //TODO LANG
		
		
		//TODO ITEMS FROM CONFIG
   		List<String> lore = new ArrayList<>();
   		
   		ItemStack item;
   		ItemMeta meta;
		
		item =  new ItemStack(Material.WATCH);
		meta = item.getItemMeta();
		meta.setDisplayName(ChatUtils.format("&6&lHora"));
		lore.add("");
		lore.add(ChatUtils.format("&7Elige la hora de la partida"));
		lore.add(ChatUtils.format("&7Solo para &b&lMMC &7y &a&lMMC&e&l+"));
		lore.add("");
		meta.setLore(lore);
   	    item.setItemMeta(meta);
		inv.setItem(13, item);
		lore.clear();
		
		timeItem = item;
		
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
			if(event.getCurrentItem().equals(timeItem)) {
				Player player = (Player)event.getWhoClicked();
				if(GamePlayer.get(player.getName()) != null) {
					GamePlayer.get(player.getName()).getGameLobby().getTimeVoteMenu().open(player);
				}
			}
		}
	}
	
	public static OptionsMenu getInstance() {
		if(instance == null) {
			instance = new OptionsMenu();
		}
		return instance;
	}

}
