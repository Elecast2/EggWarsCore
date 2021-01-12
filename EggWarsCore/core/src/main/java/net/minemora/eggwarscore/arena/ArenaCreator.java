package net.minemora.eggwarscore.arena;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.World.Environment;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.listener.EggWarsListener;
import net.minemora.eggwarscore.config.ConfigArenas;
import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.generator.GeneratorManager;
import net.minemora.eggwarscore.team.TeamManager;
import net.minemora.eggwarscore.utils.ChatUtils;

public class ArenaCreator extends EggWarsListener {

	private static Map<String, ArenaCreator> arenaCreators = new HashMap<>();

	private Arena arena;
	private String playerName;
	private String editWorldName;

	private int blockToDestroy = 0;

	public ArenaCreator(EggWarsCore plugin, String worldName, String playerName) {
		super(plugin);
		this.playerName = playerName;
		this.editWorldName = worldName + "_edit";
		if(ArenaManager.getArenas().containsKey(worldName)) {
			arena = ArenaManager.getArenas().get(worldName);
		} else {
			arena = new Arena(worldName);
		}
		World world = loadWorld();
		Bukkit.getPlayer(playerName).setGameMode(GameMode.CREATIVE);
		Bukkit.getPlayer(playerName).teleport(world.getSpawnLocation(), TeleportCause.END_PORTAL);
		Bukkit.getPlayer(playerName).getInventory().clear();
		arenaCreators.put(playerName, this);
	}

	private World loadWorld() {
		File folder = new File(EggWarsCore.getPlugin().getDataFolder(), "Arenas/" + arena.getWorldName());
		try {
			new File(editWorldName).mkdir();
			FileUtils.copyDirectory(folder, new File(editWorldName));
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		WorldCreator worldCreator = new WorldCreator(editWorldName);
		worldCreator.environment(Environment.NORMAL);
		worldCreator.generateStructures(false);
		worldCreator.generator(ArenaManager.getChunkGenerator());
		World world = worldCreator.createWorld();
		world.setTime(6000);
		world.setDifficulty(Difficulty.PEACEFUL);
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
	
	public void saveWorld() {
		World tounload = Bukkit.getWorld(editWorldName);
		Bukkit.getPlayer(playerName).teleport(Bukkit.getWorlds().get(0).getSpawnLocation(), TeleportCause.END_PORTAL);
		
		tounload.save();
		Bukkit.unloadWorld(tounload, true);
		
		File folder = new File(EggWarsCore.getPlugin().getDataFolder(), "Arenas/" + arena.getWorldName());
		try {
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		File uid = new File(editWorldName, "uid.dat");
		uid.delete();
		File world = new File(editWorldName);
		
		try {
			folder.mkdir();
			FileUtils.copyDirectory(world, folder);
			FileUtils.deleteDirectory(world);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Bukkit.getPlayer(playerName).sendMessage(ChatUtils.format(ConfigLang.get().getString("command.ewc.successful.save")
				.replaceAll("%name%", getArena().getWorldName())));
	}

	public void saveToConfig() {
		ConfigurationSection arenas = null;
		if (!EggWarsCore.getPlugin().hasArenas()) {
			arenas = ConfigArenas.get().createSection("arenas");
		} else {
			arenas = ConfigArenas.get().getConfigurationSection("arenas");
		}
		if (!arenas.contains(arena.getWorldName())) {
			arenas.createSection(arena.getWorldName());
		}
		ConfigurationSection arenaSection = arenas.getConfigurationSection(arena.getWorldName());
		arenaSection.set("name", arena.getArenaName());
		arenaSection.set("radius", arena.getRadius());
		if (!arenaSection.contains("center")) {
			arenaSection.createSection("center");
		}
		if (!arenaSection.contains("spec-spawn")) {
			arenaSection.createSection("spec-spawn");
		}
		if (!arenaSection.contains("points")) {
			arenaSection.createSection("points");
		}
		if (!arenaSection.contains("generators")) {
			arenaSection.createSection("generators");
		}
		if (!arenaSection.contains("shops")) {
			arenaSection.createSection("shops");
		}
		arenaSection.set("center.x", arena.getCenter().getX());
		arenaSection.set("center.y", arena.getCenter().getY());
		arenaSection.set("center.z", arena.getCenter().getZ());

		arenaSection.set("spec-spawn.x", arena.getSpecSpawn().getX());
		arenaSection.set("spec-spawn.y", arena.getSpecSpawn().getY());
		arenaSection.set("spec-spawn.z", arena.getSpecSpawn().getZ());
		arenaSection.set("spec-spawn.pitch", arena.getSpecSpawn().getPitch());
		arenaSection.set("spec-spawn.yaw", arena.getSpecSpawn().getYaw());

		ConfigurationSection pointsSection = arenaSection.getConfigurationSection("points");
		for (int i = 1; i <= TeamManager.getTeams().size(); i++) {
			if (!pointsSection.contains(String.valueOf(i))) {
				pointsSection.createSection(String.valueOf(i));
			}
			ConfigurationSection pointSection = pointsSection.getConfigurationSection(String.valueOf(i));
			if (!pointSection.contains("spawn-point")) {
				pointSection.createSection("spawn-point");
			}
			if (!pointSection.contains("block-to-destroy")) {
				pointSection.createSection("block-to-destroy");
			}
			ConfigurationSection spawnSection = pointSection.getConfigurationSection("spawn-point");
			spawnSection.set("x", arena.getSpawnPoints().get(i).getX());
			spawnSection.set("y", arena.getSpawnPoints().get(i).getY());
			spawnSection.set("z", arena.getSpawnPoints().get(i).getZ());
			spawnSection.set("pitch", arena.getSpawnPoints().get(i).getPitch());
			spawnSection.set("yaw", arena.getSpawnPoints().get(i).getYaw());

			ConfigurationSection blockSection = pointSection.getConfigurationSection("block-to-destroy");
			blockSection.set("x", arena.getBlocksToDestroy().get(i).getX());
			blockSection.set("y", arena.getBlocksToDestroy().get(i).getY());
			blockSection.set("z", arena.getBlocksToDestroy().get(i).getZ());

		}

		ConfigurationSection generatorsSection = arenaSection.getConfigurationSection("generators");
		int count = 1;
		for (ArenaGenerator arenaGenerator : arena.getArenaGenerators()) {
			if (!generatorsSection.contains(String.valueOf(count))) {
				generatorsSection.createSection(String.valueOf(count));
			}
			ConfigurationSection generatorSection = generatorsSection.getConfigurationSection(String.valueOf(count));
			generatorSection.set("x", arenaGenerator.getCoordinates().getX());
			generatorSection.set("y", arenaGenerator.getCoordinates().getY());
			generatorSection.set("z", arenaGenerator.getCoordinates().getZ());
			generatorSection.set("type", arenaGenerator.getGenerator().getId());
			generatorSection.set("level", arenaGenerator.getStartLevel());
			count++;
		}
		if(generatorsSection.getValues(false).size() > arena.getArenaGenerators().size()) {
			for (int i = arena.getArenaGenerators().size() + 1; i <= generatorsSection.getValues(false).size(); i++) {
				generatorsSection.set(String.valueOf(i), null);
			}
		}
		
		ConfigurationSection shopsSection = arenaSection.getConfigurationSection("shops");
		count = 1;
		for (Coordinates shopCoords : arena.getShops()) {
			if (!shopsSection.contains(String.valueOf(count))) {
				shopsSection.createSection(String.valueOf(count));
			}
			ConfigurationSection shopSection = shopsSection.getConfigurationSection(String.valueOf(count));
			shopSection.set("x", shopCoords.getX());
			shopSection.set("y", shopCoords.getY());
			shopSection.set("z", shopCoords.getZ());
			shopSection.set("pitch", shopCoords.getPitch());
			shopSection.set("yaw", shopCoords.getYaw());
			count++;
		}

		ConfigArenas.getInstance().save();
		ArenaManager.loadArenas();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!event.getPlayer().getName().equals(playerName)) {
			return;
		}
		Bukkit.getPlayer(playerName).setGameMode(GameMode.CREATIVE);
		Bukkit.getPlayer(playerName).teleport(Bukkit.getWorld(editWorldName).getSpawnLocation(), TeleportCause.END_PORTAL);
		Bukkit.getPlayer(playerName).getInventory().clear();
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.getPlayer().getName().equals(playerName)) {
			return;
		}
		if (blockToDestroy != 0) {
			event.setCancelled(true);
			arena.getBlocksToDestroy().put(blockToDestroy, new Coordinates(event.getBlock().getLocation()));
			event.getPlayer()
					.sendMessage(ChatUtils.format(ConfigLang.get().getString("command.ewc.successful.setblock")
							.replaceAll("%location%", ChatUtils.formatLocation(event.getBlock().getLocation()))
							.replaceAll("%point%", String.valueOf(blockToDestroy))));
			blockToDestroy = 0;
		}
		if(event.getBlock().getState() instanceof Sign) {
			Iterator<ArenaGenerator> iter = arena.getArenaGenerators().iterator();
			while (iter.hasNext()) {
				ArenaGenerator ag = iter.next();
				if(ag.getCoordinates().toLocation(Bukkit.getWorld(editWorldName)).getBlock().equals(event.getBlock())) { //TODO npe aqui
			        iter.remove();
			        event.getPlayer().sendMessage(ChatUtils.format(ConfigLang.get().getString("arenas-setup.generator.remove")));
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.getPlayer().getName().equals(playerName)) {
			return;
		}
		if(event.getPlayer().getItemInHand() == null) {
			return;
		}
		if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if(event.getPlayer().getItemInHand().getType() == Material.SIGN) {
			ItemStack item = event.getPlayer().getItemInHand();
			if(!item.hasItemMeta()) {
				return;		
			}
			if(item.getItemMeta()==null) {
				return;
			}
			ItemMeta meta = item.getItemMeta();
			if(meta.getLore()==null) {
				return;
			}
			if(meta.getLore().size()==0) {
				return;
			}
			if(meta.getLore().get(0).equals("generator")) {
				event.setCancelled(true);
				Block block = event.getClickedBlock().getRelative(event.getBlockFace());
				byte data;
				if(event.getBlockFace() == BlockFace.NORTH) {
					data = 2;
				}
				else if(event.getBlockFace() == BlockFace.SOUTH) {
					data = 3;
				}
				else if(event.getBlockFace() == BlockFace.WEST) {
					data = 4;
				}
				else if(event.getBlockFace() == BlockFace.EAST) {
					data = 5;
				}
				else {
					return;
				}
				block.setType(Material.WALL_SIGN);
				block.setData(data);
				String type = meta.getLore().get(1);
				int level = Integer.parseInt(meta.getLore().get(2));
				ArenaGenerator arenaGenerator = new ArenaGenerator(GeneratorManager.getGenerator(type),
						new Coordinates(block.getLocation()), level);
				arena.getArenaGenerators().add(arenaGenerator);
				event.getPlayer().sendMessage(ChatUtils.format(ConfigLang.get().getString("arenas-setup.generator.place")));
				ArenaGenerator.formatSign((Sign)block.getState(), arenaGenerator);
			}
		}
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if (!event.getPlayer().getName().equals(playerName)) {
			return;
		}
		if(!event.getLine(0).equalsIgnoreCase("[Generator]")) {
			return;
		}
		if(!GeneratorManager.getGenerators().keySet().contains(event.getLine(1))) {
			event.getBlock().setType(Material.AIR);
			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.SIGN));
			event.getPlayer()
			.sendMessage(ChatUtils.format(ConfigLang.get().getString("arenas-setup.generator.no-exists")
					.replaceAll("%name%", event.getLine(1))
					.replaceAll("%generators%", String.join(", ", GeneratorManager.getGenerators().keySet()))));
			return;
		}
		int level;
		try {
			level = Integer.parseInt(event.getLine(2));
		} catch (NumberFormatException e) {
			event.getBlock().setType(Material.AIR);
			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.SIGN));
			event.getPlayer().sendMessage(ChatUtils.format(ConfigLang.get().getString("arenas-setup.generator.no-number")));
			return;
		}
		if(level < 0 || level > GeneratorManager.getGenerator(event.getLine(1)).getLevelPrice().size()) {
			event.getBlock().setType(Material.AIR);
			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), new ItemStack(Material.SIGN));
			event.getPlayer().sendMessage(ChatUtils.format(ConfigLang.get().getString("arenas-setup.generator.out-of-range")
					.replaceAll("%max%", String.valueOf(GeneratorManager.getGenerator(event.getLine(1)).getLevelPrice().size()))));
			return;
		}
		event.setCancelled(true);
		ArenaGenerator arenaGenerator = new ArenaGenerator(GeneratorManager.getGenerator(event.getLine(1)),
				new Coordinates(event.getBlock().getLocation()), level);
		arena.getArenaGenerators().add(arenaGenerator);
		event.getPlayer().sendMessage(ChatUtils.format(ConfigLang.get().getString("arenas-setup.generator.place")));
		ArenaGenerator.formatSign((Sign)event.getBlock().getState(), arenaGenerator);
	}

	public Arena getArena() {
		return arena;
	}

	public void setArena(Arena arena) {
		this.arena = arena;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public static ArenaCreator get(String playerName) {
		return arenaCreators.get(playerName);
	}

	public static Map<String, ArenaCreator> getArenaCreators() {
		return arenaCreators;
	}

	public int getBlockToDestroy() {
		return blockToDestroy;
	}

	public void setBlockToDestroy(int blockToDestroy) {
		this.blockToDestroy = blockToDestroy;
	}
}