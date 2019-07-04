package net.minemora.eggwarscore.reportsystem;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.game.Game;
import net.minemora.eggwarscore.game.GameLobby;
import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.network.NetworkClient;
import net.minemora.reportsystem.QueueAddEvent;
import net.minemora.reportsystem.ReportSystemAPI;
import net.minemora.reportsystem.VisibilityManager;

public class ReportSystemHook {
	
	private static boolean enabled = false;
	
	public static void setup() {
		if(EggWarsCore.getPlugin().getServer().getPluginManager().getPlugin("ReportSystem") == null) {
			return;
		}
		enabled = true;
		
		ReportSystemAPI.setVisibilityManager(new VisibilityManager() {

			@Override
			public void toggleSpy(Player player, boolean enable) {
				GamePlayer gp = GamePlayer.get(player.getName());
				if(gp == null) {
					return;
				}
				if(gp.getGameLobby() == null) {
					return;
				}
				if(enable) {
					player.setGameMode(GameMode.SPECTATOR);
					for(Player lp : gp.getMulticast().getBukkitPlayers()) {
						GamePlayer lgp = GamePlayer.get(lp.getName());
						lp.hidePlayer(player);
						if(!lgp.isDead()) {
							player.showPlayer(lp);
						}
					}
				}
				else {
					player.setGameMode(GameMode.SURVIVAL);
					for(Player lp : gp.getMulticast().getBukkitPlayers()) {
						lp.showPlayer(player);
					}
				}
			}
		});
		
		ReportSystemAPI.setQueueAddEvent(new QueueAddEvent() {

			@Override
			public void onQueueAdd(String playerName, String targetName) {
				Player target = Bukkit.getPlayer(targetName);
				if(target == null) {
					return;
				}
				GamePlayer tgp = GamePlayer.get(targetName);
				if(tgp == null) {
					return;
				}
				if(tgp.getMulticast() instanceof Game) {
					NetworkClient.getRegisteredPlayers().put(playerName, tgp.getGame().getGameLobby());
				}
				else if(tgp.getMulticast() instanceof GameLobby) {
					NetworkClient.getRegisteredPlayers().put(playerName, tgp.getGameLobby());
				}
			}
			
		});
		
		ReportSystemAPI.setProcessQueueOnJoin(false);
	}
	
	public static boolean isEnabled() {
		return enabled;
	}

}
