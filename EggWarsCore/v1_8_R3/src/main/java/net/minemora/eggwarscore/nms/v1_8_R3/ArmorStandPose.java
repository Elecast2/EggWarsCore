package net.minemora.eggwarscore.nms.v1_8_R3;

import net.minecraft.server.v1_8_R3.Vector3f;
import net.minemora.eggwarscore.nms.AArmorStandPose;

public class ArmorStandPose extends AArmorStandPose {
	
	private Vector3f headPose = new Vector3f(0.0F, 0.0F, 0.0F);
	private Vector3f bodyPose = new Vector3f(0.0F, 0.0F, 0.0F);
	private Vector3f leftArmPose = new Vector3f(0.0F, 0.0F, 0.0F);
	private Vector3f rightArmPose = new Vector3f(0.0F, 0.0F, 0.0F);
	private Vector3f leftLegPose = new Vector3f(0.0F, 0.0F, 0.0F);
	private Vector3f rightLegPose = new Vector3f(0.0F, 0.0F, 0.0F);
	
	public ArmorStandPose() {}
	
	public Vector3f getHeadPose() {
		return headPose;
	}
	@Override
	public void setHeadPose(float x, float y, float z) {
		this.headPose = new Vector3f(x,y,z);
	}
	public Vector3f getBodyPose() {
		return bodyPose;
	}
	@Override
	public void setBodyPose(float x, float y, float z) {
		this.bodyPose = new Vector3f(x,y,z);
	}
	public Vector3f getLeftArmPose() {
		return leftArmPose;
	}
	@Override
	public void setLeftArmPose(float x, float y, float z) {
		this.leftArmPose = new Vector3f(x,y,z);
	}
	public Vector3f getRightArmPose() {
		return rightArmPose;
	}
	@Override
	public void setRightArmPose(float x, float y, float z) {
		this.rightArmPose = new Vector3f(x,y,z);
	}
	public Vector3f getLeftLegPose() {
		return leftLegPose;
	}
	@Override
	public void setLeftLegPose(float x, float y, float z) {
		this.leftLegPose = new Vector3f(x,y,z);
	}
	public Vector3f getRightLegPose() {
		return rightLegPose;
	}
	@Override
	public void setRightLegPose(float x, float y, float z) {
		this.rightLegPose = new Vector3f(x,y,z);
	}
}
