package net.minemora.eggwarscore.extras.trail;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import net.minemora.eggwarscore.config.extras.ConfigTrails;
import net.minemora.eggwarscore.utils.ParticleEffect;
import net.minemora.eggwarscore.utils.ParticleEffect.ParticleProperty;
import net.minemora.eggwarscore.utils.UtilParticles;
import net.minemora.eggwarscore.utils.Utils;

public class TrailEffect {
	
	private ParticleEffect particle;
	private int period = 1;
	private int delay = 0;
	private int amount = 1;
	
	private float centerOffsetX = 0;
	private float centerOffsetY = 0;
	private float centerOffsetZ = 0;
	
	private float distanceOffsetX = 0;
	private float distanceOffsetY = 0;
	private float distanceOffsetZ = 0;
	
	private Material material;
	private byte data;
	
	private TrailType type;
	
	
	public TrailEffect(ParticleEffect particle) {
		this.particle = particle;
	}
	
	public void display(Projectile projectile) {
		
		if(type == TrailType.DEFAULT) {
			if(particle.hasProperty(ParticleProperty.REQUIRES_DATA)) {
				if(particle == ParticleEffect.ITEM_CRACK) {
					ParticleEffect.ItemData itemData = new ParticleEffect.ItemData(material, data);
					particle.display(itemData, distanceOffsetX, distanceOffsetY, distanceOffsetZ, 0, amount, getRotatedLocation(
							projectile.getLocation(), projectile.getVelocity(), centerOffsetX, centerOffsetY, centerOffsetZ), 128);
				}
				else {
					ParticleEffect.BlockData blockData = new ParticleEffect.BlockData(material, data);
					particle.display(blockData, distanceOffsetX, distanceOffsetY, distanceOffsetZ, 0, amount, getRotatedLocation(
							projectile.getLocation(), projectile.getVelocity(), centerOffsetX, centerOffsetY, centerOffsetZ), 128);
				}
			}
			else {
				particle.display(distanceOffsetX, distanceOffsetY, distanceOffsetZ, 0, amount, getRotatedLocation(
						projectile.getLocation(), projectile.getVelocity(), centerOffsetX, centerOffsetY, centerOffsetZ), 128);
			}
		}
		
		
		else if(type == TrailType.CIRCLE) {
			Location center = getRotatedLocation(projectile.getLocation(), projectile.getVelocity(), 
					centerOffsetX, centerOffsetY, centerOffsetZ);
			for(int i = 0; i<32; i++) {
				double alpha = i*(360/32);
				alpha = Math.toRadians(alpha);
				double offz = Math.sin(alpha);
				double offy = Math.cos(alpha);
				Vector vector = getRotatedLocation(center, projectile.getVelocity(), 0, offy, offz)
						.toVector().subtract(center.toVector()).normalize();
				if(particle.hasProperty(ParticleProperty.REQUIRES_DATA)) {
					if(particle == ParticleEffect.ITEM_CRACK) {
						ParticleEffect.ItemData itemData = new ParticleEffect.ItemData(material, data);
						particle.display(itemData, vector, 0.12f, center, 128);
					}
					else {
						ParticleEffect.BlockData blockData = new ParticleEffect.BlockData(material, data);
						particle.display(blockData, vector, 0.12f, center, 128);
					}
				}
				else {
					particle.display(vector, 0.12f, center, 128);
				}
			}
		}
		
		
		else if(type == TrailType.COLOR) {
			UtilParticles.display(particle, (int)distanceOffsetX, (int)distanceOffsetY, (int)distanceOffsetZ, getRotatedLocation(
					projectile.getLocation(), projectile.getVelocity(), centerOffsetX, centerOffsetY, centerOffsetZ), amount);
		}
		
		
		else if(type == TrailType.HEART) {
			Location center = getRotatedLocation(projectile.getLocation(), projectile.getVelocity(), 
					centerOffsetX, centerOffsetY, centerOffsetZ);
			for(int i = 0; i<32; i++) {
				float speed;
				switch(i) {
				case 0: speed = 0.1f; break; case 1: speed = 0.2f; break; case 2: speed = 0.34f; break; case 3: speed = 0.45f; break;
				case 4: speed = 0.6f; break; case 5: speed = 0.74f; break; case 6: speed = 0.87f; break; case 7: speed = 0.95f; break;
				case 8: speed = 1; break; case 9: speed = 1.01f; break; case 10: speed = 1.08f; break; case 11: speed = 1.15f; break;
				case 12: speed = 1.2f; break; case 13: speed = 1.25f; break; case 14: speed = 1.3f; break; case 15: speed = 1.35f; break;
				case 16: speed = 1.41f; break; case 17: speed = 1.35f; break; case 18: speed = 1.3f; break; case 19: speed = 1.25f; break;
				case 20: speed = 1.2f; break; case 21: speed = 1.15f; break; case 22: speed = 1.08f; break; case 23: speed = 1.01f; break;
				case 24: speed = 1; break; case 25: speed = 0.95f; break; case 26: speed = 0.87f; break; case 27: speed = 0.74f; break;
				case 28: speed = 0.6f; break; case 29: speed = 0.45f; break; case 30: speed = 0.34f; break; case 31: speed = 0.2f; break;
				default: speed = 0;
				}
				speed = speed/10;
				double alpha = i*(360/32);
				alpha = Math.toRadians(alpha);
				double offz = Math.sin(alpha);
				double offy = Math.cos(alpha);
				Vector vector = getRotatedLocation(center, projectile.getVelocity(), 0, offy, offz)
						.toVector().subtract(center.toVector()).normalize();
				if(particle.hasProperty(ParticleProperty.REQUIRES_DATA)) {
					if(particle == ParticleEffect.ITEM_CRACK) {
						ParticleEffect.ItemData itemData = new ParticleEffect.ItemData(material, data);
						particle.display(itemData, vector, speed, center, 128);
					}
					else {
						ParticleEffect.BlockData blockData = new ParticleEffect.BlockData(material, data);
						particle.display(blockData, vector, speed, center, 128);
					}
				}
				else {
					particle.display(vector, speed, center, 128);
				}
			}
		}
	}
	
	private Location getRotatedLocation(Location center, Vector vector, double x, double y, double z) {
		Location fixedCenter = center.clone();
		
		Vector vAdd = new Vector(x,y,z);

		double theta = Math.PI + Math.atan2(-vector.getZ(),-vector.getX());
		double phi = Math.acos(vector.getY()/vector.length());
		phi = -phi + (Math.PI/2);
		
		vAdd = Utils.rotateAroundY(vAdd, theta);
		
		Vector axis = new Vector(vector.getX(), 0 , vector.getZ());
		axis = Utils.rotateAroundY(axis, Math.PI/2);
		
		vAdd = Utils.rotateAroundAxis(vAdd, axis, phi);
		
		return fixedCenter.add(vAdd.getX(), vAdd.getY(), vAdd.getZ());
	}
	                 
	public static TrailEffect deserealize(int trailId, int particleId) {
		String path = "trails." + trailId + ".particles." + particleId;
		ParticleEffect effect = ParticleEffect.valueOf(ConfigTrails.get().getString(path + ".particle"));
		TrailEffect trailEffect = new TrailEffect(effect);
		trailEffect.setPeriod(ConfigTrails.get().getInt(path + ".period", 1));
		trailEffect.setDelay(ConfigTrails.get().getInt(path + ".delay", 0));
		trailEffect.setAmount(ConfigTrails.get().getInt(path + ".amount", 1));
		
		if(ConfigTrails.get().contains(path + ".material")) {
			trailEffect.setMaterial(Material.valueOf(ConfigTrails.get().getString(path + ".material", "WOOL")));
		}
		if(ConfigTrails.get().contains(path + ".data")) {
			trailEffect.setData((byte)ConfigTrails.get().getInt(path + ".data", 0));
		}

		trailEffect.setType(TrailType.valueOf(ConfigTrails.get().getString(path + ".effect", "DEFAULT")));
		
		String[] values = ConfigTrails.get().getString(path + ".values", "0:0:0:0:0:0").split(":");
		
		trailEffect.setDistanceOffsetX(Float.valueOf(values[0]));
		trailEffect.setDistanceOffsetY(Float.valueOf(values[1]));
		trailEffect.setDistanceOffsetZ(Float.valueOf(values[2]));
		
		trailEffect.setCenterOffsetX(Float.valueOf(values[3]));
		trailEffect.setCenterOffsetY(Float.valueOf(values[4]));
		trailEffect.setCenterOffsetZ(Float.valueOf(values[5]));
		
		return trailEffect;
	}
	
	public ParticleEffect getParticle() {
		return particle;
	}
	
	public void setParticle(ParticleEffect particle) {
		this.particle = particle;
	}
	
	public int getPeriod() {
		return period;
	}
	
	public void setPeriod(int period) {
		if(period <= 0) {
			this.period = 1;
		}
		else {
			this.period = period;
		}
	}
	
	public int getDelay() {
		return delay;
	}
	
	public void setDelay(int delay) {
		if(delay < 0) {
			this.delay = 0;
		}
		else {
			this.delay = delay;
		}
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		if(amount <= 0) {
			this.amount = 1;
		}
		else {
			this.amount = amount;
		}
	}
	
	public float getCenterOffsetX() {
		return centerOffsetX;
	}
	
	public void setCenterOffsetX(float centerOffsetX) {
		this.centerOffsetX = centerOffsetX;
	}
	
	public float getCenterOffsetY() {
		return centerOffsetY;
	}
	
	public void setCenterOffsetY(float centerOffsetY) {
		this.centerOffsetY = centerOffsetY;
	}
	
	public float getCenterOffsetZ() {
		return centerOffsetZ;
	}
	
	public void setCenterOffsetZ(float centerOffsetZ) {
		this.centerOffsetZ = centerOffsetZ;
	}
	
	public float getDistanceOffsetX() {
		return distanceOffsetX;
	}
	
	public void setDistanceOffsetX(float distanceOffsetX) {
		this.distanceOffsetX = distanceOffsetX;
	}
	
	public float getDistanceOffsetY() {
		return distanceOffsetY;
	}
	
	public void setDistanceOffsetY(float distanceOffsetY) {
		this.distanceOffsetY = distanceOffsetY;
	}
	
	public float getDistanceOffsetZ() {
		return distanceOffsetZ;
	}
	
	public void setDistanceOffsetZ(float distanceOffsetZ) {
		this.distanceOffsetZ = distanceOffsetZ;
	}

	public TrailType getType() {
		return type;
	}

	public void setType(TrailType type) {
		this.type = type;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public byte getData() {
		return data;
	}

	public void setData(byte data) {
		this.data = data;
	}
}