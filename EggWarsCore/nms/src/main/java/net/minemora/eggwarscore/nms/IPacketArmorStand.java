package net.minemora.eggwarscore.nms;

import org.bukkit.Location;

public interface IPacketArmorStand {
	
	public void update();
	
	public void destroy();
	
	public <T extends AArmorStandPose> void setPose(T pose);

	public void setLocation(Location location);
}
