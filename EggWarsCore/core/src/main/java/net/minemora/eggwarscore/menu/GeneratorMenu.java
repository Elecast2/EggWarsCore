package net.minemora.eggwarscore.menu;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.game.GameGenerator;
import net.minemora.eggwarscore.generator.GeneratorManager;
import net.minemora.eggwarscore.utils.ChatUtils;

public class GeneratorMenu extends Menu {
	
	private GameGenerator gameGenerator;
	private final ItemStack infoItem;
	private final ItemStack updateItem;
	
	public GeneratorMenu(GameGenerator gameGenerator) {
		super(3);
		this.gameGenerator = gameGenerator;
		infoItem = new ItemStack(gameGenerator.getArenaGenerator().getGenerator().getMaterial());
		updateItem = new ItemStack(Material.EXP_BOTTLE);
		update();
	}
	
	public void update() {
		String title = ChatUtils.format(ConfigLang.get().getString("generator.menu.name-" 
				+ ((getGameGenerator().getLevel()>0) ? "active" : "broken"))
				.replaceAll("%name%", getGameGenerator().getArenaGenerator().getGenerator().getName())
   				.replaceAll("%level%", String.valueOf(getGameGenerator().getLevel())));
		setInventory(Bukkit.createInventory(null, 9*getBars(), title));
		ItemMeta meta = infoItem.getItemMeta();
		meta.setDisplayName(ChatUtils.format(ConfigLang.get().getString("generator.menu.item-info-"
				+ ((getGameGenerator().getLevel()>0) ? "active" : "broken"))
				.replaceAll("%name%", getGameGenerator().getArenaGenerator().getGenerator().getName())
   				.replaceAll("%level%", String.valueOf(getGameGenerator().getLevel()))));
		meta.setLore(ChatUtils.formatList(ConfigLang.get().getStringList("generator.menu.item-info-lore")).stream()
				.map(s -> s.replaceAll("%seconds%", ChatUtils.formatSeconds(getSeconds()))).collect(Collectors.toList()));
		infoItem.setItemMeta(meta);
		meta = updateItem.getItemMeta();
		if(gameGenerator.getLevel() == gameGenerator.getArenaGenerator().getGenerator().getGenerationTime().size()) {
			meta.setDisplayName(ChatUtils.format(ConfigLang.get().getString("generator.menu.item-upgrade-max")));
			meta.setLore(Arrays.asList(new String[] {""}));
		}
		else {
			meta.setDisplayName(ChatUtils.format(ConfigLang.get().getString("generator.menu.item-upgrade-name")
					.replaceAll("%next-level%", String.valueOf(getGameGenerator().getLevel()+1))
					.replaceAll("%name%", getGameGenerator().getArenaGenerator().getGenerator().getName())));
			meta.setLore(ChatUtils.formatList(ConfigLang.get().getStringList("generator.menu.item-upgrade-lore")).stream()
					.map(s -> s.replaceAll("%seconds%", ChatUtils.formatSeconds(getNextSeconds()))
					.replaceAll("%price%", String.valueOf(gameGenerator.getArenaGenerator().getGenerator()
							.getLevelPrice().get(gameGenerator.getLevel()+1)))
					.replaceAll("%material-name%", ChatUtils.format(((gameGenerator.getArenaGenerator().getGenerator()
							.getLevelPrice().get(gameGenerator.getLevel()+1)==1) ? 
							GeneratorManager.getNameSingular().get(gameGenerator.getArenaGenerator()
							.getGenerator().getLevelMaterial().get(gameGenerator.getLevel()+1)) : 
							GeneratorManager.getNamePlural().get(gameGenerator.getArenaGenerator()
							.getGenerator().getLevelMaterial().get(gameGenerator.getLevel()+1))))))
					.collect(Collectors.toList()));
		}
		updateItem.setItemMeta(meta);
		getInventory().setItem(11, infoItem);
		getInventory().setItem(15, updateItem);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getClickedInventory() == null) {
			return;
		}
		if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {
			if (event.getClick().isShiftClick() || event.getClick() == 	ClickType.DOUBLE_CLICK) {
	        	InventoryView iv = event.getWhoClicked().getOpenInventory();
	        	if(iv.getTopInventory() != null) {
	        		if(iv.getTopInventory().equals(getInventory())) {
		        		event.setCancelled(true);
		        	}
	        	}
	        }
	    }
		if (event.getClickedInventory().equals(getInventory())) {
			event.setCancelled(true);
			if (event.getCurrentItem().equals(updateItem)) {
				gameGenerator.tryToUpgrade(((Player)event.getWhoClicked()).getName());
				event.getWhoClicked().closeInventory();
			}
		}
	}
	
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.getInventory().equals(getInventory())) {
			event.setCancelled(true);
		}
	}
	
	private double getSeconds() {
		if(gameGenerator.getLevel() == 0) {
			return 0;
		}
		return (double)gameGenerator.getArenaGenerator().getGenerator().getGenerationTime().get(gameGenerator.getLevel())/20;
	}
	
	private double getNextSeconds() {
		return (double)gameGenerator.getArenaGenerator().getGenerator().getGenerationTime().get(gameGenerator.getLevel()+1)/20;
	}

	public GameGenerator getGameGenerator() {
		return gameGenerator;
	}
}