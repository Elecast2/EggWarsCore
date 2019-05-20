package net.minemora.eggwarscore.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.util.Vector;

import net.minemora.eggwarscore.shared.SharedHandler;

public final class Utils {
	
	private Utils() {}
	
	public static <E> E choice(Collection<? extends E> coll) {
	    if (coll.size() == 0) {
	        return null; // or throw IAE, if you prefer
	    }
	    Random rand = ThreadLocalRandom.current();
	    int index = rand.nextInt(coll.size());
	    if (coll instanceof List) { // optimization
	        return ((List<? extends E>) coll).get(index);
	    } else {
	        Iterator<? extends E> iter = coll.iterator();
	        for (int i = 0; i < index; i++) {
	            iter.next();
	        }
	        return iter.next();
	    }
	}
	
	public static LinkedHashMap<String, Integer> sort(Map<String, Integer> map) {
		return map.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
	        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
	}
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	public static boolean hasItems(Player player, Material material, int amount) {
		Inventory inv = player.getInventory();
		int count = 0;
		for(ItemStack item : inv.getContents()) {
			if(item!=null) {
				if(item.getType().equals(material)) {
					count = count + item.getAmount();
				}
			}
		}
		if(count >= amount) {
			return true;
		}
		return false;
	}
	
	public static int countItems(Player player, Material material) {
		Inventory inv = player.getInventory();
		int count = 0;
		for(ItemStack item : inv.getContents()) {
			if(item!=null) {
				if(item.getType().equals(material)) {
					count = count + item.getAmount();
				}
			}
		}
		return count;
	}
	
	public static void removeItems(Player player, Material material, int amount) {
		Inventory inv = player.getInventory();
		for(ItemStack item : inv.getContents()) {
			if(item!=null) {
				if(item.getType().equals(material)) {
					if(item.getAmount() > amount) {
						item.setAmount(item.getAmount()-amount);
						player.updateInventory();
						return;
					}
					else {
						amount = amount - item.getAmount();
						for(Integer i : inv.all(item).keySet()) {
							inv.setItem(i, null);
							player.updateInventory();
							break;
						}
					}
					if(amount==0) {
						return;
					}
				}
			}
		}
	}
	
	public static void makeStatue(Entity bukkitEntity) {
		SharedHandler.getNmsHandler().makeStatue(bukkitEntity);
	}
	
	public static Color chatColorToColor(ChatColor chatColor) {
		switch(chatColor) {
		case BLUE:
			return Color.BLUE;
		case RED:
			return Color.RED;
		case YELLOW:
			return Color.YELLOW;
		case BLACK:
			return Color.BLACK;
		case AQUA:
			return Color.AQUA;
		case DARK_AQUA:
			return Color.BLUE;
		case DARK_BLUE:
			return Color.BLUE;
		case DARK_GRAY:
			return Color.GRAY;
		case DARK_GREEN:
			return Color.OLIVE;
		case DARK_PURPLE:
			return Color.PURPLE;
		case DARK_RED:
			return Color.RED;
		case GOLD:
			return Color.ORANGE;
		case GRAY:
			return Color.SILVER;
		case GREEN:
			return Color.LIME;
		case LIGHT_PURPLE:
			return Color.FUCHSIA;
		default:
			return Color.WHITE;
		}
	}
	
	public static short colorToIdColor(Color color) {
		if(color.equals(Color.BLUE)) {
			return 11;
		}
		else if(color.equals(Color.RED)) {
			return 14;
		}
		else if(color.equals(Color.YELLOW)) {
			return 4;
		}
		else if(color.equals(Color.BLACK)) {
			return 15;
		}
		else if(color.equals(Color.AQUA)) {
			return 3;
		}
		else if(color.equals(Color.BLUE)) {
			return 11;
		}
		else if(color.equals(Color.NAVY)) {
			return 11;
		}
		else if(color.equals(Color.GRAY)) {
			return 8;
		}
		else if(color.equals(Color.OLIVE)) {
			return 13;
		}
		else if(color.equals(Color.PURPLE)) {
			return 10;
		}
		else if(color.equals(Color.ORANGE)) {
			return 1;
		}
		else if(color.equals(Color.SILVER)) {
			return 7;
		}
		else if(color.equals(Color.LIME)) {
			return 5;
		}
		else if(color.equals(Color.GREEN)) {
			return 13;
		}
		else if(color.equals(Color.FUCHSIA)) {
			return 6;
		}
		else {
			return 0;
		}
	}
	
	public static char getLastColor(String text) {
		int i = text.lastIndexOf("&");
		return i == -1 ? 'f' : text.charAt(i+1);
	}
	
	public static List<String> replaceAll(List<String> list, String[] regex, String[] replacement){
		if(regex.length != replacement.length) {
			throw new IllegalArgumentException("regex and replacement has to be the same length!");
		}
		List<String> result = new ArrayList<>();
		for(String str : list) {
			for(int i = 0; i<regex.length; i++) {
				str = str.replaceAll(regex[i], replacement[i]);
			}
			result.add(str);
		}
		return result;
	}
	
	public static void dyePlayer(Player player, Color color) {
		player.getInventory().setHelmet(dyeLeatherArmor(Material.LEATHER_HELMET, color));
		player.getInventory().setChestplate(dyeLeatherArmor(Material.LEATHER_CHESTPLATE, color));
		player.getInventory().setLeggings(dyeLeatherArmor(Material.LEATHER_LEGGINGS, color));
		player.getInventory().setBoots(dyeLeatherArmor(Material.LEATHER_BOOTS, color));
	}
	
	private static ItemStack dyeLeatherArmor(Material material, Color color) {
		ItemStack item = new ItemStack(material);
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(color);
		item.setItemMeta(meta);
		return item;
	}
	
	public static List<Block> getBlocksInRadius(Location location, int radius, boolean hollow) {
		List<Block> blocks = new ArrayList<>();
		int bX = location.getBlockX(), bY = location.getBlockY(), bZ = location.getBlockZ();
		for (int x = bX - radius; x <= bX + radius; x++)
			for (int y = bY - radius; y <= bY + radius; y++)
				for (int z = bZ - radius; z <= bZ + radius; z++) {
					double distance = ((bX - x) * (bX - x) + (bY - y) * (bY - y) + (bZ - z) * (bZ - z));
					if (distance < radius * radius && !(hollow && distance < ((radius - 1) * (radius - 1)))) {
						Location l = new Location(location.getWorld(), x, y, z);
						if (l.getBlock().getType() != Material.BARRIER)
							blocks.add(l.getBlock());
					}
				}
		return blocks;
	}
	
	public static ItemStack getCustomTextureHead(String texture) {
		return SharedHandler.getNmsHandler().getCustomTextureHead(texture);
	}
	
	public static Vector rotateAroundY(Vector vector, double angle) {
	    double angleCos = Math.cos(angle);
	    double angleSin = Math.sin(angle);
	    double x = angleCos * vector.getX() - angleSin * vector.getZ();
	    double z = angleSin * vector.getX() + angleCos * vector.getZ();
	    return vector.setX(x).setZ(z);
	}
	
	public static Vector rotateAroundAxis(Vector vector, Vector axis, double theta) {
	
		axis.normalize();
		double x = vector.getX();
		double y = vector.getY();
		double z = vector.getZ();
		double u = axis.getX();
		double v = axis.getY();
		double w = axis.getZ();
		
		double xPrime = u*(u*x + v*y + w*z)*(1d - Math.cos(theta)) 
                + x*Math.cos(theta)
                + (-w*y + v*z)*Math.sin(theta);
		double yPrime = v*(u*x + v*y + w*z)*(1d - Math.cos(theta))
                + y*Math.cos(theta)
                + (w*x - u*z)*Math.sin(theta);
		double zPrime = w*(u*x + v*y + w*z)*(1d - Math.cos(theta))
                + z*Math.cos(theta)
                + (-v*x + u*y)*Math.sin(theta);
	
		return vector.setX(xPrime).setY(yPrime).setZ(zPrime);
	}
	
	public static void setTagPrefix(Player player, String playerName, String group, String prefix) {
		org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
		Objective obj = scoreboard.getObjective(player.getName());
		if(obj == null) {
			return;
		}
		org.bukkit.scoreboard.Team sbteam;
		if(scoreboard.getTeam(group) != null) {
			sbteam = scoreboard.getTeam(group);
		}
		else {
			sbteam = scoreboard.registerNewTeam(group);
		}
		if(!sbteam.getEntries().contains(playerName)) {
			sbteam.addEntry(playerName);
			
	        obj.getScore(playerName).setScore(-1);
		}
		if(!sbteam.getPrefix().equals(ChatUtils.format(prefix))) {
			sbteam.setPrefix(ChatUtils.format(prefix));
		}
	}
}
