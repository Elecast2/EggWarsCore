package net.minemora.eggwarscore.arena;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.config.ConfigArenas;
import net.minemora.eggwarscore.generator.Generator;
import net.minemora.eggwarscore.generator.GeneratorManager;

public class Arena {

	private String arenaName;
	private String worldName;
	private Coordinates center;
	private Coordinates specSpawn;
	private Map<Integer, Coordinates> spawnPoints = new HashMap<>();
	private Map<Integer, Coordinates> blocksToDestroy = new HashMap<>();
	private Set<ArenaGenerator> arenaGenerators = new HashSet<>();
	private Set<Coordinates> shops = new HashSet<>();
	private int radius;

	public Arena(String worldName) {
		this.worldName = worldName;
	}

	public String getArenaName() {
		return arenaName;
	}

	public void setArenaName(String arenaName) {
		this.arenaName = arenaName;
	}

	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public Coordinates getCenter() {
		return center;
	}

	public void setCenter(Coordinates center) {
		this.center = center;
	}

	public Coordinates getSpecSpawn() {
		return specSpawn;
	}

	public void setSpecSpawn(Coordinates specSpawn) {
		this.specSpawn = specSpawn;
	}

	public Map<Integer, Coordinates> getSpawnPoints() {
		return spawnPoints;
	}

	public void setSpawnPoints(Map<Integer, Coordinates> spawnPoints) {
		this.spawnPoints = spawnPoints;
	}

	public Map<Integer, Coordinates> getBlocksToDestroy() {
		return blocksToDestroy;
	}

	public void setBlocksToDestroy(Map<Integer, Coordinates> blocksToDestroy) {
		this.blocksToDestroy = blocksToDestroy;
	}

	public Set<ArenaGenerator> getArenaGenerators() {
		return arenaGenerators;
	}

	public void setArenaGenerators(Set<ArenaGenerator> arenaGenerators) {
		this.arenaGenerators = arenaGenerators;
	}
	
	public Set<Coordinates> getShops() {
		return shops;
	}

	public void setShops(Set<Coordinates> shops) {
		this.shops = shops;
	}

	public static Arena deserealize(String worldName) {
		Arena arena = new Arena(worldName);
		arena.setArenaName(ConfigArenas.get().getString("arenas." + worldName + ".name"));
		arena.setRadius(ConfigArenas.get().getInt("arenas." + worldName + ".radius"));
		arena.setCenter(new Coordinates(ConfigArenas.get().getDouble("arenas." + worldName + ".center.x"),
				ConfigArenas.get().getDouble("arenas." + worldName + ".center.y"),
				ConfigArenas.get().getDouble("arenas." + worldName + ".center.z")));
		arena.setSpecSpawn(new Coordinates(ConfigArenas.get().getDouble("arenas." + worldName + ".spec-spawn.x"),
				ConfigArenas.get().getDouble("arenas." + worldName + ".spec-spawn.y"),
				ConfigArenas.get().getDouble("arenas." + worldName + ".spec-spawn.z"),
				(float) ConfigArenas.get().getDouble("arenas." + worldName + ".spec-spawn.pitch"),
				(float) ConfigArenas.get().getDouble("arenas." + worldName + ".spec-spawn.yaw")));
		if (ConfigArenas.get().getConfigurationSection("arenas." + worldName + ".generators") != null) {
			for (String id : ConfigArenas.get().getConfigurationSection("arenas." + worldName + ".generators")
					.getValues(false).keySet()) {
				String generatorId = ConfigArenas.get()
						.getString("arenas." + worldName + ".generators." + id + ".type");
				Generator generator = GeneratorManager.getGenerator(generatorId);
				int startLevel = ConfigArenas.get().getInt("arenas." + worldName + ".generators." + id + ".level");
				Coordinates coordinates = new Coordinates(
						ConfigArenas.get().getDouble("arenas." + worldName + ".generators." + id + ".x"),
						ConfigArenas.get().getDouble("arenas." + worldName + ".generators." + id + ".y"),
						ConfigArenas.get().getDouble("arenas." + worldName + ".generators." + id + ".z"));
				ArenaGenerator arenaGenerator = new ArenaGenerator(generator, coordinates, startLevel);
				arena.getArenaGenerators().add(arenaGenerator);
			}
		}
		else {
			EggWarsCore.getPlugin().getLogger().warning("The arena " + worldName + " doesn't have generators!");
		}
		if (ConfigArenas.get().getConfigurationSection("arenas." + worldName + ".points") != null) {
			for (String id : ConfigArenas.get().getConfigurationSection("arenas." + worldName + ".points")
					.getValues(false).keySet()) {
				int teamId = Integer.parseInt(id);
				Coordinates spawnPoint = new Coordinates(
						ConfigArenas.get().getDouble("arenas." + worldName + ".points." + id + ".spawn-point.x"),
						ConfigArenas.get().getDouble("arenas." + worldName + ".points." + id + ".spawn-point.y"),
						ConfigArenas.get().getDouble("arenas." + worldName + ".points." + id + ".spawn-point.z"),
						(float) ConfigArenas.get()
								.getDouble("arenas." + worldName + ".points." + id + ".spawn-point.pitch"),
						(float) ConfigArenas.get()
								.getDouble("arenas." + worldName + ".points." + id + ".spawn-point.yaw"));
				Coordinates blockToDestroy = new Coordinates(
						ConfigArenas.get().getDouble("arenas." + worldName + ".points." + id + ".block-to-destroy.x"),
						ConfigArenas.get().getDouble("arenas." + worldName + ".points." + id + ".block-to-destroy.y"),
						ConfigArenas.get().getDouble("arenas." + worldName + ".points." + id + ".block-to-destroy.z"));
				arena.getSpawnPoints().put(teamId, spawnPoint);
				arena.getBlocksToDestroy().put(teamId, blockToDestroy);
			}
		}
		else {
			EggWarsCore.getPlugin().getLogger().warning("The arena " + worldName + " doesn't have spawnpoints!");
		}
		if (ConfigArenas.get().getConfigurationSection("arenas." + worldName + ".shops") != null) {
			for (String id : ConfigArenas.get().getConfigurationSection("arenas." + worldName + ".shops")
					.getValues(false).keySet()) {
				Coordinates shopLoc = new Coordinates(
						ConfigArenas.get().getDouble("arenas." + worldName + ".shops." + id + ".x"),
						ConfigArenas.get().getDouble("arenas." + worldName + ".shops." + id + ".y"),
						ConfigArenas.get().getDouble("arenas." + worldName + ".shops." + id + ".z"),
						(float) ConfigArenas.get()
								.getDouble("arenas." + worldName + ".shops." + id + ".pitch"),
						(float) ConfigArenas.get()
								.getDouble("arenas." + worldName + ".shops." + id + ".yaw"));
				arena.getShops().add(shopLoc);
			}
		}
		else {
			EggWarsCore.getPlugin().getLogger().warning("The arena " + worldName + " doesn't have shops!");
		}

		return arena;
	}	
}