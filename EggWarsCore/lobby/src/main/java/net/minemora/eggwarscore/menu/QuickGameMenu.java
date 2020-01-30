package net.minemora.eggwarscore.menu;

import java.util.List;

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

import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.database.Stat;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.player.LobbyPlayer;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.ItemConfig;

public class QuickGameMenu extends Menu {
	
	private LobbyPlayer lobbyPlayer;

	public QuickGameMenu(LobbyPlayer lobbyPlayer) {
		super(1);
		this.lobbyPlayer = lobbyPlayer;
		update();
	}
	
	private void update() {
		Inventory inv = Bukkit.createInventory(null, 9*getBars(), ChatUtils.format("&6&lConfigurar Modo")); //TODO LANG
		for(String mode : GameManager.getModesId().keySet()) {
			ItemStack item = new ItemConfig(ConfigMain.getInstance(), "modes." + mode  + ".item").getItem();
			ItemMeta meta = item.getItemMeta();
			List<String> lore = meta.getLore();
			int modeId = GameManager.getModesId().get(mode);
			if(lobbyPlayer.getMode() == modeId) {
				lore.add(ChatUtils.format("&a&lSELECCIONADO")); //TODO LANG
			}
			else {
				lore.add(ChatUtils.format("&7Has &aclick &7para seleccionar")); //TODO LANG
				lore.add(ChatUtils.format("&7este &5modo &6y que seas enviado"));
				lore.add(ChatUtils.format("&7de manera &2automática &7cada"));
				lore.add(ChatUtils.format("&7vez que uses el &cNPC&7."));
			}
			meta.setLore(lore);
			item.setItemMeta(meta);
			inv.setItem(modeId, item);
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
			int mode = event.getSlot();
			if(lobbyPlayer.getMode() == mode) {
				player.playSound(player.getLocation(), Sound.NOTE_BASS, 0.5f, 1); //TODO CONFIG
				player.sendMessage(ChatUtils.format("&c¡Ya tienes seleccionado este modo!")); //TODO LANG
			}
			else {
				lobbyPlayer.setMode(mode);
				lobbyPlayer.updateQuickGameLine();
				Database.set(Stat.MODE, player, mode);
				player.playSound(player.getLocation(), Sound.CLICK, 0.5f, 1); //TODO CONFIG
				player.sendMessage(ChatUtils.format("&a¡Modo seleccionado correctamente!")); //TODO LANG
				update();
			}
			player.closeInventory();
		}
	}

	public LobbyPlayer getLobbyPlayer() {
		return lobbyPlayer;
	}

}
