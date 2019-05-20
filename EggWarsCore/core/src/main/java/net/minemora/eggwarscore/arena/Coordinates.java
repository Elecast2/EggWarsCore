package net.minemora.eggwarscore.arena;

import org.bukkit.Location;
import org.bukkit.World;

public class Coordinates {

	private double x;
	private double y;
	private double z;
	private float pitch;
	private float yaw;

	public Coordinates(double x, double y, double z, float pitch, float yaw) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
	}

	public Coordinates(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = 0;
		this.yaw = 0;
	}

	public Coordinates(Location loc) {
		this.x = loc.getX();
		this.y = loc.getY();
		this.z = loc.getZ();
		this.pitch = loc.getPitch();
		this.yaw = loc.getYaw();
	}

	public Location toLocation(World world) {
		return new Location(world, x, y, z, yaw, pitch);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public float getPitch() {
		return pitch;
	}

	public void setPitch(float pitch) {
		this.pitch = pitch;
	}

	public float getYaw() {
		return yaw;
	}

	public void setYaw(float yaw) {
		this.yaw = yaw;
	}
}
