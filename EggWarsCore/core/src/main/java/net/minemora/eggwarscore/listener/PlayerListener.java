package net.minemora.eggwarscore.listener;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.arena.ArenaManager;
import net.minemora.eggwarscore.chat.ChatManager;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.extras.kit.Kit;
import net.minemora.eggwarscore.game.Game;
import net.minemora.eggwarscore.game.GameGenerator;
import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.game.Multicast;
import net.minemora.eggwarscore.lobby.Lobby;
import net.minemora.eggwarscore.lobby.LobbyItem;
import net.minemora.eggwarscore.network.NetworkClient;
import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.shared.VaultManager;
import net.minemora.eggwarscore.shop.ShopManager;
import net.minemora.eggwarscore.team.TeamManager;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.Utils;

public class PlayerListener extends EggWarsListener {
	
	public PlayerListener(EggWarsCore plugin) {
		super(plugin);
	}
	
	@EventHandler
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if(!NetworkClient.getRegisteredPlayers().containsKey(event.getName())) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Player is not on the list");
			return;
		}
		if(TeamManager.getMaxPlayers()*TeamManager.getTeams().size() == 
				NetworkClient.getRegisteredPlayers().get(event.getName()).getPlayersCount()) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "This game is full");
			NetworkClient.getRegisteredPlayers().remove(event.getName());
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		for(Player player : Bukkit.getOnlinePlayers()) {
			player.hidePlayer(event.getPlayer());
			event.getPlayer().hidePlayer(player);
		}
		event.setJoinMessage(null);
		new GamePlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (!EggWarsCore.getPlugin().hasArenas()) {
			return;
		}
		event.setQuitMessage(null);
		GamePlayer.get(event.getPlayer().getName()).remove();
	}
	
    @EventHandler
    public void onEntityPickupItem(PlayerPickupItemEvent event) {
    	if(event.getItem().getItemStack().getItemMeta() != null) {
    		if(event.getItem().getItemStack().getItemMeta().getDisplayName() != null) {
		    	if(event.getItem().getItemStack().getItemMeta().getDisplayName().equals("nomerge")) {
		    		GamePlayer gp = GamePlayer.get(event.getPlayer().getName());
		    		for(GameGenerator gm : gp.getGame().getGameArena().getGenerators()) {
		        		if(!event.getItem().equals(gm.getCounterItem())) {
		        			continue;
		        		}
		        		if(gm.getCount() > 10) {
		        			event.getPlayer().getInventory().addItem(
		        					new ItemStack(gm.getArenaGenerator().getGenerator().getMaterial(), (gm.getCount() - 10)));
		        		}
		        		gm.setCounterItem(null);
		        		gm.setCount(0);
		        	}
		    		event.getItem().getItemStack().setItemMeta(null);
		    		return;
		    	}
    		}
    	}
    	GamePlayer gp = GamePlayer.get(event.getPlayer().getName());
    	if(!gp.isLoaded()) {
    		event.setCancelled(true);
    		return;
    	}
    	if(gp.getGame() == null) { //TODO cambiar por otro tipo de check
    		return;
    	}
    	if(event.getItem().getItemStack().getType() == Material.STAINED_CLAY 
    			|| event.getItem().getItemStack().getType() == Material.WOOL
    			|| event.getItem().getItemStack().getType() == Material.STAINED_GLASS) {
    		
    		event.getItem().getItemStack().setDurability(Utils.colorToIdColor(
    				Utils.chatColorToColor(gp.getGameTeam().getTeam().getColor())));
    		return;
		}
    	if(event.getItem().getItemStack().getItemMeta() instanceof LeatherArmorMeta) {
			LeatherArmorMeta meta = (LeatherArmorMeta) event.getItem().getItemStack().getItemMeta();
			meta.setColor(Utils.chatColorToColor(gp.getGameTeam().getTeam().getColor()));
			event.getItem().getItemStack().setItemMeta(meta);
			return;
		}
    	if(event.getItem().getItemStack().getType() == Material.REDSTONE_BLOCK) {
    		event.setCancelled(true);
    		return;
    	}
    }
    
    @EventHandler
	public void onDamage(EntityDamageEvent event) {
    	if(event.isCancelled()) {
    		return;
    	}
    	if(event.getEntity().getType() == EntityType.ITEM_FRAME) { //TODO config prevent break item frames
    		event.setCancelled(true);
    		return;
    	}
    	if(!(event.getEntity() instanceof Player)) {
    		return;
    	}
		Player player = (Player) event.getEntity();
		GamePlayer gp = GamePlayer.get(player.getName());
		if(!gp.isLoaded()) {
    		event.setCancelled(true);
    		return;
    	}
		if(gp.getGame()==null) { //TODO cambiar por otro tipo de check
			return;
		}
		if(gp.isDead()) {
			event.setCancelled(true);
			return;
		}
		if(gp.getGame().isEnding()) {
			event.setCancelled(true);
			return;
		}
		if(event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent dmgByEntEvent = (EntityDamageByEntityEvent) event;
			if(dmgByEntEvent.getDamager() instanceof Player) {
				if(Game.evaluateDamage(event, (Player) event.getEntity(), (Player) dmgByEntEvent.getDamager())) {
					return;
				}
			}
			else if (dmgByEntEvent.getDamager() instanceof Projectile) {
				if(((Projectile)dmgByEntEvent.getDamager()).getShooter() instanceof Player) {
					if(Game.evaluateDamage(event, (Player) event.getEntity(), (Player)((Projectile)dmgByEntEvent.getDamager()).getShooter())) {
						return;
					}
				}
			}
		}
		if(event.getCause().equals(DamageCause.VOID)) {
			if(ConfigMain.get().getBoolean("general.instant-death-on-void")) {
				event.setDamage(player.getHealth());
			}
		}
		if ((player.getHealth() - event.getFinalDamage()) <= 0) {
			event.setCancelled(true);
			player.setLastDamageCause(event);
			if(gp.getGameTeam().isEggDestroyed()) {
				gp.setDead(event.getCause());
			}
			else {
				gp.respawn(event.getCause());
			}
		}
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
    	GamePlayer gp = GamePlayer.get(event.getPlayer().getName());
    	if(!gp.isLoaded()) {
    		event.setCancelled(true);
    		return;
    	}
    	if(gp.getGame() == null) { //TODO cambiar por otro tipo de check quizas gp.getMulticast instanceof Game
    		return;
    	}
    	if(gp.isDead()) {
			return;
		}
    	if(gp.getGame().getPlacedBlocks().contains(event.getBlock())) {
    		gp.getGame().getPlacedBlocks().remove(event.getBlock());
    		if(event.getBlock().getType() == Material.ENDER_CHEST) { //TODO CHECK IF ENABLED ON CONFIG
        		event.setCancelled(true);
        		event.getBlock().setType(Material.AIR);
        		event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation().add(0.5, 0.5, 0.5), 
        				new ItemStack(Material.ENDER_CHEST));
        	}
    	}
    	else if(event.getBlock().getType().equals(ArenaManager.getBlockToDestroy())) {
    		for(int id : gp.getGame().getGameArena().getBlocksToDestroy().keySet()) {
        		if(event.getBlock().equals(gp.getGame().getGameArena().getBlocksToDestroy().get(id).getBlock())) {
        			event.setCancelled(true);
        			gp.getGame().destroyBlock(id, event.getPlayer().getName());
        			break;
        		}
        	}
    	}
    	else {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
    	GamePlayer gp = GamePlayer.get(event.getPlayer().getName());
    	if(!gp.isLoaded()) {
    		event.setCancelled(true);
    		return;
    	}
    	if(gp.getGame() == null) { //TODO cambiar por otro tipo de check
    		return;
    	}
    	Block placed = event.getBlock();
    	for(Location loc : gp.getGame().getGameArena().getSpawnPoints().values()) {
    		if(loc.distanceSquared(placed.getLocation()) < ((TeamManager.getMaxPlayers() >= 4) ? 7 : 5) && placed.getLocation().getBlockY() >= loc.getBlockY()) {
    			event.getPlayer().sendMessage(ChatUtils.format("&cNo puedes contruir cerca de el punto de aparición"));//TODO LANG
    			event.setCancelled(true);
        		return;
    		}
    	}
    	if(placed.getLocation().distanceSquared(gp.getGame().getGameArena().getCenter()) > 26000) { //TODO CONFIG
    		event.getPlayer().sendMessage(ChatUtils.format("&cNo puedes contruir tan lejos"));//TODO LANG
    		event.setCancelled(true);
    		return;
    	}
    	gp.getGame().getPlacedBlocks().add(placed);
    }
    
    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
    	if(event.getBlock().getType() == Material.DRAGON_EGG) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	if(GamePlayer.get(event.getPlayer().getName()) == null) {
    		event.setCancelled(true);
    		return;
    	}
		if(!GamePlayer.get(event.getPlayer().getName()).isLoaded()) {
    		event.setCancelled(true);
    		return;
    	}
    	if(event.hasItem()) {
    		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
    			for(LobbyItem lobbyItem : Lobby.getLobby().getLobbyItems().values()) {
    				if(lobbyItem.getItem().equals(event.getItem())) {
    					lobbyItem.getAction().perform(event.getPlayer());
    					return;
    				}
    			}
    			if(event.getItem().getType() == Material.COMPASS) {  //TODO IF ENABLED ON CONFIG
    				GamePlayer gp = GamePlayer.get(event.getPlayer().getName());
    				if(gp.getMulticast() instanceof Game) {
    					if(gp.getGameTeam() == null) {
    						return;
    					}
    					Player np = Game.getNearestPlayer(event.getPlayer());
    					if(np==null) {
    						return;
    					}
    					GamePlayer ngp = GamePlayer.get(np.getName());
    					if(ngp.getGameTeam() == null) {
    						return;
    					}
    					int distance = (int) event.getPlayer().getLocation().distance(np.getLocation());
    					event.getPlayer().sendMessage(ChatUtils.format("&f[&4&lRastreador&f] " 
    							+ ngp.getGameTeam().getTeam().getFinalName() + " " + np.getName() 
    							+ " &7[&f" + distance + " bloques&7]"));//TODO LANG
    				}
    			}
    		}
    	}
    	if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
    		if(event.getClickedBlock().getState() instanceof Sign) {
    			GamePlayer gp = GamePlayer.get(event.getPlayer().getName());
    			if(gp.getGame() == null) { //TODO cambiar por otro tipo de check quizas gp.getMulticast instanceof Game
    	    		return;
    	    	}
    			if(gp.isDead()) {
    				return;
    			}
    			for(GameGenerator gameGenerator : gp.getGame().getGameArena().getGenerators()) {
    				if(gameGenerator.getLocation().getBlock().equals(event.getClickedBlock())) {
    					gameGenerator.getMenu().open(event.getPlayer());
    				}
    			}
    		}
    		if(event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.TRAPPED_CHEST) {
    			if(GamePlayer.get(event.getPlayer().getName()).isDead()) {
    				event.setCancelled(true);
    				return;
    			}
    		}
    		if(event.getClickedBlock().getType() == Material.ENDER_CHEST) {
    			event.setCancelled(true);
        		GamePlayer gp = GamePlayer.get(event.getPlayer().getName());
            	if(!gp.isLoaded()) {
            		return;
            	}
            	if(gp.getGame() == null) { //TODO cambiar por otro tipo de check
            		return;
            	}
            	if(gp.getGameTeam() == null) { //TODO cambiar por otro tipo de check
            		return;
            	}
            	event.getPlayer().openInventory(gp.getGameTeam().getEnderChest());
            	SharedHandler.getNmsHandler().playEnderChestAction(event.getClickedBlock().getLocation(), true);
            	gp.getGame().getOpenEnderChests().put(event.getPlayer().getName(), event.getClickedBlock().getLocation());
    		}
    	}
    	else if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
    		if(event.getClickedBlock().getType().equals(ArenaManager.getBlockToDestroy())) {
    			GamePlayer gp = GamePlayer.get(event.getPlayer().getName());
    			if(gp.getGame() == null) { //TODO cambiar por otro tipo de check quizas gp.getMulticast instanceof Game
    	    		return;
    	    	}
    			if(gp.isDead()) {
    				return;
    			}
        		for(int id : gp.getGame().getGameArena().getBlocksToDestroy().keySet()) {
            		if(event.getClickedBlock().equals(gp.getGame().getGameArena().getBlocksToDestroy().get(id).getBlock())) {
            			//event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 4, 200));
            			Bukkit.getServer().getPluginManager().callEvent(new BlockBreakEvent(event.getClickedBlock(), event.getPlayer()));
            			break;
            		}
            	}
        	}
    	}
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
    	GamePlayer gp = GamePlayer.get(event.getPlayer().getName());
    	if(gp==null) {
    		return;
    	}
    	if(!gp.isLoaded()) {
    		return;
    	}
    	if(gp.getGame() == null) { //TODO cambiar por otro tipo de check
    		return;
    	}
    	if(gp.getGameTeam() == null) { //TODO cambiar por otro tipo de check
    		return;
    	}
    	if(event.getInventory().equals(gp.getGameTeam().getEnderChest())) {
    		if(gp.getGame().getOpenEnderChests().containsKey(event.getPlayer().getName())) {
        		Location loc = gp.getGame().getOpenEnderChests().get(event.getPlayer().getName());
        		int count = 0;
        		for(Location loc2 : gp.getGame().getOpenEnderChests().values()) {
        			if(loc.equals(loc2)) {
        				count++;
        			}
        		}
        		if(count > 1) {
        			return;
        		}
        		SharedHandler.getNmsHandler().playEnderChestAction(loc, false);
        		gp.getGame().getOpenEnderChests().remove(event.getPlayer().getName());
        	}
    	}
    }
    
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {  //TODO mejorar al añadir rangos y convertir en clases manager
    	event.setCancelled(true);
    	GamePlayer gp = GamePlayer.get(event.getPlayer().getName());
		if(gp == null) {
			event.setCancelled(true);
    		return;
    	}
		if(!gp.isLoaded()) {
    		event.setCancelled(true);
    		return;
    	}
		String format;
		String prefix = "";
		Multicast multicast;
		if(gp.getMulticast() instanceof Game) {
			if(!gp.isDead()) {
				if(event.getMessage().startsWith("@")) {
					multicast = gp.getGame();
					prefix = ConfigMain.get().getString("chat-format.global-prefix");
					event.setMessage(event.getMessage().substring(1));
				}
				else {
					multicast = gp.getGameTeam();
				}
			}
			else {
				multicast = gp.getGame();
			}
		}
		else {
			multicast = gp.getGameLobby();
		}
		
		String message = event.getMessage();
		message = message.replace("\\", "\\\\").replace("$", "\\$");
		if(VaultManager.hasPermission(event.getPlayer(), "ewc.chat.color")) {
			message = ChatUtils.format(message);
			
		}
		if(gp.getGameTeam()!=null) {
			if(gp.isDead()) {
				format = ChatUtils.format(ConfigMain.get().getString("chat-format.spectator-team"));
			}
			else {
				format = ChatUtils.format(prefix + ConfigMain.get().getString("chat-format.team"));
			}
			format = ChatManager.replace(event.getPlayer(), format, new String[] {"team-color","team-name"});
			multicast.broadcastMessage(ChatUtils.format(ChatManager.replaceBasic(event.getPlayer(), format))
					.replaceAll("%message%", message));
		}
		else {
			if(gp.isDead()) {
				format = ChatUtils.format(ConfigMain.get().getString("chat-format.spectator-no-team"));
			}
			else {
				format = ChatUtils.format(ConfigMain.get().getString("chat-format.no-team"));
			}
			multicast.broadcastMessage(ChatUtils.format(ChatManager.replaceBasic(event.getPlayer(), format))
					.replaceAll("%message%", message));
		}
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if(GamePlayer.get(event.getPlayer().getName()) == null) {
			event.setCancelled(true);
    		return;
    	}
		if(!GamePlayer.get(event.getPlayer().getName()).isLoaded()) {
    		event.setCancelled(true);
    		return;
    	}
    	if(event.getRightClicked().getType() == ShopManager.getEntityType()) {
    		event.setCancelled(true);
    		GamePlayer gp = GamePlayer.get(event.getPlayer().getName());
    		
    		if(gp.getGame() == null) { //TODO cambiar por otro tipo de check
        		return;
        	}
    		if(gp.isDead()) {
				return;
			}
    		gp.getGame().getShop().getShopMenu().open(event.getPlayer());
    	}
    	if(event.getRightClicked().getType() == EntityType.ITEM_FRAME) { //TODO config prevent rotate item frames
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
    	if(!ConfigMain.get().getBoolean("general.allow-crafting")) {
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
    	if(event.getCause().equals(TeleportCause.ENDER_PEARL)) {
    		event.setCancelled(true);
    		event.getPlayer().teleport(event.getTo(), TeleportCause.PLUGIN);
    		event.getPlayer().setFallDistance(0);
    	}
    }
    
    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
    	if(event.getEntity().getShooter() instanceof Player) {
    		Player shooter = (Player)event.getEntity().getShooter();
    		GamePlayer gp = GamePlayer.get(shooter.getName());
    		if(gp.getTrail()!=null) {
    			gp.getTrail().play(event.getEntity());
    		}
    	}
    }
    
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
    	if(Kit.isBinded(event.getItemDrop().getItemStack())) {
    		event.getPlayer().sendMessage(ChatUtils.format("&cNo puedes lanzar un item atado a ti")); //TODO LANG
    		event.setCancelled(true);
    		return;
    	}
    	if(event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
    		event.setCancelled(true);
    		return;
    	}
    }
    
    @EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
    	if((event.getCurrentItem() != null) && (event.getClickedInventory() != null) && (event.getClickedInventory().getHolder() != null)) {
			if(!event.getClickedInventory().getHolder().equals(event.getWhoClicked())) {
				if(Kit.isBinded(event.getCurrentItem())) {
	    			event.getWhoClicked().sendMessage(ChatUtils.format("&cNo puedes guardar un item atado a ti")); //TODO LANG
	        		event.setCancelled(true);
	        	}
			}
			else {
				if(event.isShiftClick()) {
					if(Kit.isBinded(event.getCurrentItem())) {
    	    			event.getWhoClicked().sendMessage(ChatUtils.format("&cNo puedes guardar un item atado a ti")); //TODO LANG
    	        		event.setCancelled(true);
    	        	}
				}
			}
    	}
    	if(event.getInventory().getType() == InventoryType.ANVIL) { //TODO FROM CONFIG
    		event.setCancelled(true);
    	}
    	if(event.getInventory().getType() == InventoryType.ENCHANTING) { //TODO FROM CONFIG
    		event.setCancelled(true);
    	}
    }
    
    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
    	event.setAmount(0);
    }
}