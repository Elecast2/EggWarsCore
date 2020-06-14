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
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.game.Game;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.network.GamesConnection;
import net.minemora.eggwarscore.utils.ChatUtils;

public class GamesMenu extends Menu {
	
	private String mode;
	
	private Map<Integer,Game> games = new HashMap<>();

	public GamesMenu(String mode) {
		super(0);
		this.mode = mode;
	}
	
	public void refresh() {  //TODO todo esto configurable para cuando vaya a vender el plugin
		for(int slot : games.keySet()) {
			Game game = games.get(slot);
			ItemMeta meta = getInventory().getItem(slot).getItemMeta();
			meta.setDisplayName(ChatUtils.format("&b&l" + game.getMode().toUpperCase() + "-" + (slot + 1)));
			List<String> lore = new ArrayList<>();
			lore.add("");
			if(game.isInGame()) {
				if(getInventory().getItem(slot).getDurability() != (short) 14) {
					getInventory().getItem(slot).setDurability((short) 14);
				}
				lore.add(ChatUtils.format("&c&lEN JUEGO"));
			}
			else if(game.isRestarting()){
				if(getInventory().getItem(slot).getDurability() != (short) 7) {
					getInventory().getItem(slot).setDurability((short) 7);
				}
				lore.add(ChatUtils.format("&8&lREINICIANDO"));
			}
			else if(game.getPlayerCount() >= GameManager.getMaxPlayers(game.getMode())/2) {
				if(getInventory().getItem(slot).getDurability() != (short) 4) {
					getInventory().getItem(slot).setDurability((short) 4);
				}
				lore.add(ChatUtils.format("&a&lENTRAR"));
			}
			else if(game.getPlayerCount() >= 1){
				if(getInventory().getItem(slot).getDurability() != (short) 5) {
					getInventory().getItem(slot).setDurability((short) 5);
				}
				lore.add(ChatUtils.format("&a&lENTRAR"));
			}
			else {
				if(getInventory().getItem(slot).getDurability() != (short) 0) {
					getInventory().getItem(slot).setDurability((short) 0);
				}
				lore.add(ChatUtils.format("&a&lENTRAR"));
			}
			if(game.getPlayerCount() >= 0){
				lore.add(ChatUtils.format("&f"+game.getPlayerCount()+"/" + GameManager.getMaxPlayers(game.getMode())));
				lore.add("");
			}
			meta.setLore(lore);
			getInventory().getItem(slot).setItemMeta(meta);
		}
	}
	
	public void update() {
		games.clear();
		int i = 0;
		for(GamesConnection connection : GameManager.getGames().get(mode)) {
			for(Game game : connection.getGames().values()) {
				games.put(i, game);
				i++;
			}
		}
		if(games.isEmpty()) {
			GameManager.getGamesMenus().remove(mode);
			setInventory(null);
			HandlerList.unregisterAll(this);
			return;
		}
		setBars(numberToBars(games.size()));
		Inventory inv = Bukkit.createInventory(null, 9*getBars(), mode.toUpperCase()); //TODO title from config
		for(int slot : games.keySet()) {
			inv.setItem(slot, new ItemStack(Material.WOOL)); //TODO material from config and depend of game status
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
			event.getWhoClicked().closeInventory();
			((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.CLICK, 0.5f, 1);
			GameManager.attemptToSendPlayer((Player)event.getWhoClicked(), games.get(event.getSlot()));
			
		}
	}
	
	public String getMode() {
		return mode;
	}

	public Map<Integer,Game> getGamesBySlot() {
		return games;
	}

}
