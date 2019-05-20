package net.minemora.eggwarscore.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.game.GameLobby;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.game.GameTeam;


public class Debug implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			return true;
		}
		System.out.println("----------- SERVER INFO -----------");
		System.out.println(Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + " players");
		System.out.println("----------- RAM INFO -----------");
		System.out.println("RAM = " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1048576 +
			" MB / " + Runtime.getRuntime().maxMemory()/1048576 + " MB");
		System.out.println("----------- GAMES INFO -----------");
		for(GameLobby globby : GameManager.getGames().values()) {
			System.out.println("Game Id: " + globby.getId());
			System.out.println("Is in game? " + (globby.getGame() != null));
			if(globby.getGame() != null) {
				System.out.println("   --=-- Game info --=-- ");
				System.out.println("   Map Name: " + globby.getGame().getGameArena().getArena().getArenaName());
				System.out.println("   Teams:");
				for(GameTeam team : globby.getGame().getGameTeams().values()) {
					System.out.println("     Id: " + team.getTeam().getId());
					System.out.println("     Name: " + team.getTeam().getName());
					System.out.println("     Players:");
					for(String playerName : team.getPlayers()) {
						if(Bukkit.getPlayer(playerName) == null) {
							System.out.println("        Nick: " + playerName + ", BUKKIT NULL, GP null? " 
									+ (GamePlayer.get(playerName) == null));
							continue;
						}
						if(GamePlayer.get(playerName) == null) {
							System.out.println("        Nick: " + playerName + ", GP NULL, BK null? " 
									+ (Bukkit.getPlayer(playerName) == null));
							continue;
						}
						System.out.println("        Nick: " + playerName + ", Dead: " + GamePlayer.get(playerName).isDead() 
								+ ", world: " + Bukkit.getPlayer(playerName).getWorld().getName());
					}
				}
			}
		}
		return true;
	}
}
