package net.minemora.eggwarscore.nms;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public interface NMS {
	
	public void removeArrows(Player player);
	
	public void makeStatue(Entity entity);
	
	public ItemStack getCustomTextureHead(String texture);
	
	public void sendTitleToPlayer(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle);
	
	public void sendActionBar(Player player, String message);
	
	public void createNPC(Plugin plugin, Player player, int index, Location loc, String skin, String sign);
	
	public void playEnderChestAction(Location location, boolean open);
	
	public AArmorStandPose getNewArmorStandPose();
	
	public IPacketArmorStand getNewPacketArmorStand(Location location, ItemStack helmet, ItemStack chestPlate, 
			ItemStack leggings, ItemStack boots, ItemStack itemInRightArm);

}