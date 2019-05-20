package net.minemora.eggwarscore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.npc.NPCManager;
import net.minemora.eggwarscore.utils.ChatUtils;

public class EggWarsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatUtils.format("&cOnly players can use this command"));
			return true;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("ewc.command")) {
			player.sendMessage(ChatUtils.format(ConfigLang.get().getString("command.ewc.no-perm"))); //TODO on lang
			return true;
		}
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("npc")) {
				if (args.length == 1) {
					player.sendMessage(ChatUtils.format(ConfigLang.get().getStringList("command.ewc.npc.usage"))); //TODO on lang
				} else if (args.length == 2) {
					if(args[1].equalsIgnoreCase("rewards")) {  //TODO mensajes de sucess y demas
						NPCManager.addNPC("rewards", player.getLocation());
                	}
                	else if(args[1].equalsIgnoreCase("quickjoin")) {
                		NPCManager.addNPC("quick-join", player.getLocation());
                	}
                	else if(args[1].equalsIgnoreCase("parkour")) {
                		NPCManager.addNPC("parkour", player.getLocation());
                	}
                	else if(args[1].equalsIgnoreCase("mode")) {
                		//TODO lang mode correct usage
                	}
				} else if (args.length == 3) {
					if(args[1].equalsIgnoreCase("mode")) {
						if(GameManager.getModes().contains(args[2])) {
							NPCManager.addNPC("mode-" + args[2], player.getLocation());
						}
						else {
							//TODO lang list of available modes
						}
	            	}
				}
			}
		}
		return true;
	}
}