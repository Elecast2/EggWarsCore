package net.minemora.eggwarscore.extras;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.config.Config;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.menu.BuyMenu;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.ItemConfig;
import net.minemora.eggwarscore.utils.Utils;

public abstract class Extra extends ItemConfig {
	
	private UnlockType unlockType;
	
	private String name;
	private List<String> desc;
	private int level;
	private int price;
	private String permission;
	
	private Config config;
	private int id;
	
	private ItemStack[] lockedItems;
	private ItemStack[] unlockedItems;
	private Map<String,ItemStack> permissionLockedItems = new HashMap<>();
	private Map<String,ItemStack> permissionUnlockedItems = new HashMap<>();
	
	private ExtraManager manager;

	public Extra(ExtraManager manager, Config config, String path, int id) {
		super(config, path + ".menu-item");
		this.manager = manager;
		this.config = config;
		this.id = id;
		this.name = config.getConfig().getString(path + ".name");
		this.desc = config.getConfig().getStringList(path + ".desc");
		this.unlockType = UnlockType.valueOf(config.getConfig().getString(path + ".unlock-type", "DEFAULT"));
		this.price = config.getConfig().getInt(path + ".price", 0);
		this.level = config.getConfig().getInt(path + ".level", 0);
		if(config.getConfig().contains(path + ".permission")) {
			this.permission = config.getConfig().getString(path + ".permission");
		}
		loadItems();
	}
	
	private void loadItems() {
		
		ItemMeta meta = getItem().getItemMeta();
		meta.setDisplayName(ChatUtils.format(name));
		getItem().setItemMeta(meta);
		
		lockedItems = new ItemStack[4];
		unlockedItems = new ItemStack[4];
		
		unlockedItems[0] = getUnlockedItem(UnlockType.DEFAULT);
		unlockedItems[1] = getUnlockedItem(UnlockType.PRICE);
		unlockedItems[2] = getUnlockedItem(UnlockType.LEVEL);
		unlockedItems[3] = getUnlockedItem(UnlockType.KEY);
		
		lockedItems[0] = getLockedItem(UnlockType.DEFAULT);
		lockedItems[1] = getLockedItem(UnlockType.PRICE);
		lockedItems[2] = getLockedItem(UnlockType.LEVEL);
		lockedItems[3] = getLockedItem(UnlockType.KEY);
		
		for (String id : config.getConfig().getConfigurationSection("item-format.permission").getValues(false).keySet()) {
			String permission = config.getConfig().getString("item-format.permission." + id + ".permission");
			ItemStack unlockedItem = getUnlockedItem(UnlockType.PERMISSION, id);
			ItemStack lockedItem =getLockedItem(UnlockType.PERMISSION, id);
			permissionLockedItems.put(permission, lockedItem);
			permissionUnlockedItems.put(permission, unlockedItem);
		}

	}
	
	private ItemStack getUnlockedItem(UnlockType type) {
		return getUnlockedItem(type, null);
	}
	
	private ItemStack getLockedItem(UnlockType type) {
		return getLockedItem(type, null);
	}
	
	private ItemStack getUnlockedItem(UnlockType type, String id) {
		if(id==null) {
			id = "";
		}
		else {
			id = "." + id;
		}
		ItemStack item = getItem().clone();	
		setLore(item, "item-format." + type.getPath() + id + ".unlocked-desc");
		return item;
	}
	
	private ItemStack getLockedItem(UnlockType type, String id) {
		if(id==null) {
			id = "";
		}
		else {
			id = "." + id;
		}
		ItemStack item = new ItemConfig(config, "item-format." + type.getPath() + id + ".locked-item").getItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatUtils.format(name));
		item.setItemMeta(meta);
		setLore(item, "item-format." + type.getPath() + id + ".locked-desc");
		return item;
	}
	
	private void setLore(ItemStack item, String lorePath) {
		ItemMeta meta = item.getItemMeta();	
		List<String> lore = config.getConfig().getStringList(lorePath);
		int descIndex = 0;
		boolean containsDesc = false;
		for(String line : lore) {
			if(line.contains("%description%")) {
				containsDesc = true;
				break;
			}
			descIndex++;
		}
		if(containsDesc) {
			lore.remove(descIndex);
			lore.addAll(descIndex, desc);
		}
		lore = Utils.replaceAll(lore, new String[] {"%price%", "%level%"}, new String[] {String.valueOf(price), String.valueOf(level)});
		meta.setLore(ChatUtils.formatList(lore));
		item.setItemMeta(meta);
	}

	public UnlockType getUnlockType() {
		return unlockType;
	}
	
	public void inventoryClickAction(PlayerStats playerStats) {
		Player player = playerStats.getPlayer();
		if(player == null) {
			return;
		}
		if(playerStats.hasExtra(this)) {
			if(playerStats.hasExtraEquiped(this)) {
				player.playSound(player.getLocation(), Sound.valueOf(ConfigMain.get().getString("extras.unavailable-sound")), 10, 1);
			}
			else {
				playerStats.getExtraMenu(this).enchantItem(this);
				giveToPlayer(playerStats);
				player.playSound(player.getLocation(), Sound.valueOf(ConfigMain.get().getString("extras.select-sound")), 10, 1);
			}
		}
		else {
			if(unlockType == UnlockType.PRICE && playerStats.getMoney() >= price) {
				BuyMenu.getInstance().open(player, this);
			}
			else {
				player.playSound(player.getLocation(), Sound.valueOf(ConfigMain.get().getString("extras.unavailable-sound")), 10, 1);
			}
		}
	}
	
	public abstract void giveToPlayer(PlayerStats playerStats);
	
	public ItemStack getMenuItem(PlayerStats playerStats) {
		switch (unlockType) {
		case DEFAULT:
			return unlockedItems[0];
		case PRICE:
			if(playerStats.hasExtra(this)) {
				return unlockedItems[1];
			}
			else {
				return lockedItems[1];
			}
		case LEVEL:
			if(playerStats.hasExtra(this)) {
				return unlockedItems[2];
			}
			else {
				return lockedItems[2];
			}
		case KEY:
			if(playerStats.hasExtra(this)) {
				return unlockedItems[3];
			}
			else {
				return lockedItems[3];
			}
		case PERMISSION:
			if(playerStats.hasExtra(this)) {
				return permissionUnlockedItems.get(permission);
			}
			else {
				return permissionLockedItems.get(permission);
			}
		default:
			return lockedItems[0];
		}
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getDesc() {
		return desc;
	}

	public void setDesc(List<String> desc) {
		this.desc = desc;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public ExtraManager getManager() {
		return manager;
	}

}