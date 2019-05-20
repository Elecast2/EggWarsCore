package net.minemora.eggwarscore.menu;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.bungee.BungeeHandler;
import net.minemora.eggwarscore.bungee.BungeeListener;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.utils.ChatUtils;

public class LobbiesMenu extends Menu {
	
	private static LobbiesMenu instance;
	
	private int current;
	
	private int[] layout;
	
	private Map<Integer,String> lobbies = new HashMap<>();

	private LobbiesMenu(Map<Integer,String> lobbies) {
		super(lobbies.size() <= 5 ? 3 : numberToBars(lobbies.size()));
		this.lobbies = lobbies;
		this.current = ConfigMain.get().getInt("lobbies-menu.current-lobby");
		switch (lobbies.size()) {
		case 5:
			layout = new int[]{ 9, 11, 13, 15, 17 };
			break;
		case 4:
			layout = new int[]{ 10, 12, 14, 16 };
			break;
		case 3:
			layout = new int[]{ 11, 13, 15 };
			break;
		case 2:
			layout = new int[]{ 11, 15 };
			break;
		case 1:
			layout = new int[]{ 13 };
			break;
		default:
			layout = new int[lobbies.size()];
			for(int i = 0; i < lobbies.size(); i++) {
				layout[i] = i;
			}
			break;
		}
		Inventory inv = Bukkit.createInventory(null, 9*getBars(), "Lobbies"); //TODO LANG
		for(int i : lobbies.keySet()) {  //TODO items configurables
			ItemStack item;
			if(i == current) {
				item = new ItemStack(Material.STAINED_GLASS, i, (short) 5);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatUtils.format("&bLobby " + i));
				meta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
		    	meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
			}
			else {
				item = new ItemStack(Material.STAINED_GLASS, i, (short) 0);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(ChatUtils.format("&bLobby " + i));
				item.setItemMeta(meta);
			}
			inv.setItem(layout[i-1], item);
		}
		setInventory(inv);
	}
	
	public void updateItems() {
		for(int i : lobbies.keySet()) {
			if(i == current) {
				updateItem(i, Bukkit.getOnlinePlayers().size());
			}
			else {
				BungeeHandler.getPlayerCount(lobbies.get(i), "RedisBungee"); //TODO if redis enabled instead bungee
			}
		}
	}
	
	public void updateItem(int id, int playerCount) {
		ItemMeta meta = getInventory().getItem(layout[id-1]).getItemMeta();
		if(id == current) {
			meta.setLore(Arrays.asList(ChatUtils.format(new String[]{ //TODO LANG
					"",
					"&e¡Ya estas aqui!",
					"",
					"&9Jugadores: &a" + playerCount + "/100",
					""
			})));
		}
		else {
			meta.setLore(Arrays.asList(ChatUtils.format(new String[]{ //TODO LANG
					"",
					"&a▶ ¡Click para entrar!",
					"",
					"&9Jugadores: &a" + playerCount + "/100",
					""
			})));
		}
		getInventory().getItem(layout[id-1]).setItemMeta(meta);
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
			
			for(int i = 0; i < layout.length; i++) {
				if(event.getSlot() == layout[i]) {
					if(current == i+1) {
						event.getWhoClicked().sendMessage(ChatUtils.format("&c¡Ya te encuentras conectado a ese lobby!")); //TODO LANG or BASS
					}
					else {
						BungeeHandler.sendPlayer((Player) event.getWhoClicked(), lobbies.get(i+1));
					}
					break;
				}
			}
		}
	}
	
	public Map<Integer, String> getLobbies() {
		return lobbies;
	}
	
	public static LobbiesMenu getInstance() {
		if(instance == null) {
			BungeeListener.getInstance().setup(EggWarsCoreLobby.getPlugin(), "RedisBungee"); //TODO QUITAR IS SE USA EN OTRO LADO Y CONFIGURABLE REDIS
			Map<Integer,String> lobbies = new HashMap<>();
			for(String id : ConfigMain.get().getConfigurationSection("lobbies-menu.lobbies").getValues(false).keySet()) {
				int number = Integer.parseInt(id);
				String serverName = ConfigMain.get().getString("lobbies-menu.lobbies." + id + ".bungee-name");
				lobbies.put(number, serverName);
			}
			instance = new LobbiesMenu(lobbies);
		}
		return instance;
	}

}
