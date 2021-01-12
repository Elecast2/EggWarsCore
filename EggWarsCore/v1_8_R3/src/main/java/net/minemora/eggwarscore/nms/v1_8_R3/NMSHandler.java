package net.minemora.eggwarscore.nms.v1_8_R3;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EntityTracker;
import net.minecraft.server.v1_8_R3.EntityTrackerEntry;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IntHashMap;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.TileEntityEnderChest;
import net.minecraft.server.v1_8_R3.World;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minemora.eggwarscore.nms.AArmorStandPose;
import net.minemora.eggwarscore.nms.APlayerHolo;
import net.minemora.eggwarscore.nms.IPacketArmorStand;
import net.minemora.eggwarscore.nms.NMS;

public class NMSHandler implements NMS {

	@Override
	public void removeArrows(Player player) {
		((CraftPlayer) player).getHandle().getDataWatcher().watch(9, (byte) 0);
	}

	@Override
	public void makeStatue(Entity entity) {
		net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
	    NBTTagCompound tag = nmsEntity.getNBTTag();
	    if (tag == null) {
	        tag = new NBTTagCompound();
	    }
	    nmsEntity.c(tag);
	    tag.setInt("NoAI", 1);
	    tag.setInt("Silent", 1);
	    tag.setInt("Invulnerable", 1);
	    nmsEntity.f(tag);
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
        PlayerConnection con = ((CraftPlayer) player).getHandle().playerConnection;
        con.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut));
        if (subtitle != null) {
            con.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}")));
        }
        if (title != null) {
            con.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}")));
        }
    }

	@Override
	public void createNPC(Plugin plugin, Player player, int index, Location loc, String skin, String sign) {
		MinecraftServer mserver = ((CraftServer) Bukkit.getServer()).getServer();
	    WorldServer wserver = ((CraftWorld) loc.getWorld()).getHandle();
	    
	    GameProfile gp = new GameProfile(UUID.randomUUID(), "");
	    gp.getProperties().put("textures", new Property("textures", skin, sign));
	    
	    EntityPlayer ep = new EntityPlayer(mserver, wserver, gp, new PlayerInteractManager(wserver));
	    ep.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	    ep.d(1000 + index);
	    
	    DataWatcher watcher = ep.getDataWatcher();
	    watcher.watch(10, (byte) 127);
	    
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
        world.playBlockAction(position, tileChest.w(), 1, open ? 1 : 0);
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
		CraftPlayer cp = (CraftPlayer) player;
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(ChatSerializer.a("{\"text\": \"" + message + "\"}"), (byte) 2);
        cp.getHandle().playerConnection.sendPacket(packetPlayOutChat);
	}
	
	private static Field trackedField;
	private static Field entitiesByIdField;
	private static Field entitiesByUUIDField;
	
	static {
		try {
        	trackedField = EntityTracker.class.getDeclaredField("c");
            trackedField.setAccessible(true);
            entitiesByIdField = net.minecraft.server.v1_8_R3.World.class.getDeclaredField("entitiesById");
            entitiesByIdField.setAccessible(true);
            entitiesByUUIDField = WorldServer.class.getDeclaredField("entitiesByUUID");
            entitiesByUUIDField.setAccessible(true);
        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void removeWorldFromMemory(org.bukkit.World world) {
		WorldServer ws = ((CraftWorld)world).getHandle();
		ws.chunkProviderServer.chunks.clear();
		ws.chunkProviderServer.unloadQueue.clear();
		ws.chunkProviderServer = null;
		ws.entityList.clear();
		ws.h.clear();
		ws.k.clear();
		ws.tileEntityList.clear();
		ws.tracker.trackedEntities.c();
		ws.capturedBlockStates.clear();
		ws.capturedTileEntities.clear();
        try {
			Set<EntityTrackerEntry> trackedSet = (Set<EntityTrackerEntry>) trackedField.get(ws.tracker);
            trackedSet.clear();
            
            IntHashMap<Entity> entbyid = (IntHashMap<Entity>) entitiesByIdField.get((net.minecraft.server.v1_8_R3.World)ws);
            entbyid.c();
            
            Map<UUID, Entity> entbyuid = (Map<UUID, Entity>) entitiesByUUIDField.get(ws);
            entbyuid.clear();
        } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
            e.printStackTrace();
        }
	}

	@Override
	public APlayerHolo createPlayerHolo(Player player, Location location, String text) {
		return new PlayerHolo(player, location, text);
	}
}