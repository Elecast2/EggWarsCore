package net.minemora.eggwarscore.arena;

import java.util.List;

import org.bukkit.block.Sign;

import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.generator.Generator;
import net.minemora.eggwarscore.utils.ChatUtils;

public class ArenaGenerator {

	private Coordinates coordinates;
	private Generator generator;
	private int startLevel;

	public ArenaGenerator(Generator generator, Coordinates coordinates, int startLevel) {
		this.generator = generator;
		this.coordinates = coordinates;
		this.startLevel = startLevel;
	}
	
	public static void formatSign(Sign sign, ArenaGenerator arenaGenerator) {
		List<String> lines = ConfigLang.get().getStringList("generator.sign." + ((arenaGenerator.getStartLevel() == 0) ? "broken" : "active"));
		for(int i = 0; i < 4; i++) {
			sign.setLine(i, ChatUtils.format(lines.get(i).replaceAll("%name%", arenaGenerator.getGenerator().getName())
					.replaceAll("%level%", String.valueOf(arenaGenerator.getStartLevel()))));
		}
		sign.update();
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}

	public Generator getGenerator() {
		return generator;
	}

	public void setGenerator(Generator generator) {
		this.generator = generator;
	}

	public int getStartLevel() {
		return startLevel;
	}

	public void setStartLevel(int startLevel) {
		this.startLevel = startLevel;
	}
}