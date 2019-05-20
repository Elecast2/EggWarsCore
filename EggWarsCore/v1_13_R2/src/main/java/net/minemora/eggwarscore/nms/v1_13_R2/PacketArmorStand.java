package net.minemora.eggwarscore.nms.v1_13_R2;

import java.util.NoSuchElementException;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_13_R2.EnumItemSlot;
import net.minecraft.server.v1_13_R2.EntityArmorStand;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_13_R2.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_13_R2.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_13_R2.WorldServer;
import net.minemora.eggwarscore.nms.AArmorStandPose;
import net.minemora.eggwarscore.nms.IPacketArmorStand;

public class PacketArmorStand implements IPacketArmorStand {
	
	private EntityArmorStand NMSArmorStand;
	private ArmorStandPose pose = new ArmorStandPose();

	Location location;
	
	public PacketArmorStand(Location location, ItemStack helmet, ItemStack chestPlate, ItemStack leggings, ItemStack boots, ItemStack itemInRightArm) {
		this.location = location;
		WorldServer s = ((CraftWorld)location.getWorld()).getHandle();
		NMSArmorStand = new EntityArmorStand(s);
        
		NMSArmorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);
		NMSArmorStand.setArms(true);
		NMSArmorStand.setBasePlate(true);
		NMSArmorStand.setNoGravity(true);
        
        PacketPlayOutSpawnEntityLiving spawnpacket = new PacketPlayOutSpawnEntityLiving(NMSArmorStand);
        
        PacketPlayOutEntityEquipment helmetPacket = null;
        PacketPlayOutEntityEquipment chestPlatePacket = null;
        PacketPlayOutEntityEquipment leggingsPacket = null;
        PacketPlayOutEntityEquipment bootsPacket = null;
        PacketPlayOutEntityEquipment itemInRightArmPacket = null;
        		
        if(helmet != null) {
        	helmetPacket = new PacketPlayOutEntityEquipment(NMSArmorStand.getId(), EnumItemSlot.HEAD, CraftItemStack.asNMSCopy(helmet));  
        }
		if(chestPlate != null) {
			chestPlatePacket = new PacketPlayOutEntityEquipment(NMSArmorStand.getId(), EnumItemSlot.CHEST, CraftItemStack.asNMSCopy(chestPlate));  
		}
		if(leggings != null) {
			leggingsPacket = new PacketPlayOutEntityEquipment(NMSArmorStand.getId(), EnumItemSlot.LEGS, CraftItemStack.asNMSCopy(leggings));  
		}
		if(boots != null) {
			bootsPacket = new PacketPlayOutEntityEquipment(NMSArmorStand.getId(), EnumItemSlot.FEET, CraftItemStack.asNMSCopy(boots));  
		}
		if(itemInRightArm != null) {
			itemInRightArmPacket = new PacketPlayOutEntityEquipment(NMSArmorStand.getId(), EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(itemInRightArm));  
		}
               
        for(Entity ent : location.getWorld().getNearbyEntities(location, 32, 32, 32)) {
        	if(ent instanceof Player) {
        		Player p = (Player)ent;
        		((CraftPlayer)p).getHandle().playerConnection.sendPacket(spawnpacket);
        		if(helmetPacket != null) {
        			((CraftPlayer)p).getHandle().playerConnection.sendPacket(helmetPacket);
        		}
        		if(chestPlatePacket != null) {
        			((CraftPlayer)p).getHandle().playerConnection.sendPacket(chestPlatePacket);
        		}
        		if(leggingsPacket != null) {
        			((CraftPlayer)p).getHandle().playerConnection.sendPacket(leggingsPacket);
        		}
        		if(bootsPacket != null) {
        			((CraftPlayer)p).getHandle().playerConnection.sendPacket(bootsPacket);
        		}
        		if(itemInRightArmPacket != null) {
        			((CraftPlayer)p).getHandle().playerConnection.sendPacket(itemInRightArmPacket);
        		}
        	}
        }
	}
	
	@Override
	public void update() {
		Vector vec1 = location.getDirection().clone().setY(0).normalize().multiply(pose.getZoff());
		Vector vec2 = location.getDirection().clone().setY(0).normalize().multiply(pose.getXoff());
		double xoff1 = vec1.getX();
		double zoff1 = vec1.getZ();
		double xoff2 = vec2.getZ();
		double zoff2 = -vec2.getX();
		location.add(xoff1 + xoff2, pose.getYoff(), zoff1 + zoff2);
		location.setYaw(location.getYaw() + pose.getRotation());
		NMSArmorStand.setHeadPose(pose.getHeadPose());
		NMSArmorStand.setBodyPose(pose.getBodyPose());
		NMSArmorStand.setRightArmPose(pose.getRightArmPose());
		NMSArmorStand.setLeftArmPose(pose.getLeftArmPose());
		NMSArmorStand.setRightLegPose(pose.getRightLegPose());
		NMSArmorStand.setLeftLegPose(pose.getLeftLegPose());
		NMSArmorStand.setPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);
		PacketPlayOutEntityMetadata packetupdate = 
				new PacketPlayOutEntityMetadata(NMSArmorStand.getId(), NMSArmorStand.getDataWatcher(), false);
		PacketPlayOutEntityTeleport packetpos = new PacketPlayOutEntityTeleport(NMSArmorStand);
		
		try {
			//TODO check if if empty or null
			for(Entity ent : location.getWorld().getNearbyEntities(location, 32, 32, 32)) {
	        	if(ent instanceof Player) {
	        		Player p = (Player)ent;
	        		((CraftPlayer)p).getHandle().playerConnection.sendPacket(packetupdate);
	        		((CraftPlayer)p).getHandle().playerConnection.sendPacket(packetpos);
	        	}
			}
		} catch(NoSuchElementException e) {
			return;
		}
	}
	
	@Override
	public void destroy() {
		PacketPlayOutEntityDestroy packetdestroy = new PacketPlayOutEntityDestroy(NMSArmorStand.getId());
		//TODO check if if empty or null
		for(Entity ent : location.getWorld().getNearbyEntities(location, 32, 32, 32)) {
        	if(ent instanceof Player) {
        		Player p = (Player)ent;
        		((CraftPlayer)p).getHandle().playerConnection.sendPacket(packetdestroy);
        	}
		}
	}

	public ArmorStandPose getPose() {
		return pose;
	}
	
	@Override
	public <T extends AArmorStandPose> void setPose(T pose) {
		this.pose = (ArmorStandPose) pose;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
