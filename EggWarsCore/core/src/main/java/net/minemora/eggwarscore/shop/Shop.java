package net.minemora.eggwarscore.shop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

import net.minemora.eggwarscore.config.ConfigShop;
import net.minemora.eggwarscore.menu.ShopMenu;

public class Shop {
	
	private String name;
	private String[] description;
	private int slot;
	private Map<Integer,ShopSection> sections = new HashMap<>();
	private Map<ShopSection,Set<Offer>> timedOffers = new HashMap<>();
	private ShopMenu menu;
	
	public Shop(String name, String[] description, int slot, Map<Integer,ShopSection> sections) {
		this.setName(name);
		this.setDescription(description);
		this.setSlot(slot);
		this.setSections(sections);
		this.menu = new ShopMenu(this);
	}
	
	public static Shop deserealize(String type) {
		String name = ConfigShop.get().getString("shops." + type + ".name");
		List<String> descList = ConfigShop.get().getStringList("shops." + type + ".desc");
		String[] description = descList.toArray(new String[descList.size()]);
		int slot = ConfigShop.get().getInt("shops." + type + ".slot");
		Map<Integer,ShopSection> sections = new HashMap<>();
		for (String section : ConfigShop.get().getConfigurationSection("shops." + type + ".sections").getValues(false).keySet()) {
			String sectionName = ConfigShop.get().getString("shops." + type + ".sections." + section + ".name");
			List<String> sectionDescList = ConfigShop.get().getStringList("shops." + type + ".sections." + section + ".desc");
			String[] sectionDesc = sectionDescList.toArray(new String[sectionDescList.size()]);
			int sectionSlot = ConfigShop.get().getInt("shops." + type + ".sections." + section + ".slot");
			Material material = Material.valueOf(ConfigShop.get().getString("shops." + type + ".sections." + section + ".item"));
			Map<Integer,Offer> offers = new HashMap<>();
			for (String offer : ConfigShop.get().getConfigurationSection("shops." + type + ".sections." + section + ".offers").getValues(false).keySet()) {
				String path = "shops." + type + ".sections." + section + ".offers";
				Material offerMaterial = Material.valueOf(ConfigShop.get().getString(path + "." + offer + ".price.material"));
				int price = ConfigShop.get().getInt(path + "." + offer + ".price.amount", 1);
				Offer offerObj = new Offer(path, offerMaterial, price, Integer.parseInt(offer));
				if (ConfigShop.get().getConfigurationSection(path + "." + offer).contains("update")) {
					offerObj.setTimed(true);
					offerObj.setTime(ConfigShop.get().getInt(path + "." + offer + ".update.time", 300));
					offerObj.setName(ConfigShop.get().getString(path + "." + offer + ".update.name", material.name().toLowerCase()));
				}
				offers.put(Integer.parseInt(offer), offerObj);
			}
			sections.put(sectionSlot, new ShopSection(sectionName, sectionDesc, sectionSlot, material, offers));
		}
		Shop shop = new Shop(name, description, slot, sections);
		for(ShopSection section : shop.getSections().values()) {
			for(Offer offer : section.getOffers().values()) {
				if(offer.isTimed()) {
					if(!shop.getTimedOffers().containsKey(section)) {
						shop.getTimedOffers().put(section, new HashSet<>());
					}
					shop.getTimedOffers().get(section).add(offer);
				}
			}
		}
		return shop;
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

	public Map<Integer,ShopSection> getSections() {
		return sections;
	}

	public void setSections(Map<Integer,ShopSection> sections) {
		this.sections = sections;
	}

	public ShopMenu getShopMenu() {
		return menu;
	}

	public void setShopMenu(ShopMenu menu) {
		this.menu = menu;
	}

	public Map<ShopSection,Set<Offer>> getTimedOffers() {
		return timedOffers;
	}

}
