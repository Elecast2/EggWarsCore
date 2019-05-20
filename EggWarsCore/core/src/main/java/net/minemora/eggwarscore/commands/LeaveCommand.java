package net.minemora.eggwarscore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.utils.ChatUtils;

public class LeaveCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatUtils.format("&cOnly players can use this command"));
			return true;
		}
		Player player = (Player) sender;
		GamePlayer.sendToLobby(player);
		return true;
	}
}
