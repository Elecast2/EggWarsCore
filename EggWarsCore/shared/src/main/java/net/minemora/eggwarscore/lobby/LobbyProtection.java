package net.minemora.eggwarscore.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.Plugin;

import net.minemora.eggwarscore.listener.EggWarsListener;

public class LobbyProtection extends EggWarsListener {

	public LobbyProtection(Plugin plugin) {
		super(plugin);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().getWorld().equals(Bukkit.getWorlds().get(0))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getWorld().equals(Bukkit.getWorlds().get(0))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (event.getPlayer().getWorld().equals(Bukkit.getWorlds().get(0))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (event.getPlayer().getWorld().equals(Bukkit.getWorlds().get(0))) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity().getWorld().equals(Bukkit.getWorlds().get(0))) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked().getWorld().equals(Bukkit.getWorlds().get(0))) {
			event.setCancelled(true);
		}
	}

	/*
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
	*/
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getPlayer().getWorld().equals(Bukkit.getWorlds().get(0))) {
			if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Material type = event.getClickedBlock().getType();
				if(type == Material.HOPPER 
						|| type == Material.LEVER
						|| type == Material.STONE_BUTTON
						|| type == Material.WOOD_BUTTON
						|| type == Material.FURNACE
						|| type == Material.CHEST
						|| type == Material.TRAP_DOOR
						|| type == Material.TRAPPED_CHEST) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity().getWorld().equals(Bukkit.getWorlds().get(0))) {
			event.setCancelled(true);
			if (event.getEntity() instanceof Player) {
				if (event.getCause().equals(DamageCause.VOID)) {
					event.getEntity().teleport(Lobby.getLobby().getSpawn());
				}
			}
		}
	}
}
