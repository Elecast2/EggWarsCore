package net.minemora.eggwarscore.shop;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.utils.ChatUtils;

public class ShopSection {
	
	private String name;
	private String[] description;
	private int slot;
	private Material material;
	private Map<Integer,Offer> offers = new HashMap<>();
	private ItemStack menuItem;
	
	public ShopSection(String name, String[] description, int slot, Material material, Map<Integer,Offer> offers) {
		this.setName(name);
		this.setDescription(description);
		this.setSlot(slot);
		this.setMaterial(material);
		this.setOffers(offers);
		this.menuItem = createMenuItem();
	}
	
	private ItemStack createMenuItem() {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatUtils.format(name));
		meta.setLore(Arrays.asList(ChatUtils.format(description)));
		item.setItemMeta(meta);
		return item;
	}
	
	public int getOffersAmount(long time) {
		int i = 0;
		for(Offer offer : getOffers().values()) {
			if(offer.isTimed()) {
				if(time >= offer.getTime()) {
					i++;
				}
			}
			else {
				i++;
			}
		}
		return i;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getDescription() {
		return description;
	}

	public void setDescription(String[] description) {
		this.description = description;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Map<Integer,Offer> getOffers() {
		return offers;
	}

	public void setOffers(Map<Integer,Offer> offers) {
		this.offers = offers;
	}

	public ItemStack getMenuItem() {
		return menuItem;
	}
}
