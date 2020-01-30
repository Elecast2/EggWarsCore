package net.minemora.eggwarscore.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.game.GameLobby;
import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.utils.ChatUtils;

public class TimeVoteMenu extends Menu {
	
	private GameLobby gameLobby;
	
	private Map<ItemStack, String> timeItems = new HashMap<>();
	
	private Map<String, Integer> timeSlots = new HashMap<>();

	public TimeVoteMenu(GameLobby gameLobby) {
		super(3);
		this.gameLobby = gameLobby;
		
		//TODO FROM CONFIG
		
   		ItemStack item;
   		ItemMeta meta;
   		List<String> lore = new ArrayList<>();
		Inventory inv = Bukkit.createInventory(null, 9*getBars(), ChatUtils.format("&6&lHora"));
		
		item =  new ItemStack(Material.WATCH);
		meta = item.getItemMeta();
		meta.setDisplayName(ChatUtils.format("&b&lMedio Dia"));
   	    item.setItemMeta(meta);
		inv.setItem(10, item);
		timeItems.put(item, "day");
		timeSlots.put("day", 10);
		lore.clear();
		
		item =  new ItemStack(Material.WATCH);
		meta = item.getItemMeta();
		meta.setDisplayName(ChatUtils.format("&e&lAmanecer"));
   	    item.setItemMeta(meta);
		inv.setItem(13, item);
		timeItems.put(item, "morning");
		timeSlots.put("morning", 13);
		lore.clear();
			
		item =  new ItemStack(Material.WATCH);
		meta = item.getItemMeta();
		meta.setDisplayName(ChatUtils.format("&1&lMedia Noche"));
   	    item.setItemMeta(meta);
		inv.setItem(16, item);
		timeItems.put(item, "night");
		timeSlots.put("night", 16);
		lore.clear();
		setInventory(inv);
		
		updateItem("day");
		updateItem("night");
		updateItem("morning");
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
			if(gameLobby.getGame() != null) {
				return;
			}
			GamePlayer gp = GamePlayer.get(event.getWhoClicked().getName());
			for(ItemStack item : timeItems.keySet()) {
				if(event.getCurrentItem().equals(item)) {
					String timeName = timeItems.get(item);
					gameLobby.updateTimeVotes(gp, timeName, 1);
					break;
				}
				//TODO procurar que los votos se reduscan al desconectarse solo si esta en lobby
			}
			((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.CLICK, 0.5f, 1);
			event.getWhoClicked().closeInventory();
		}
	}
	
	public void updateItem(String timeName) {
		ItemStack item = getItem(timeName);
		timeItems.remove(item);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatUtils.format("&7Votos: &a" + gameLobby.getTimeVotes().get(timeName))); //TODO LANG
		meta.setLore(lore);
		item.setItemMeta(meta);
		timeItems.put(item, timeName);
		getInventory().setItem(timeSlots.get(timeName), item);
	}
	
	private ItemStack getItem(String timeName) {
		for(ItemStack item : timeItems.keySet()) {
			if(timeItems.get(item).equals(timeName)) {
				return item;
			}
		}
		return null;
	}

}
