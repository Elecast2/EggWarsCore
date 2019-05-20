package net.minemora.eggwarscore.protocollib;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.game.Game;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.npc.NPC;
import net.minemora.eggwarscore.npc.NPCManager;
import net.minemora.eggwarscore.parkour.Parkour;
import net.minemora.eggwarscore.player.LobbyPlayer;
import net.minemora.eggwarscore.utils.ChatUtils;

public final class ProtocolLibHook {
	
	private static ProtocolManager protocolManager;
	
	private ProtocolLibHook() {}

	public static void setup() {
		if (EggWarsCoreLobby.getPlugin().getServer().getPluginManager().getPlugin("ProtocolLib") == null) {
			return;
		}
		protocolManager = ProtocolLibrary.getProtocolManager();
		
		protocolManager.addPacketListener(new PacketAdapter(EggWarsCoreLobby.getPlugin(), ListenerPriority.NORMAL, 
				PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                	if (event.getPacket().getEntityUseActions().getValues().get(0) == EntityUseAction.INTERACT_AT) {
                		PacketContainer packet = event.getPacket();
                        int id = packet.getIntegers().read(0);
                        if(NPCManager.getNPCs().containsKey(id-1000)) {
                        	Player player = event.getPlayer();
                        	LobbyPlayer lp = LobbyPlayer.get(player.getName());
                        	if(lp == null) {
                        		return;
                        	}
                        	NPC npc = NPCManager.getNPCs().get(id-1000);
                        	if(npc.getPath().equals("rewards")) {
                        		new BukkitRunnable() {
                    				@Override
                    				public void run() {
                    					lp.getDailyRewardsMenu().open(player);
                    				}
                    			}.runTaskLater(EggWarsCoreLobby.getPlugin(), 1);
                        	}
                        	else if(npc.getPath().equals("quick-join")) {
                        		Game game = GameManager.getQuickGame(GameManager.getMode(lp.getMode()));
                        		if(game == null) {
                        			player.sendMessage(ChatUtils.format("&cLo sentimos... No se encuentran partidas disponibles. "
                        					+ "&6Intenta mas tarde"));
                        			return;
                        		}
                        		GameManager.attemptToSendPlayer(player, game);
                        	}
                        	else if(npc.getPath().equals("parkour")) {
                        		Parkour.getInstance().timeTrial(player);
                        	}
                        	else if(npc.getPath().startsWith("mode-")) {
                        		String mode = npc.getPath().substring(5);
                        		if(GameManager.getGamesMenus().containsKey(mode)) {
                        			new BukkitRunnable() {
                        				@Override
                        				public void run() {
                        					GameManager.getGamesMenus().get(mode).open(player);
                        				}
                        			}.runTaskLater(EggWarsCoreLobby.getPlugin(), 1);
                        		}
                        	}
                        }
  					}
                }
            }
        });
		
	}

	public static ProtocolManager getProtocolManager() {
		return protocolManager;
	}

}
