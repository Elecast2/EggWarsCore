package net.minemora.eggwarscore.shop;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.EntityType;

import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.config.ConfigShop;


public final class ShopManager {
	
	private static Map<String, Shop> shops = new HashMap<>();
	
	private static EntityType entityType;
	
	private ShopManager() {}
	
	public static void loadShops() {
		if (getShopList().isEmpty()) {
			return;
		}
		shops.clear();
		for (String type : getShopList()) {
			shops.put(type, Shop.deserealize(type));
		}
		entityType = EntityType.valueOf(ConfigMain.get().getString("shop.entity-type"));
	}
	
	public static Set<String> getShopList() {
		Set<String> shopList = new HashSet<>();
		if (ConfigShop.get().get("shops") != null) {
			for (String arena : ConfigShop.get().getConfigurationSection("shops").getValues(false).keySet()) {
				shopList.add(arena);
			}
		}
		return shopList;
	}

	public static Map<String, Shop> getShops() {
		return shops;
	}

	public static EntityType getEntityType() {
		return entityType;
	}
}