package net.minemora.eggwarscore.rchest;

import org.bukkit.configuration.ConfigurationSection;

import net.minemora.eggwarscore.config.ConfigRewardChest;

public class Reward {
	
	private int id;
	private RewardType type;
	private String value;
	private String title;
	private String subTitle;
	private String message;
	
	public Reward(int id, String path) {
		this.setId(id);
		ConfigurationSection section = ConfigRewardChest.get().getConfigurationSection(path);
		this.value = section.getString("value");
		this.type = RewardType.valueOf(section.getString("type"));
		if(section.contains("custom-title")) {
			this.title = section.getString("custom-title");
		}
		else {
			this.title = RewardChest.getTitle(type, value);
		}
		if(section.contains("custom-sub-title")) {
			this.subTitle = section.getString("custom-sub-title");
		}
		else {
			this.subTitle = RewardChest.getSubTitle(type, value);
		}
		if(section.contains("custom-message")) {
			this.message = section.getString("custom-message");
		}
		else {
			this.message = RewardChest.getMessage(type, value);
		}
	}
	
	public RewardType getType() {
		return type;
	}
	
	public void setType(RewardType type) {
		this.type = type;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSubTitle() {
		return subTitle;
	}
	
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIntValue() {
		return Integer.valueOf(value);
	}
}
