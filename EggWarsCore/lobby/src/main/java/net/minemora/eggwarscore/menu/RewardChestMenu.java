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

import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.rchest.RewardChest;
import net.minemora.eggwarscore.utils.ChatUtils;

public class RewardChestMenu extends Menu {
	
	private Inventory noKeysInventory;
	private ItemStack keyItem;
	private ItemStack noKeyItem;

	public RewardChestMenu() {
		super(3);
		
		setInventory(Bukkit.createInventory(null, 9*getBars(), ChatUtils.format("&5&lCofre Especial")));//TODO LANG
		
		ItemMeta meta;
   		List<String> lore = new ArrayList<>();
   		
   		keyItem =  new ItemStack(Material.EYE_OF_ENDER);
		meta = keyItem.getItemMeta();
		meta.setDisplayName(ChatUtils.format("&5&lUsar llave")); //TODO LANG
		lore.add("");
		lore.add(ChatUtils.format("&7Al hacer click usaras una llave"));//TODO LANG
		lore.add(ChatUtils.format("&7para desbloquear un premio"));
		lore.add(ChatUtils.format("&7respectivo a tu nivel."));
		lore.add("");
		meta.setLore(lore);
		keyItem.setItemMeta(meta);
		getInventory().setItem(13, keyItem);

		noKeysInventory = Bukkit.createInventory(null, 9*getBars(), ChatUtils.format("&5&lCofre Especial"));//TODO LANG
		noKeyItem =  new ItemStack(Material.INK_SACK);
		meta = noKeyItem.getItemMeta();
		meta.setDisplayName(ChatUtils.format("&7&l¡No tienes llaves!"));//TODO LANG
		noKeyItem.setItemMeta(meta);
   		noKeysInventory.setItem(13, noKeyItem);
	}
	
	@Override
	public void open(Player player) {
		PlayerStats ps = PlayerStats.get(player.getName());
		if(ps == null) {
			return;
		}
		if(ps.getChestKeys() > 0) {
			player.openInventory(getInventory());
		}
		else {
			player.openInventory(noKeysInventory);
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
		if (event.getClickedInventory().equals(getInventory()) || event.getClickedInventory().equals(noKeysInventory)) {
			event.getWhoClicked().closeInventory();
			if(event.getCurrentItem().equals(keyItem)) {
				PlayerStats ps = PlayerStats.get(event.getWhoClicked().getName());
				if(ps == null) {
					return;
				}
				if(ps.getChestKeys() > 0) {
					RewardChest.getInstance().giveReward((Player)event.getWhoClicked());
				}
			}
			else if(event.getCurrentItem().equals(noKeyItem)) {
				//TODO hacer alguna accion aca como sonido o mensaje
			}
		}
	}
}
