package net.minemora.eggwarscore.team;

import org.bukkit.ChatColor;

import net.minemora.eggwarscore.config.ConfigMain;

public class Team {

	private final int id;
	private final String name;
	private final ChatColor color;

	public Team(int id, String name, ChatColor color) {
		this.id = id;
		this.name = name;
		this.color = color;
	}
	
	public String getFinalName() {
		return getColor() + getName();
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public ChatColor getColor() {
		return color;
	}
	
	public static Team deserealize(int id) {
		Team team = new Team(id, ConfigMain.get().getString("team.teams." + id + ".name"),
				ChatColor.valueOf(ConfigMain.get().getString("team.teams." + id + ".color")));
		return team;
	}
}