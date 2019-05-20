package net.minemora.eggwarscore.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;

import net.minemora.eggwarscore.config.ConfigGenerators;
import net.minemora.eggwarscore.config.ConfigMain;

public final class GeneratorManager {

	private static Map<String, Generator> generators = new HashMap<>();
	private static Map<Material, String> nameSingular = new HashMap<>();
	private static Map<Material, String> namePlural = new HashMap<>();
	private static int mergeGroups;
	private static boolean noMerge;

	private GeneratorManager() {
	}

	public static void loadGenerators() {
		for (String generatorId : getGeneratorList()) {
			generators.put(generatorId, Generator.deserealize(generatorId));
		}
		for(String key : ConfigGenerators.get().getConfigurationSection("material-names").getValues(false).keySet()) {
			nameSingular.put(Material.valueOf(ConfigGenerators.get().getString("material-names." + key + ".material")), 
					ConfigGenerators.get().getString("material-names." + key + ".name"));
			namePlural.put(Material.valueOf(ConfigGenerators.get().getString("material-names." + key + ".material")), 
					ConfigGenerators.get().getString("material-names." + key + ".name-plural"));
		}
		mergeGroups = ConfigMain.get().getInt("generator.merge-groups");
		noMerge = ConfigMain.get().getBoolean("generator.no-merge");
	}

	public static Set<String> getGeneratorList() {
		return ConfigGenerators.get().getConfigurationSection("generators").getValues(false).keySet();
	}

	public static Map<String, Generator> getGenerators() {
		return generators;
	}

	public static Generator getGenerator(String generatorId) {
		return generators.get(generatorId);
	}

	public static Map<Material, String> getNameSingular() {
		return nameSingular;
	}

	public static Map<Material, String> getNamePlural() {
		return namePlural;
	}

	public static int getMergeGroups() {
		return mergeGroups;
	}

	public static boolean isNoMerge() {
		return noMerge;
	}
}
