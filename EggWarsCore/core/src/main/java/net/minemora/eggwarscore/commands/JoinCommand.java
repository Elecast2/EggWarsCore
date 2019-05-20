package net.minemora.eggwarscore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.utils.ChatUtils;

public class JoinCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatUtils.format("&cOnly players can use this command"));
			return true;
		}
		Player player = (Player) sender;
		GamePlayer gp = GamePlayer.get(player.getName());
		if(gp==null) {
			return true;
		}
		if(gp.getGame()==null) {
			return true;
		}
		if(!gp.isDead()) {
			if(!gp.getGame().isEnding()) {
				return true;
			}
		}
		if(gp.isJoining()) {
			return true;
		}
		gp.setJoining(true);
		GamePlayer.sendToQuickGame(player);		
		return true;
	}
}
