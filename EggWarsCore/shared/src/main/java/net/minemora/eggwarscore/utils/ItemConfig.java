package net.minemora.eggwarscore.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.config.Config;

public class ItemConfig {
	
	private ItemStack item;
	
	public ItemConfig(Config config, String path) {
		this.item = setupItem(config, path);
	}
	
	private ItemStack setupItem(Config config, String path) {
		Material material = Material.valueOf(config.getConfig().getString(path + ".material"));
		int amount = config.getConfig().getInt(path + ".amount", 1);
		short damage = (short) config.getConfig().getInt(path + ".data", 0);
		ItemStack item = new ItemStack(material, amount, damage);
		ItemMeta meta = item.getItemMeta();
		if(config.getConfig().contains(path + ".display-name")) {
			meta.setDisplayName(ChatUtils.format(config.getConfig().getString(path + ".display-name")));
		}
		if(config.getConfig().contains(path + ".lore")) {
			meta.setLore(ChatUtils.formatList(config.getConfig().getStringList(path + ".lore")));
		}
		if(config.getConfig().contains(path + ".enchantments")) {
			for(String enchant : config.getConfig().getStringList(path + ".enchantments")) {
				int level = Integer.valueOf(enchant.split(":")[1]);
				enchant = enchant.split(":")[0];
				meta.addEnchant(Enchantment.getByName(enchant), level, true);
			}
		}
		item.setItemMeta(meta);
		return item;
	}
	
	public ItemStack getItem() {
		return item;
	}
}