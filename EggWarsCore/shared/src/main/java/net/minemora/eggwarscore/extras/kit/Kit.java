package net.minemora.eggwarscore.extras.kit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.minemora.eggwarscore.config.extras.ConfigKits;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.database.Stat;
import net.minemora.eggwarscore.extras.Extra;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.ItemConfig;
import net.minemora.eggwarscore.utils.Utils;

public class Kit extends Extra {
	
	private Map<Integer,ItemStack> items = new HashMap<>();
	private ItemStack[] armor = new ItemStack[4];
	private final int resendTimes;
	private boolean binding = true; //TODO configurable

	public Kit(int id) {
		super(KitManager.getInstance(), ConfigKits.getInstance(), "kits." + id, id);
		this.resendTimes = ConfigKits.get().getInt("kits." + id + ".give-on-respawn-times");
		String path = "kits." + id + ".items";
		for (String itemId : ConfigKits.get().getConfigurationSection(path).getValues(false).keySet()) {
			ItemStack item = new ItemConfig(ConfigKits.getInstance(), path + "." + itemId).getItem();
			String slot = ConfigKits.get().getString(path + "." + itemId + ".slot");
			if(slot.equalsIgnoreCase("helmet")) {
				armor[3] = item;
			}
			else if(slot.equalsIgnoreCase("chestplate")) {
				armor[2] = item;
			}
			else if(slot.equalsIgnoreCase("leggings")) {
				armor[1] = item;
			}
			else if(slot.equalsIgnoreCase("boots")) {
				armor[0] = item;
			}
			else {
				int nslot = Integer.parseInt(slot);
				items.put(nslot, item);
			}
		}
	}
	
	public void equip(Player player, Color color) {
		for(int i : items.keySet()) {
			ItemStack re = items.get(i).clone();
			if(binding) {
				re = bind(re);
			}
			if(re.getType() == Material.STAINED_CLAY || re.getType() == Material.WOOL || re.getType() == Material.STAINED_GLASS) {
				re.setDurability(Utils.colorToIdColor(color));
			}
			player.getInventory().setItem(i, re);
		}
		ItemStack[] reArmor = new ItemStack[4];
		for(int i = 0; i < armor.length; i++) {
			if(armor[i]!=null) {
				ItemStack re = armor[i].clone();
				if(binding) {
					re = bind(re);
				}
				if(re.getItemMeta() instanceof LeatherArmorMeta) {
					LeatherArmorMeta meta = (LeatherArmorMeta) re.getItemMeta();
					meta.setColor(color);
					re.setItemMeta(meta);
				}
				reArmor[i] = re;
			}
			player.getInventory().setArmorContents(reArmor);
		}
	}
	
	private ItemStack bind(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(Arrays.asList(new String[] {ChatUtils.format("&8binded")}));
	   	item.setItemMeta(meta);
	   	return item;
	}

	@Override
	public void giveToPlayer(PlayerStats playerStats) {
		playerStats.setKit(this);
		playerStats.setRandomKit(false);
		Database.set(Stat.KIT, playerStats.getPlayer(), getId());
	}
	
	public static boolean isBinded(ItemStack item) {
		if(item.getItemMeta() == null) {
			return false;
		}
		if(item.getItemMeta().getLore() == null) {
			return false;
		}
		if(item.getItemMeta().getLore().size() == 0) {
			return false;
		}
		if(item.getItemMeta().getLore().get(0).equals(ChatUtils.format("&8binded"))) {
			return true;
		}
		return false;
	}

	public Map<Integer,ItemStack> getItems() {
		return items;
	}

	public ItemStack[] getArmor() {
		return armor;
	}

	public int getResendTimes() {
		return resendTimes;
	}

	public boolean isBinding() {
		return binding;
	}

	public void setBinding(boolean binding) {
		this.binding = binding;
	}
}
