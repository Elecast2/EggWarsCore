package net.minemora.eggwarscore.game;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.block.Sign;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.arena.ArenaGenerator;
import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.generator.GeneratorManager;
import net.minemora.eggwarscore.menu.GeneratorMenu;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.InstantFirework;
import net.minemora.eggwarscore.utils.Utils;

public class GameGenerator {

	private ArenaGenerator arenaGenerator;
	private Location location;
	private Location dropLocation;
	private GameArena gameArena;
	private int level;
	private BukkitTask currentTask = null;
	private GeneratorMenu menu;
	private Item counterItem = null;
	private int count = 0;

	public GameGenerator(ArenaGenerator arenaGenerator, GameArena gameArena) {
		this.arenaGenerator = arenaGenerator;
		this.gameArena = gameArena;
		this.location = arenaGenerator.getCoordinates().toLocation(Bukkit.getWorld(gameArena.getLoadedWorldName()));
		this.dropLocation = new Location(Bukkit.getWorld(gameArena.getLoadedWorldName()), 
				location.getBlockX(), location.getBlockY(), location.getBlockZ()).add(0.5,0.6,0.5);
		setLevel(arenaGenerator.getStartLevel());
		this.menu = new GeneratorMenu(this);
	}

	private void dropItem() {
		if((count <= 10) || counterItem == null || !counterItem.isValid()) { //TODO configurable
			ItemStack item = new ItemStack(arenaGenerator.getGenerator().getMaterial());
			if(GeneratorManager.isNoMerge()) {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("nomerge");
				meta.setLore(Arrays.asList(new String[]{String.valueOf(ThreadLocalRandom.current().nextInt(GeneratorManager.getMergeGroups()))}));
			   	item.setItemMeta(meta);
			}
			if(counterItem == null || !counterItem.isValid()) {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName("nomerge");
				meta.setLore(Arrays.asList(new String[]{"counter"}));
			   	item.setItemMeta(meta);
			}
			Item drop = location.getWorld().dropItem(dropLocation, item);
			drop.setPickupDelay(0);
			drop.setVelocity(new Vector(ThreadLocalRandom.current()
					.nextDouble(-0.05,0.05),0.1,ThreadLocalRandom.current().nextDouble(-0.05,0.05)));
			if(counterItem == null || !counterItem.isValid()) {
				counterItem = drop;
			}
		}
		count++;
	}

	private void setLevel(int level) {
		this.level = level;
		if (currentTask != null) {
			currentTask.cancel();
		}
		if(level==0) {
			return;
		}
		currentTask = new BukkitRunnable() {
			@Override
			public void run() {
				dropItem();
				if (gameArena.getGame().isEnding()) {
					HandlerList.unregisterAll(getMenu());
					cancel();
					return;
				}
			}
		}.runTaskTimer(EggWarsCore.getPlugin(), 0L, arenaGenerator.getGenerator().getGenerationTime().get(level));
	}

	private void upgrade() {
		if (getLevel() >= arenaGenerator.getGenerator().getLevelPrice().size()) {
			return;
		}
		setLevel(getLevel() + 1);
		updateSign();
		menu.update();

		FireworkEffect fireworkEffect = FireworkEffect.builder().with(Type.BALL).withColor(Color.GREEN).withColor(Color.YELLOW).build();
		new InstantFirework(fireworkEffect, dropLocation);
	
		Bukkit.getWorld(gameArena.getLoadedWorldName()).playSound(dropLocation, Sound.LEVEL_UP, 1, 1.5f);
	}
	
	private void updateSign() {
		Sign sign = (Sign) location.getBlock().getState();
		List<String> lines = ConfigLang.get().getStringList("generator.sign.active");
		for(int i = 0; i < 4; i++) {
			sign.setLine(i, ChatUtils.format(lines.get(i).replaceAll("%name%", arenaGenerator.getGenerator().getName())
					.replaceAll("%level%", String.valueOf(level))));
		}
		sign.update();
	}
	
	public void tryToUpgrade(String playerName) {
		if(level >= getArenaGenerator().getGenerator().getLevelMaterial().size()) {
			return;
		}
		Player player = Bukkit.getPlayer(playerName);
		if(Utils.hasItems(player, getArenaGenerator().getGenerator().getLevelMaterial().get(level+1), 
				getArenaGenerator().getGenerator().getLevelPrice().get(level+1))) {
			Utils.removeItems(player, getArenaGenerator().getGenerator().getLevelMaterial().get(level+1), 
					getArenaGenerator().getGenerator().getLevelPrice().get(level+1));
			GamePlayer.get(player.getName()).addExp(getArenaGenerator().getGenerator().getExp(level+1)); 
			upgrade();
		}
		else {
			Bukkit.getPlayer(playerName).sendMessage(ChatUtils.format(ConfigLang.get().getString("generator.no-material")));
		}
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public GameArena getGameArena() {
		return gameArena;
	}

	public void setGameArena(GameArena gameArena) {
		this.gameArena = gameArena;
	}

	public ArenaGenerator getArenaGenerator() {
		return arenaGenerator;
	}

	public void setArenaGenerator(ArenaGenerator arenaGenerator) {
		this.arenaGenerator = arenaGenerator;
	}

	public int getLevel() {
		return level;
	}

	public Location getDropLocation() {
		return dropLocation;
	}

	public void setDropLocation(Location dropLocation) {
		this.dropLocation = dropLocation;
	}

	public GeneratorMenu getMenu() {
		return menu;
	}

	public void setMenu(GeneratorMenu menu) {
		this.menu = menu;
	}

	public Item getCounterItem() {
		return counterItem;
	}

	public void setCounterItem(Item counterItem) {
		this.counterItem = counterItem;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}