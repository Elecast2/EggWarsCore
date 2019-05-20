package net.minemora.eggwarscore.asa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.nms.AArmorStandPose;
import net.minemora.eggwarscore.nms.IPacketArmorStand;
import net.minemora.eggwarscore.shared.SharedHandler;

public class Animation {
	
	private Map<Integer,AArmorStandPose> poses = new HashMap<>();
	private String[] equipment;
	
	public Animation(String fileName) {
		InputStream is = SharedHandler.getPlugin().getResource("animations/" + fileName + ".asa");
		Reader reader = new InputStreamReader(is);
		deserialize(reader);
	}
	//TODO añadir otro metodo de play pro con el equipment
	public void play(Location loc, long period, int loops) {
		IPacketArmorStand pas = SharedHandler.getNmsHandler().getNewPacketArmorStand(loc,
				new ItemStack(Material.getMaterial(equipment[0])),
				new ItemStack(Material.getMaterial(equipment[1])),
				new ItemStack(Material.getMaterial(equipment[2])),
				new ItemStack(Material.getMaterial(equipment[3])),
				new ItemStack(Material.getMaterial(equipment[4])));
		new BukkitRunnable() {
        	int loop = 0;
        	int count = 0;
        	@Override
        	public void run() {
        		pas.setPose(poses.get(count));
        		pas.update();
    	        count++;
    	        if(loop == loops) {
    	        	pas.destroy();
    	        	cancel();
    	        }
    	        if(count == poses.size()) {
    	        	count = 0;
    	        	loop++;
    	        }
        	}
        }.runTaskTimerAsynchronously(SharedHandler.getPlugin(), 5L, period);
	}
	
	public void play(Location loc, long period, int loops, ItemStack head) {
		IPacketArmorStand pas = SharedHandler.getNmsHandler().getNewPacketArmorStand(loc, head,
				new ItemStack(Material.getMaterial(equipment[1])),
				new ItemStack(Material.getMaterial(equipment[2])),
				new ItemStack(Material.getMaterial(equipment[3])),
				new ItemStack(Material.getMaterial(equipment[4])));
		new BukkitRunnable() {
        	int loop = 0;
        	int count = 0;
        	@Override
        	public void run() {
        		pas.setPose(poses.get(count));
        		pas.update();
    	        count++;
    	        if(loop == loops) {
    	        	pas.destroy();
    	        	cancel();
    	        }
    	        if(count == poses.size()) {
    	        	count = 0;
    	        	loop++;
    	        }
        	}
        }.runTaskTimerAsynchronously(SharedHandler.getPlugin(), 5L, period);
	}
	
	private void deserialize(Reader reader) {
		try {
			int count = 0;
			BufferedReader entrada = new BufferedReader(reader);
			String lectura;
			lectura = entrada.readLine();
			while (lectura != null) {
				if(lectura.contains(";")) {
					AArmorStandPose pose = SharedHandler.getNmsHandler().getNewArmorStandPose();
					String[] parts = lectura.split(";");
					for(int i = 0; i<parts.length; i++) {
						String bodyPart = parts[i].substring(0, parts[i].indexOf(":"));
						String part = parts[i].substring(parts[i].indexOf("[")+1,parts[i].indexOf("]"));
						String[] subparts = part.split(",");
						if(bodyPart.equals("Body")) {
							pose.setBodyPose(Float.parseFloat(subparts[0]), Float.parseFloat(subparts[1]), Float.parseFloat(subparts[2]));
						}
						else if(bodyPart.equals("Head")) {
							pose.setHeadPose(Float.parseFloat(subparts[0]), Float.parseFloat(subparts[1]), Float.parseFloat(subparts[2]));
						}
						else if(bodyPart.equals("LeftArm")) {
							pose.setLeftArmPose(Float.parseFloat(subparts[0]), Float.parseFloat(subparts[1]), Float.parseFloat(subparts[2]));
						}
						else if(bodyPart.equals("RightArm")) {
							pose.setRightArmPose(Float.parseFloat(subparts[0]), Float.parseFloat(subparts[1]), Float.parseFloat(subparts[2]));
						}
						else if(bodyPart.equals("LeftLeg")) {
							pose.setLeftLegPose(Float.parseFloat(subparts[0]), Float.parseFloat(subparts[1]), Float.parseFloat(subparts[2]));
						}
						else if(bodyPart.equals("RightLeg")) {
							pose.setRightLegPose(Float.parseFloat(subparts[0]), Float.parseFloat(subparts[1]), Float.parseFloat(subparts[2]));
						}
						else if(bodyPart.equals("Position")) {
							pose.setXoff(Double.parseDouble(subparts[0]));
							pose.setYoff(Double.parseDouble(subparts[1]));
							pose.setZoff(Double.parseDouble(subparts[2]));
						}
						else if(bodyPart.equals("Rotation")) {
							pose.setRotation(Float.parseFloat(part));
						}
					}
					poses.put(count, pose);
					count++;
				}
				else {
					String part = lectura.substring(lectura.indexOf("[")+1,lectura.indexOf("]"));
					String[] subparts = part.split(",");
					setEquipment(subparts);
				}
				lectura = entrada.readLine();
			}
			entrada.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public String[] getEquipment() {
		return equipment;
	}

	public void setEquipment(String[] equipment) {
		this.equipment = equipment;
	}

	public Map<Integer,AArmorStandPose> getPoses() {
		return poses;
	}

	public void setPoses(Map<Integer,AArmorStandPose> poses) {
		this.poses = poses;
	}
}
