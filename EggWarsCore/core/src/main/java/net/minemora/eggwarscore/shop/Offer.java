package net.minemora.eggwarscore.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.config.ConfigShop;
import net.minemora.eggwarscore.generator.GeneratorManager;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.ItemConfig;
import net.minemora.eggwarscore.utils.Utils;

public class Offer extends ItemConfig {
	
	private ItemStack reward;
	private Material material;
	private int price;
	private int order;
	private ItemStack menuItem;
	private boolean timed;
	private int time;
	private String name;
	
	public Offer(String path, Material material, int price, int order) {
		super(ConfigShop.getInstance(), path + "." + order);
		this.setReward(getItem());
		this.setMaterial(material);
		this.setPrice(price);
		this.setOrder(order);
		this.menuItem = createMenuItem();
	}
	
	private ItemStack createMenuItem() {
		ItemStack item = reward.clone();
		ItemMeta meta = item.getItemMeta();
		List<String> lore;
		if(meta.getLore()==null) {
			lore = new ArrayList<>();
		}
		else {
			lore = meta.getLore();
		}
		lore.add("");
		lore.add(getFormattedPrice());
		lore.add("");
		meta.setLore(Arrays.asList(ChatUtils.format(lore)));
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack getReward(Color color) {
		ItemStack re = reward.clone();
		if(re.getType() == Material.STAINED_CLAY || re.getType() == Material.WOOL || re.getType() == Material.STAINED_GLASS) {
			re.setDurability(Utils.colorToIdColor(color));
		}
		if(re.getItemMeta() instanceof LeatherArmorMeta) {
			LeatherArmorMeta meta = (LeatherArmorMeta) re.getItemMeta();
			meta.setColor(color);
			re.setItemMeta(meta);
		}
		return re;
	}
	
	public String getFormattedPrice() {
		return ConfigLang.get().getString("shop.price").replaceAll("%price%", String.valueOf(price))
				.replaceAll("%material%", ((price==1) ? GeneratorManager.getNameSingular().get(material) 
				: GeneratorManager.getNamePlural().get(material)));
	}

	public void setReward(ItemStack reward) {
		this.reward = reward;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public ItemStack getMenuItem() {
		return menuItem;
	}

	public boolean isTimed() {
		return timed;
	}

	public void setTimed(boolean timed) {
		this.timed = timed;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}