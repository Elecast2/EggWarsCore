package net.minemora.eggwarscore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.utils.ChatUtils;

public class Tutorial implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			String[] msg = { //TODO LANG
					"&f&l&m================&B&L(&A&lCOMO JUGAR&B&L)&7&l&m================",
					"   &5EggWars &7consiste en un juego donde varios equipos son "
					+ "distribuidos en islas, cada una contiene un huevo el cual "
					+ "debes proteger, ya que si es roto, tu equipo obtendra mortalidad, "
					+ "y al morir todos en tu equipo, perderas. Las islas tambien contienen "
					+ "generadores, que pueden utilizarse para comprar en la tienda. El "
					+ "ultimo equipo en sobrevivir sera el ganador, debe haber un balance "
					+ "entre destruir a los demas equipos y proteger el huevo.",
					"&7&l&m======================&f&l&m======================"
			};
					
			p.sendMessage(ChatUtils.format(msg));
			
			return true;
		}
		return false;
	}
}