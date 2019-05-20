package net.minemora.eggwarscore.arena;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.config.ConfigArenas;
import net.minemora.eggwarscore.config.ConfigMain;

public final class ArenaManager {

	private static Map<String, Arena> arenas = new HashMap<>();
	private static Material blockToDestroy;

	private static ChunkGenerator chunkGenerator;

	private ArenaManager() {
	}

	public static void setup() {
		chunkGenerator = new ChunkGenerator() {

			@Override
			public List<BlockPopulator> getDefaultPopulators(World world) {
				return Arrays.asList(new BlockPopulator[0]);
			}

			@Override
			public boolean canSpawn(World world, int x, int z) {
				return true;
			}

			@Override
			public byte[] generate(World world, Random random, int x, int z) {
				return new byte[32768];
			}

			@Override
			public Location getFixedSpawnLocation(World world, Random random) {
				return new Location(world, 0.0D, 64.0D, 0.0D);
			}
		};
		setBlockToDestroy(Material.valueOf(ConfigMain.get().getString("game.block-to-destroy")));
	}

	public static Set<String> getValidWorlds() {
		Set<String> validWorlds = new HashSet<>();
		File[] subdirs = new File(EggWarsCore.getPlugin().getDataFolder(), "Arenas").listFiles(File::isDirectory);
		if (subdirs.length > 0) {
			for (int i = 0; i < subdirs.length; i++) {
				boolean isWorld = new File(subdirs[i], "level.dat").exists();
				if (isWorld) {
					validWorlds.add(subdirs[i].getName());
				}
			}
		}
		return validWorlds;
	}

	public static Set<String> getArenaList() {
		Set<String> arenaList = new HashSet<>();
		if (ConfigArenas.get().get("arenas") != null) {
			for (String arena : ConfigArenas.get().getConfigurationSection("arenas").getValues(false).keySet()) {
				arenaList.add(arena);
			}
		}
		return arenaList;
	}

	public static void loadArenas() {
		if (getArenaList().isEmpty()) {
			return;
		}
		arenas.clear();
		for (String worldName : getArenaList()) {
			arenas.put(worldName, Arena.deserealize(worldName));
		}
	}

	public static Map<String, Arena> getArenas() {
		return arenas;
	}

	public static Arena getArena(String worldName) {
		return arenas.get(worldName);
	}

	public static ChunkGenerator getChunkGenerator() {
		return chunkGenerator;
	}

	public static Material getBlockToDestroy() {
		return blockToDestroy;
	}

	public static void setBlockToDestroy(Material blockToDestroy) {
		ArenaManager.blockToDestroy = blockToDestroy;
	}
}