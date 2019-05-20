package net.minemora.eggwarscore.generator;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;

import net.minemora.eggwarscore.config.ConfigGenerators;

public class Generator {

	private String id;
	private Material material;
	private String name;
	private Map<Integer, Integer> generationTime = new HashMap<>();
	private Map<Integer, Integer> levelPrice = new HashMap<>();
	private Map<Integer, Material> levelMaterial = new HashMap<>();
	private Map<Integer, Integer> exp = new HashMap<>();

	public Generator(String id, String name, Material material) {
		this.id = id;
		this.name = name;
		this.material = material;
	}

	public Map<Integer, Integer> getGenerationTime() {
		return generationTime;
	}

	public Material getMaterial() {
		return material;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public Map<Integer, Integer> getLevelPrice() {
		return levelPrice;
	}

	public Map<Integer, Material> getLevelMaterial() {
		return levelMaterial;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public int getExp(int level) {
		return exp.get(level);
	}
	
	public Map<Integer, Integer> getExp() {
		return exp;
	}

	public static Generator deserealize(String generatorId) {
		Generator generator = new Generator(generatorId,
				ConfigGenerators.get().getString("generators." + generatorId + ".name"),
				Material.valueOf(ConfigGenerators.get().getString("generators." + generatorId + ".material")));
		for (String levelString : ConfigGenerators.get()
				.getConfigurationSection("generators." + generatorId + ".levels").getValues(false).keySet()) {
			int level = Integer.parseInt(levelString);
			generator.getGenerationTime().put(level,
					ConfigGenerators.get().getInt("generators." + generatorId + ".levels." + level + ".ticks"));
			generator.getLevelMaterial().put(level, Material.valueOf(ConfigGenerators.get()
					.getString("generators." + generatorId + ".levels." + level + ".price.material")));
			generator.getLevelPrice().put(level,
					ConfigGenerators.get().getInt("generators." + generatorId + ".levels." + level + ".price.amount"));
			generator.getExp().put(level, ConfigGenerators.get().getInt("generators." + generatorId + ".levels." + level + ".exp"));
		}
		return generator;
	}
}