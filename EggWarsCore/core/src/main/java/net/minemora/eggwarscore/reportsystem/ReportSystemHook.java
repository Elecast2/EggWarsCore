package net.minemora.eggwarscore.reportsystem;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.game.GamePlayer;
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
				if(gp.getGame() == null) {
					return;
				}
				if(enable) {
					player.setGameMode(GameMode.SPECTATOR);
					for(Player lp : gp.getGame().getBukkitPlayers()) {
						lp.hidePlayer(player);
					}
				}
				else {
					player.setGameMode(GameMode.ADVENTURE);
					for(Player lp : gp.getGame().getBukkitPlayers()) {
						GamePlayer lgp = GamePlayer.get(lp.getName());
						if(!lgp.isDead()) {
							lp.showPlayer(player);
						}
					}
				}
			}
		});
		
		ReportSystemAPI.setProcessQueueOnJoin(false);
	}
	
	public static boolean isEnabled() {
		return enabled;
	}

}
