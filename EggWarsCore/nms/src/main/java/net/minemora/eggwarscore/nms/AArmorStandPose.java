package net.minemora.eggwarscore.nms;

public abstract class AArmorStandPose {
	
	private float rotation = 0;
	private double xoff = 0;
	private double yoff = 0;
	private double zoff = 0;
	
	public abstract void setHeadPose(float x, float y, float z);
	
	public abstract void setBodyPose(float x, float y, float z);
	
	public abstract void setLeftArmPose(float x, float y, float z);
	
	public abstract void setRightArmPose(float x, float y, float z);
	
	public abstract void setLeftLegPose(float x, float y, float z);
	
	public abstract void setRightLegPose(float x, float y, float z);
	
	public float getRotation() {
		return rotation;
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}
	
	public double getXoff() {
		return xoff;
	}

	public void setXoff(double xoff) {
		this.xoff = xoff;
	}
	
	public double getYoff() {
		return yoff;
	}

	public void setYoff(double yoff) {
		this.yoff = yoff;
	}
	
	public double getZoff() {
		return zoff;
	}

	public void setZoff(double zoff) {
		this.zoff = zoff;
	}
	
}
