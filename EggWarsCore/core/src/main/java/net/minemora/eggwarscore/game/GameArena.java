package net.minemora.eggwarscore.game;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.World.Environment;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.arena.Arena;
import net.minemora.eggwarscore.arena.ArenaManager;
import net.minemora.eggwarscore.holographicdisplays.HolographicDisplaysHook;
import net.minemora.eggwarscore.lobby.Lobby;

public class GameArena {

	private Arena arena;
	private Game game;
	private Location center;
	private Location specSpawn;
	private String loadedWorldName;
	private Map<Integer, Location> spawnPoints = new HashMap<>();
	private Map<Integer, Location> blocksToDestroy = new HashMap<>();
	private Set<GameGenerator> generators = new HashSet<>();
	private Set<Location> shops = new HashSet<>();
	private String votedTime;

	public GameArena(Game game, String worldName, String votedTime) {
		this.game = game;
		this.votedTime = votedTime;
		arena = ArenaManager.getArena(worldName);
		World world = loadWorld();
		center = arena.getCenter().toLocation(world);
		specSpawn = arena.getSpecSpawn().toLocation(world);
		arena.getSpawnPoints().keySet().stream().forEach(i -> spawnPoints.put(i, arena.getSpawnPoints().get(i).toLocation(world)));
		arena.getBlocksToDestroy().keySet().stream().forEach(i -> blocksToDestroy.put(i, arena.getBlocksToDestroy().get(i).toLocation(world)));
		arena.getArenaGenerators().stream().forEach(ag -> generators.add(new GameGenerator(ag, this)));
		arena.getShops().stream().forEach(s -> shops.add(s.toLocation(world)));
		HolographicDisplaysHook.loadShopHolograms(this);
	}

	private World loadWorld() {
		loadedWorldName = arena.getWorldName() + "_" + game.getGameId();
		File folder = new File(EggWarsCore.getPlugin().getDataFolder(), "Arenas/" + arena.getWorldName());
		try {
			new File(loadedWorldName).mkdir();
			FileUtils.copyDirectory(folder, new File(loadedWorldName));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		WorldCreator worldCreator = new WorldCreator(loadedWorldName);
		worldCreator.environment(Environment.NORMAL);
		worldCreator.generateStructures(false);
		worldCreator.generator(ArenaManager.getChunkGenerator());
		World world = worldCreator.createWorld();
		if(votedTime.equals("morning")) {
			world.setTime(0);
		}
		else if(votedTime.equals("night")) {
			world.setTime(18000);
		}
		else {
			world.setTime(6000);
		}
		world.setDifficulty(Difficulty.NORMAL);
		world.setSpawnFlags(true, true);
		world.setPVP(true);
		world.setStorm(false);
		world.setThundering(false);
		world.setWeatherDuration(Integer.MAX_VALUE);
		world.setKeepSpawnInMemory(false);
		world.setTicksPerAnimalSpawns(1);
		world.setTicksPerMonsterSpawns(1);
		world.setAutoSave(false);
		world.setGameRuleValue("doMobSpawning", "false");
		world.setGameRuleValue("mobGriefing", "false");
		world.setGameRuleValue("doFireTick", "false");
		world.setGameRuleValue("showDeathMessages", "false");
		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("randomTickSpeed", "0");
		return world;
	}
	
	public void unloadWorld() {
		World world = getWorld();
		if(world.getPlayers() != null) {//TODO posiblemente esta no sea la razon del null y sea el world
			for(Player player : world.getPlayers()) {
				player.teleport(Lobby.getLobby().getSpawn(), TeleportCause.END_PORTAL);
				System.out.println("ESTO NO DEBERIA EJECUTARSE");
			}
		}
		Bukkit.unloadWorld(world, true);
		File worldFolder = new File(loadedWorldName);
		try {
			FileUtils.deleteDirectory(worldFolder);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public World getWorld() {
		return Bukkit.getWorld(loadedWorldName);
	}

	public Location getCenter() {
		return center;
	}

	public void setCenter(Location center) {
		this.center = center;
	}

	public Location getSpecSpawn() {
		return specSpawn;
	}

	public void setSpecSpawn(Location specSpawn) {
		this.specSpawn = specSpawn;
	}

	public Map<Integer, Location> getSpawnPoints() {
		return spawnPoints;
	}

	public void setSpawnPoints(Map<Integer, Location> spawnPoints) {
		this.spawnPoints = spawnPoints;
	}

	public Map<Integer, Location> getBlocksToDestroy() {
		return blocksToDestroy;
	}

	public void setBlocksToDestroy(Map<Integer, Location> blocksToDestroy) {
		this.blocksToDestroy = blocksToDestroy;
	}

	public Set<GameGenerator> getGenerators() {
		return generators;
	}

	public void setGenerators(Set<GameGenerator> generators) {
		this.generators = generators;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Arena getArena() {
		return arena;
	}

	public void setArena(Arena arena) {
		this.arena = arena;
	}

	public String getLoadedWorldName() {
		return loadedWorldName;
	}

	public void setLoadedWorldName(String loadedWorldName) {
		this.loadedWorldName = loadedWorldName;
	}

	public Set<Location> getShops() {
		return shops;
	}

	public void setShops(Set<Location> shops) {
		this.shops = shops;
	}
}
