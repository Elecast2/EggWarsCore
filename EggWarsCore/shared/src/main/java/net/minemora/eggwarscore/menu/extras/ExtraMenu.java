package net.minemora.eggwarscore.menu.extras;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.extras.Extra;
import net.minemora.eggwarscore.menu.Menu;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.ItemConfig;

public abstract class ExtraMenu extends Menu {
	
	private PlayerStats playerStats;
	
	private Map<Integer,Inventory> allExtrasMenus = new HashMap<>();
	private Map<Integer,Inventory> availableExtrasMenus = new HashMap<>();
	private Map<Integer,Inventory> unavailableExtrasMenus = new HashMap<>();
	
	private Map<Integer,Extra> allExtrasSlots = new HashMap<>();
	private Map<Integer,Extra> availableExtrasSlots = new HashMap<>();
	private Map<Integer,Extra> unavailableExtrasSlots = new HashMap<>();
	
	private ItemStack randomExtraItem;
	private ItemStack removeExtraItem;
	private ItemStack showAllItem;
	private ItemStack showAvailableItem;
	private ItemStack showUnavailableItem;
	private ItemStack previousPageItem;
	private ItemStack nextPageItem;

	public ExtraMenu(PlayerStats playerStats) {
		super(2);
		this.playerStats = playerStats;
		
		randomExtraItem = new ItemConfig(ConfigMain.getInstance(), "extras.option-items.random").getItem();
		removeExtraItem = new ItemConfig(ConfigMain.getInstance(), "extras.option-items.remove").getItem();
		showAllItem = new ItemConfig(ConfigMain.getInstance(), "extras.option-items.show-all").getItem();
		showUnavailableItem = new ItemConfig(ConfigMain.getInstance(), "extras.option-items.show-unavailable").getItem();
		showAvailableItem = new ItemConfig(ConfigMain.getInstance(), "extras.option-items.show-available").getItem();
		previousPageItem = new ItemConfig(ConfigMain.getInstance(), "extras.option-items.previous-page").getItem();
		if(previousPageItem.getType() == Material.BANNER) {
			BannerMeta ppMeta = (BannerMeta) previousPageItem.getItemMeta();
			ppMeta.setBaseColor(DyeColor.BLACK);
			ppMeta.addPattern(new Pattern(DyeColor.RED, PatternType.RHOMBUS_MIDDLE));
			ppMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_RIGHT));
			ppMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.SQUARE_TOP_RIGHT));
			ppMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.SQUARE_BOTTOM_RIGHT));
			previousPageItem.setItemMeta(ppMeta);
		}
		nextPageItem = new ItemConfig(ConfigMain.getInstance(), "extras.option-items.next-page").getItem();
		if(nextPageItem.getType() == Material.BANNER) {
			BannerMeta npMeta = (BannerMeta) nextPageItem.getItemMeta();
			npMeta.setBaseColor(DyeColor.BLACK);
			npMeta.addPattern(new Pattern(DyeColor.GREEN, PatternType.RHOMBUS_MIDDLE));
			npMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.STRIPE_LEFT));
			npMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.SQUARE_TOP_LEFT));
			npMeta.addPattern(new Pattern(DyeColor.BLACK, PatternType.SQUARE_BOTTOM_LEFT));
			nextPageItem.setItemMeta(npMeta);
		}
		
		for(int i = 0; i < getPages(getAllExtras()); i++) {
			getExtrasMenus(ShowType.ALL).put(i, getPage(i, getAllExtras(), ShowType.ALL));
		}
		
		for(int i = 0; i < getPages(getAvailableExtras()); i++) {
			getExtrasMenus(ShowType.AVAILABLE).put(i, getPage(i, getAvailableExtras(), ShowType.AVAILABLE));
		}
		
		for(int i = 0; i < getPages(getUnavailableExtras()); i++) {
			getExtrasMenus(ShowType.UNAVAILABLE).put(i, getPage(i, getUnavailableExtras(), ShowType.UNAVAILABLE));
		}
		
		setInventory(allExtrasMenus.get(0));
	}
	
	private Inventory getPage(int page, Collection<? extends Extra> extras, ShowType showType) {
		int maxPages = getPages(extras);
		if(page > maxPages) {
			return null;
		}
		String title = getMenuTitle();
		int addBars;
		PageType pageType;
		if(maxPages > 1) {
			if(page == maxPages) {
				addBars = getAddBars(getSubList(page, extras).size());
				pageType = PageType.LAST;
			}
			else {
				addBars = 4;
				if(page == 1) {
					pageType = PageType.FIRST;
				}
				else {
					pageType = PageType.MIDDLE;
				}
			}
			title = title + ConfigLang.get().getString("extras.page").replaceAll("%page%", String.valueOf(page));
		}
		else {
			addBars = getAddBars(extras.size());
			pageType = PageType.NO_PAGES;
		}
		if(title.length() > 32) {
			title = title.substring(0, 32);
		}
		
		Inventory inv = Bukkit.createInventory(null, 9*(getBars()+addBars), ChatUtils.format(title));
		borderInventory(inv, pageType, showType);
		fillExtras(inv, getSubList(page, extras), showType);
		return inv;
	}
	
	private void fillExtras(Inventory inv, List<? extends Extra> list, ShowType showType) {
		int slot = 0;
		for(Extra extra : list) {
			int bukkitSlot = pageSlotToBukkit(slot);
			ItemStack item = extra.getMenuItem(playerStats);
			if(playerStats.hasExtraEquiped(extra)) {
				ItemMeta meta = item.getItemMeta();
				meta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				item.setItemMeta(meta);
			}
			inv.setItem(bukkitSlot, extra.getMenuItem(playerStats));
			getExtrasSlots(showType).put(slot, extra);
			slot++;
		}
	}
	
	private void borderInventory(Inventory inv, PageType pageType, ShowType showType) {
		ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15);
		ItemMeta meta = pane.getItemMeta();
		meta.setDisplayName(" ");
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		pane.setItemMeta(meta);
		for(int i = 0; i < 9; i++) {
			inv.setItem(i, pane);
		}
		for(int i = inv.getSize()-1; i > (inv.getSize()-10); i--) {
			if(i == inv.getSize()-8) {
				if(pageType == PageType.MIDDLE || pageType == PageType.LAST) {
					inv.setItem(i, previousPageItem);
				}
				else {
					inv.setItem(i, pane);
				}
			} 
			else if (i == inv.getSize()-6) {
				inv.setItem(i, randomExtraItem);
			}
			else if (i == inv.getSize()-5) {
				inv.setItem(i, removeExtraItem);
			} 
			else if (i == inv.getSize()-4) {
				ItemStack item;
				if(showType == ShowType.UNAVAILABLE) {
					item = showAllItem;
				}
				else if(showType == ShowType.AVAILABLE) {
					item = showUnavailableItem;
				}
				else {
					item = showAvailableItem;
				}
				inv.setItem(i, item);
			} 
			else if (i == inv.getSize()-2) {
				if(pageType == PageType.MIDDLE || pageType == PageType.FIRST) {
					inv.setItem(i, nextPageItem);
				}
				else {
					inv.setItem(i, pane);
				}
			}
			else {
				inv.setItem(i, pane);
			}
		}
		for(int i = 1; i <= (numberToBars(inv.getSize())-2); i++) {
			switch(i) {
			case 1:
				inv.setItem(9, pane);
				inv.setItem(17, pane);
				break;
			case 2:
				inv.setItem(18, pane);
				inv.setItem(26, pane);
				break;
			case 3:
				inv.setItem(27, pane);
				inv.setItem(35, pane);
				break;
			case 4:
				inv.setItem(36, pane);
				inv.setItem(44, pane);
				break;
			}
		}
	}
	
	private List<? extends Extra> getSubList(int page, Collection<? extends Extra> extras) {
		int maxPages = getPages(extras);
		if(page > maxPages) {
			return null;
		}
		List<? extends Extra> list = new ArrayList<>(extras);
		if(maxPages > 1) {
			if(page == maxPages) {
				return list.subList(page*28, extras.size());
			}
			else {
				return list.subList(page*28, (page+1)*28);
			}
		}
		else {
			return list;
		}
	}
	
	private int getPages(Collection<? extends Extra> extras) {
		return ((extras.size()-1)/28)+1;
	}
	
	private int getAddBars(int size) {
		return ((size-1)/7)+1;
	}

	private int pageSlotToBukkit(int pageSlot) {
		if(pageSlot < 7) {
			return pageSlot+10;
		}
		else if(pageSlot > 6 && pageSlot < 14) {
			return pageSlot+12;
		}
		else if(pageSlot > 13 && pageSlot < 21) {
			return pageSlot+14;
		}
		else {
			return pageSlot+16;
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
	        		if(isAnExtraInventory(iv.getTopInventory())) {
		        		event.setCancelled(true);
		        	}
	        	}
	        }
	    }
		if(isAnExtraInventory(event.getClickedInventory())) {
			if(isAnExtraMenuSlot(event.getSlot(), event.getClickedInventory().getSize())) {
				if(event.getClickedInventory().getItem(event.getSlot()) == null) {
					return;
				}
				ShowType showType = getShowTypeByInventory(event.getClickedInventory());
				int slot = getSlotFromBukkit(event.getSlot(), getPageByInventory(event.getClickedInventory(), showType));
				Extra extra = getExtrasSlots(showType).get(slot);
				extra.inventoryClickAction(playerStats);
			}
			else {
				if (event.getCurrentItem().equals(removeExtraItem)) {
					Extra extra = getCurrentExtra();
					if(extra != null) {
						disenchantItem(extra);
						removeExtra();
						playerStats.getPlayer().playSound(playerStats.getPlayer().getLocation(), 
								Sound.valueOf(ConfigMain.get().getString("extras.select-sound")), 10, 1);
					}
					else {
						if(!isExtraRandom()) {
							playerStats.getPlayer().playSound(playerStats.getPlayer().getLocation(), 
									Sound.valueOf(ConfigMain.get().getString("extras.unavailable-sound")), 10, 1);
						}
						else {
							removeExtra();
							disenchantRandom();
							playerStats.getPlayer().playSound(playerStats.getPlayer().getLocation(), 
									Sound.valueOf(ConfigMain.get().getString("extras.select-sound")), 10, 1);
						}
					}
				}
				else if (event.getCurrentItem().equals(randomExtraItem)) {
					if(isExtraRandom()) {
						playerStats.getPlayer().playSound(playerStats.getPlayer().getLocation(), 
								Sound.valueOf(ConfigMain.get().getString("extras.unavailable-sound")), 10, 1);
					}
					else {
						Extra extra = getCurrentExtra();
						if(extra != null) {
							disenchantItem(extra);
						}
						ItemMeta meta = randomExtraItem.getItemMeta();
						meta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
						meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						randomExtraItem.setItemMeta(meta);
						updateRandomItem();
						setExtraRandom();
						playerStats.getPlayer().playSound(playerStats.getPlayer().getLocation(), 
								Sound.valueOf(ConfigMain.get().getString("extras.select-sound")), 10, 1);
					}
				}
				else if (event.getCurrentItem().equals(showAvailableItem)) {
					//event.getWhoClicked().closeInventory();
					event.getWhoClicked().openInventory(getExtrasMenus(ShowType.AVAILABLE).get(0));
					playerStats.getPlayer().playSound(playerStats.getPlayer().getLocation(), 
							Sound.valueOf(ConfigMain.get().getString("extras.select-sound")), 10, 1);
				}
				else if (event.getCurrentItem().equals(showUnavailableItem)) {
					//event.getWhoClicked().closeInventory();
					event.getWhoClicked().openInventory(getExtrasMenus(ShowType.UNAVAILABLE).get(0));
					playerStats.getPlayer().playSound(playerStats.getPlayer().getLocation(), 
							Sound.valueOf(ConfigMain.get().getString("extras.select-sound")), 10, 1);
				}
				else if (event.getCurrentItem().equals(showAllItem)) {
					//event.getWhoClicked().closeInventory();
					event.getWhoClicked().openInventory(getExtrasMenus(ShowType.ALL).get(0));
					playerStats.getPlayer().playSound(playerStats.getPlayer().getLocation(), 
							Sound.valueOf(ConfigMain.get().getString("extras.select-sound")), 10, 1);
				}
				else if (event.getCurrentItem().equals(nextPageItem)) {
					ShowType showType = getShowTypeByInventory(event.getClickedInventory());
					int currentPage = getPageByInventory(event.getClickedInventory(), showType);
					int maxPages = getExtrasMenus(showType).size();
					if(currentPage == (maxPages-1)) {
						return;
					}
					//event.getWhoClicked().closeInventory();
					event.getWhoClicked().openInventory(getExtrasMenus(showType).get(currentPage+1));
					playerStats.getPlayer().playSound(playerStats.getPlayer().getLocation(), 
							Sound.valueOf(ConfigMain.get().getString("extras.select-sound")), 10, 1);
				}
				else if (event.getCurrentItem().equals(previousPageItem)) {
					ShowType showType = getShowTypeByInventory(event.getClickedInventory());
					int currentPage = getPageByInventory(event.getClickedInventory(), showType);
					if(currentPage == 0) {
						return;
					}
					//event.getWhoClicked().closeInventory();
					event.getWhoClicked().openInventory(getExtrasMenus(showType).get(currentPage-1));
					playerStats.getPlayer().playSound(playerStats.getPlayer().getLocation(), 
							Sound.valueOf(ConfigMain.get().getString("extras.select-sound")), 10, 1);
				}
			}
		}
	}
	
	public void disenchantRandom() {
		ItemMeta meta = randomExtraItem.getItemMeta();
		for(Enchantment enchant : meta.getEnchants().keySet()) {
			meta.removeEnchant(enchant);
		}
		randomExtraItem.setItemMeta(meta);
		updateRandomItem();
	}
	
	private void disenchantItem(Extra extra) {
		ShowType[] showTypes = new ShowType[] {ShowType.ALL, ShowType.AVAILABLE};
		for(ShowType showType : showTypes) {
			for(int slot : getExtrasSlots(showType).keySet()) {
				if(getExtrasSlots(showType).get(slot).equals(extra)) {
					ItemStack item = getExtrasMenus(showType).get(getPageBySlot(slot)).getItem(getBukkitSlot(slot));
					ItemMeta meta = item.getItemMeta();
					for(Enchantment enchant : meta.getEnchants().keySet()) {
						meta.removeEnchant(enchant);
					}
					item.setItemMeta(meta);
					break;
				}
			}
		}
	}
	
	public void enchantItem(Extra extra) {
		Extra prevExtra = getCurrentExtra();
		if(prevExtra != null) {
			disenchantItem(prevExtra);
		}
		ShowType[] showTypes = new ShowType[] {ShowType.ALL, ShowType.AVAILABLE};
		for(ShowType showType : showTypes) {
			for(int slot : getExtrasSlots(showType).keySet()) {
				if(getExtrasSlots(showType).get(slot).equals(extra)) {
					ItemStack item = getExtrasMenus(showType).get(getPageBySlot(slot)).getItem(getBukkitSlot(slot));
					ItemMeta meta = item.getItemMeta();
					meta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					item.setItemMeta(meta);
					if(isExtraRandom()) {
						disenchantRandom();
					}
					break;
				}
			}
		}
	}
	
	private int getBukkitSlot(int slot) {
		return pageSlotToBukkit(getPageSlot(slot, getPageBySlot(slot)));
	}
	
	private int getPageBySlot(int slot) {
		return ((slot-1)/28);
	}
	
	private int getPageSlot(int slot, int page) {
		return slot - (28*page);
	}
	
	private int getSlotFromBukkit(int bukkitSlot, int page) {
		if(bukkitSlot > 9 && bukkitSlot < 17) {
			return (bukkitSlot - 10) + (28*page);
		}
		if(bukkitSlot > 18 && bukkitSlot < 26) {
			return (bukkitSlot - 12) + (28*page);
		}
		if(bukkitSlot > 27 && bukkitSlot < 35) {
			return (bukkitSlot - 14) + (28*page);
		}
		if(bukkitSlot > 36 && bukkitSlot < 44) {
			return (bukkitSlot - 16) + (28*page);
		}
		return 0;
	}
	
	private boolean isAnExtraMenuSlot(int bukkitSlot, int invSize) {
		if(bukkitSlot < 9) {
			return false;
		}
		if(bukkitSlot > (invSize-10)) {
			return false;
		}
		for(int i = 1; i <= (numberToBars(invSize)-2); i++) {
			switch(i) {
			case 1:
				if(bukkitSlot == 9) {
					return false;
				}
				if(bukkitSlot == 17) {
					return false;
				}
			case 2:
				if(bukkitSlot == 18) {
					return false;
				}
				if(bukkitSlot == 26) {
					return false;
				}
			case 3:
				if(bukkitSlot == 27) {
					return false;
				}
				if(bukkitSlot == 35) {
					return false;
				}
			case 4:
				if(bukkitSlot == 36) {
					return false;
				}
				if(bukkitSlot == 44) {
					return false;
				}
			}
		}
		return true;
	}
	
	private int getPageByInventory(Inventory inv, ShowType showType) {
		for(int i : getExtrasMenus(showType).keySet()) {
			if(getExtrasMenus(showType).get(i).equals(inv)) {
				return i;
			}
		}
		return 0;
	}
	
	private ShowType getShowTypeByInventory(Inventory inv) {
		for(ShowType showType : ShowType.values()) {
			if(getExtrasMenus(showType).values().contains(inv)) {
				return showType;
			}
		}
		return null;
	}
	
	private boolean isAnExtraInventory(Inventory inv) {
		for(ShowType showType : ShowType.values()) {
			if(getExtrasMenus(showType).values().contains(inv)) {
				return true;
			}
		}
		return false;
	}
	
	private void updateRandomItem() {
		for(ShowType showType : ShowType.values()) {
			for(Inventory inv : getExtrasMenus(showType).values()) {
				inv.setItem(inv.getSize()-6, randomExtraItem);
			}
		}
		playerStats.getPlayer().updateInventory();
	}
	
	public abstract Collection<? extends Extra> getAllExtras();
	
	public abstract Collection<? extends Extra> getAvailableExtras();
	
	public abstract Extra getCurrentExtra();
	
	public abstract void removeExtra();
	
	public abstract void setExtraRandom();
	
	public abstract boolean isExtraRandom();
	
	public abstract String getMenuTitle();
	
	public Collection<? extends Extra> getUnavailableExtras() {
		Collection<? extends Extra> list = new ArrayList<>(getAllExtras());
		list.removeAll(getAvailableExtras());
		return list;
	}

	public PlayerStats getPlayerStats() {
		return playerStats;
	}
	
	public Map<Integer,Inventory> getExtrasMenus(ShowType showType) {
		if(showType == ShowType.UNAVAILABLE) {
			return unavailableExtrasMenus;
		}
		else if(showType == ShowType.AVAILABLE) {
			return availableExtrasMenus;
		}
		else {
			return allExtrasMenus;
		}
	}
	
	public Map<Integer,Extra> getExtrasSlots(ShowType showType) {
		if(showType == ShowType.UNAVAILABLE) {
			return unavailableExtrasSlots;
		}
		else if(showType == ShowType.AVAILABLE) {
			return availableExtrasSlots;
		}
		else {
			return allExtrasSlots;
		}
	}
}