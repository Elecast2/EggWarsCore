package net.minemora.eggwarscore.nms.v1_8_R3;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minemora.eggwarscore.nms.APlayerHolo;

public class PlayerHolo extends APlayerHolo {
	
	private EntityArmorStand NMSArmorStand;

	public PlayerHolo(Player player, Location location, String text) {
		super(player, location, text);
		
		WorldServer s = ((CraftWorld)location.getWorld()).getHandle();
		NMSArmorStand = new EntityArmorStand(s);
		NMSArmorStand.setLocation(location.getX(), location.getY(), location.getZ(), location.getYaw(), 0);
		NMSArmorStand.setGravity(false);
		NMSArmorStand.setInvisible(true);
		NMSArmorStand.setCustomName(text);
		NMSArmorStand.setCustomNameVisible(true);
		
		PacketPlayOutSpawnEntityLiving spawnpacket = new PacketPlayOutSpawnEntityLiving(NMSArmorStand);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(spawnpacket);
	}

	@Override
	protected void updateTextNMS(String text) {
		Player player = Bukkit.getPlayer(getPlayerName());
		if(player == null) {
			NMSArmorStand = null;
			return;
		}
		NMSArmorStand.setCustomName(text);
		PacketPlayOutEntityMetadata packetupdate = new PacketPlayOutEntityMetadata(NMSArmorStand.getId(), NMSArmorStand.getDataWatcher(), false);
		((CraftPlayer)player).getHandle().playerConnection.sendPacket(packetupdate);
	}

	public EntityArmorStand getNMSArmorStand() {
		return NMSArmorStand;
	}

	public void setNMSArmorStand(EntityArmorStand nMSArmorStand) {
		NMSArmorStand = nMSArmorStand;
	}

}
