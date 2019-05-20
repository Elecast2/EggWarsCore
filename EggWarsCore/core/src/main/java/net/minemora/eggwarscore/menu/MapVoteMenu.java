package net.minemora.eggwarscore.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.arena.ArenaManager;
import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.game.GameLobby;
import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.Utils;

public class MapVoteMenu extends Menu {
	
	private GameLobby gameLobby;
	
	private Map<ItemStack, String> mapItems = new HashMap<>();

	public MapVoteMenu(GameLobby gameLobby) {
		super(numberToBars(gameLobby.getMapVotes().size()));
		this.gameLobby = gameLobby;
		setInventory(Bukkit.createInventory(null, 9*getBars(), ChatUtils.format(ConfigLang.get().getString("map-vote-menu.title"))));
		int i = 0;
		for(String mapName :  gameLobby.getMapVotes().keySet()) {
			ItemStack item = new ItemStack(Material.GRASS);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatUtils.format(ConfigLang.get().getString("map-vote-menu.display-name")
					.replaceAll("%map-name%", ArenaManager.getArena(mapName).getArenaName())));
			meta.setLore(ChatUtils.formatList(Utils.replaceAll(ConfigLang.get().getStringList("map-vote-menu.lore"), 
					new String[] {"%votes%"}, new String[] {String.valueOf(gameLobby.getMapVotes().get(mapName))})));
			item.setItemMeta(meta);
			mapItems.put(item, mapName);
			getInventory().setItem(i, item);
			i++;
		}
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
			for(ItemStack item : mapItems.keySet()) {
				if(event.getCurrentItem().equals(item)) {
					String mapName = mapItems.get(item);
					gameLobby.updateMapVotes(gp, mapName, 1*gp.getMapVotesMultiplier());//TODO aplicar multiplicador de rango
					break;
				}
				//TODO procurar que los votos se reduscan al desconectarse solo si esta en lobby
			}
			
			event.getWhoClicked().closeInventory();
		}
	}
	
	public void updateItem(String mapName) {
		ItemStack item = getItem(mapName);
		mapItems.remove(item);
		ItemMeta meta = item.getItemMeta();
		meta.setLore(ChatUtils.formatList(Utils.replaceAll(ConfigLang.get().getStringList("map-vote-menu.lore"),
				new String[] {"%votes%"}, new String[] {String.valueOf(gameLobby.getMapVotes().get(mapName))})));
		item.setItemMeta(meta);
		mapItems.put(item, mapName);
		int i = 0;
		for(String map :  gameLobby.getMapVotes().keySet()) {
			if(map.equals(mapName)) {
				break;
			}
			i++;
		}
		getInventory().setItem(i, item);
	}
	
	private ItemStack getItem(String mapName) {
		for(ItemStack item : mapItems.keySet()) {
			if(mapItems.get(item).equals(mapName)) {
				return item;
			}
		}
		return null;
	}

	public GameLobby getGameLobby() {
		return gameLobby;
	}

}
