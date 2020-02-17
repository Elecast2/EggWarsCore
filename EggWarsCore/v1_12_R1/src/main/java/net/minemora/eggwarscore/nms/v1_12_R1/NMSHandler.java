package net.minemora.eggwarscore.nms.v1_12_R1;

import java.lang.reflect.Field;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_12_R1.DataWatcherObject;
import net.minecraft.server.v1_12_R1.DataWatcherRegistry;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import net.minecraft.server.v1_12_R1.PlayerInteractManager;
import net.minecraft.server.v1_12_R1.WorldServer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.TileEntityEnderChest;
import net.minecraft.server.v1_12_R1.World;
import net.minecraft.server.v1_12_R1.DataWatcher;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_12_R1.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import net.minemora.eggwarscore.nms.AArmorStandPose;
import net.minemora.eggwarscore.nms.IPacketArmorStand;
import net.minemora.eggwarscore.nms.NMS;

public class NMSHandler implements NMS {

	@Override
	public void removeArrows(Player player) {
		((CraftPlayer)player).getHandle().getDataWatcher().set(new DataWatcherObject<>(10, DataWatcherRegistry.b),0);
	}

	@Override
	public void makeStatue(Entity entity) {
	    ((LivingEntity)entity).setAI(false);
	    ((LivingEntity)entity).setSilent(false);
	    ((LivingEntity)entity).setInvulnerable(false);
	}
	
	@Override
	public ItemStack getCustomTextureHead(String texture) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		GameProfile profile = new GameProfile(UUID.randomUUID(), "");
	    profile.getProperties().put("textures", new Property("textures", texture));
	    Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        head.setItemMeta(meta);
		return head;
	}
	
	@Override
	public void sendTitleToPlayer(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
		player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		
	}

	@Override
	public void createNPC(Plugin plugin, Player player, int index, Location loc, String skin, String sign) {
		MinecraftServer mserver = ((CraftServer) Bukkit.getServer()).getServer();
	    WorldServer wserver = ((CraftWorld) loc.getWorld()).getHandle();
	    
	    GameProfile gp = new GameProfile(UUID.randomUUID(), "");
	    gp.getProperties().put("textures", new Property("textures", skin, sign));
	    
	    EntityPlayer ep = new EntityPlayer(mserver, wserver, gp, new PlayerInteractManager(wserver));
	    ep.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	    ep.h(1000 + index);
	    
	    DataWatcher watcher = ep.getDataWatcher();
	    watcher.set(new DataWatcherObject<>(13, DataWatcherRegistry.a), (byte)127);
	    
	    PlayerConnection pconn = ((CraftPlayer) player).getHandle().playerConnection;
	    
	    pconn.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, ep));
	    pconn.sendPacket(new PacketPlayOutNamedEntitySpawn(ep));
	    pconn.sendPacket(new PacketPlayOutEntityHeadRotation(ep, (byte) (ep.yaw * 256 / 360)));
	    pconn.sendPacket(new PacketPlayOutEntityMetadata(ep.getId(), watcher, true));
	    
	    new BukkitRunnable() {
            @Override
            public void run() {
           	 
           	 if(player.isOnline()) {
           	 
	            	PlayerConnection pconn = ((CraftPlayer) player).getHandle().playerConnection;
	         		 
	                pconn.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ep));
           	 }
            }
        }.runTaskLaterAsynchronously(plugin, 60);
	}
	
	@Override
	public void playEnderChestAction(Location location, boolean open) {
		World world = ((CraftWorld) location.getWorld()).getHandle();
        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        TileEntityEnderChest tileChest = (TileEntityEnderChest) world.getTileEntity(position);
        world.playBlockAction(position, tileChest.getBlock(), 1, open ? 1 : 0);
	}
	
	@Override
	public AArmorStandPose getNewArmorStandPose() {
		return new ArmorStandPose();
	}

	@Override
	public IPacketArmorStand getNewPacketArmorStand(Location location, ItemStack helmet, ItemStack chestPlate,
			ItemStack leggings, ItemStack boots, ItemStack itemInRightArm) {
		return new PacketArmorStand(location, helmet, chestPlate, leggings, boots, itemInRightArm);
	}

	@Override
	public void sendActionBar(Player player, String message) {
		player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new ComponentBuilder(message).create());
	}

	@Override
	public void removeWorldFromMemory(org.bukkit.World world) {
		return;
	}
}