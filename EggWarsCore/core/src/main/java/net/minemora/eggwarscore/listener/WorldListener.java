package net.minemora.eggwarscore.listener;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.world.WorldInitEvent;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.game.Game;
import net.minemora.eggwarscore.generator.Generator;
import net.minemora.eggwarscore.generator.GeneratorManager;

public class WorldListener extends EggWarsListener {

	public WorldListener(EggWarsCore plugin) {
		super(plugin);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onWorldInit(WorldInitEvent e) {
		e.getWorld().setKeepSpawnInMemory(false);
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if(event.blockList()==null) {
			return;
		}
		if(event.blockList().size()==0) {
			return;
		}
		Game game = Game.getByWorldName(event.getEntity().getWorld().getName());
		if(game == null) {
			return;
		}
		Iterator<Block> iter = event.blockList().iterator();
		while (iter.hasNext()) {
		    Block block = iter.next();
		    if(!game.getPlacedBlocks().contains(block)) {
		        iter.remove();
		    }
		}
		game.getPlacedBlocks().removeAll(event.blockList());
	}
	
	@EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) { //TODO FROM CONFIG
    	if(event.getSpawnReason().equals(SpawnReason.EGG)) {
    		event.setCancelled(true);
    	}
    }
	
	@EventHandler
	public void onItemDespawn(ItemDespawnEvent event) {
		for(Generator generator : GeneratorManager.getGenerators().values()) {
			if(generator.getMaterial() == event.getEntity().getItemStack().getType()) {
				event.setCancelled(true); //TODO FROM CONFIG
			}
		}
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if (event.getWorld().equals(Bukkit.getWorlds().get(0))) {
			if(event.getWorld().isThundering()) {
				event.getWorld().setThundering(false);
			}
			if(event.getWorld().hasStorm()) {
				event.getWorld().setStorm(false);
			}
			event.setCancelled(true);
		}
	}
}