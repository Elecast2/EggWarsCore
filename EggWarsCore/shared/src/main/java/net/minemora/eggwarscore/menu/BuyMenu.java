package net.minemora.eggwarscore.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.extras.Extra;
import net.minemora.eggwarscore.utils.ChatUtils;

public class BuyMenu extends Menu {
	
	private ItemStack acceptItem;
	private ItemStack canelItem;
	
	private Map<String, Extra> query = new HashMap<>();
	
	private static BuyMenu instance;

	private BuyMenu() {
		super(3);
		Inventory inv = Bukkit.createInventory(null, 9*getBars(), ChatUtils.format("&c&l¿Deseas comprarlo?"));
		
		ItemMeta meta;
		
		acceptItem = new ItemStack(Material.STAINED_GLASS, 1, (short)5); //TODO ITEMCONFIG
		meta = acceptItem.getItemMeta();
		meta.setDisplayName(ChatUtils.format("&a&lSI")); //TODO ITEMCONFIG
		acceptItem.setItemMeta(meta);
		
		canelItem = new ItemStack(Material.STAINED_GLASS, 1, (short)14); //TODO ITEMCONFIG
		meta = canelItem.getItemMeta();
		meta.setDisplayName(ChatUtils.format("&c&lNO")); //TODO ITEMCONFIG
		canelItem.setItemMeta(meta);
		
		inv.setItem(11, canelItem);
		inv.setItem(15, acceptItem);
		
		setInventory(inv);//TODO LANG
	}
	
	public void open(Player player, Extra extra) {
		player.playSound(player.getLocation(), Sound.CLICK, 10, 1);
		open(player);
		query.put(player.getName(), extra);
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
			if (event.getCurrentItem().equals(canelItem)) {
				query.remove(player.getName());
				player.closeInventory();
				return;
			}
			else if (event.getCurrentItem().equals(acceptItem)) {
				ps.buyExtra(query.get(player.getName()));
				query.remove(player.getName());
				player.closeInventory();
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if(event.getInventory().equals(getInventory())) {
			if(query.containsKey(event.getPlayer().getName())) {
				query.remove(event.getPlayer().getName());
			}
		}
	}
	
	public Map<String, Extra> getQuery(){
		return this.query;
	}
	
	public static BuyMenu getInstance() {
		if(instance == null) {
			instance = new BuyMenu();
		}
		return instance;
	}

}
