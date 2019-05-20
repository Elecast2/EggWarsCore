package net.minemora.eggwarscore.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.chat.ChatManager;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.lobby.Lobby;
import net.minemora.eggwarscore.lobby.LobbyItem;
import net.minemora.eggwarscore.player.LobbyPlayer;
import net.minemora.eggwarscore.rchest.RewardChest;
import net.minemora.eggwarscore.shared.VaultManager;
import net.minemora.eggwarscore.utils.ChatUtils;

public class PlayerListener extends EggWarsListener {
	
	public PlayerListener(EggWarsCoreLobby plugin) {
		super(plugin);
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		
		//TODO TEMPORAL
		
		//if(VaultManager.getPlayerGroup(event.getPlayer()).equals("Usuario")) {
		//	event.getPlayer().kickPlayer("¡No te encuentras en la lista para entrar!");
		//	return;
		//}
		
		//------------
		
		
		new LobbyPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		if(!PlayerStats.getPlayersStats().containsKey(event.getPlayer().getName())){
			return;
		}
		LobbyPlayer.get(event.getPlayer().getName()).remove();
	}
	
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
    	if(LobbyPlayer.get(event.getPlayer().getName()) == null) {
    		event.setCancelled(true);
    		return;
    	}
		if(!LobbyPlayer.get(event.getPlayer().getName()).isLoaded()) {
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
    		}
    	}
    	if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
    		if(event.getClickedBlock().getType().equals(Material.ENDER_CHEST)) {
    			event.setCancelled(true);
    			RewardChest.getInstance().getMenu().open(event.getPlayer());
    			return;
    		}
    		else if(event.getClickedBlock().getType().equals(Material.SKULL)) { 
    			if(event.getPlayer().getWorld().getBlockAt(-8, 69, 2).equals(event.getClickedBlock())) { //TODO FROM CONFIG
    				event.setCancelled(true);
        			LobbyPlayer.get(event.getPlayer().getName()).getQuickGameMenu().open(event.getPlayer());
        			return;
    			}
    			
    		}
    	}
    }
	
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) { //TODO color perm
    	event.setCancelled(true);
    	LobbyPlayer lp = LobbyPlayer.get(event.getPlayer().getName());
		if(lp == null) {
    		return;
    	}
		if(!lp.isLoaded()) {
    		return;
    	}
		String format = ConfigMain.get().getString("chat-format.format");
		String message = event.getMessage();
		message = message.replace("\\", "\\\\").replace("$", "\\$");
		if(VaultManager.hasPermission(event.getPlayer(), "ewc.chat.color")) {
			message = ChatUtils.format(message);
		}
		format = ChatUtils.format(ChatManager.replaceBasic(event.getPlayer(), format)).replaceAll("%message%", message);
		Bukkit.broadcastMessage(format);
    }
    
    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
    	event.setAmount(0);
    }
}