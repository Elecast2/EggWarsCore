package net.minemora.eggwarscore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.utils.ChatUtils;

public class Rules implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			
			String[] msg = { //TODO LANG
					"&f&l&m=============&E&L(&c&lREGLAS DEL JUEGO&E&L)&7&l&m=============",
					"   &f>> &6Prohibido el uso de hacks o clientes modificados que",
					"        &6otorgen ventajas sobre los demas.",
					"",
					"   &f>> &6Prohibido aprovecharse de algún bug o error.",
					"",
					"   &f>> &6No se permite el team-kill en modo Dúo y Escuadrón.",
					"",
					"   &7Para reportar: &bfacebook.com/groups/MinemoraReportes/",
					"&7&l&m======================&f&l&m======================"
			};
					
			p.sendMessage(ChatUtils.format(msg));
			
			return true;
		}
		return false;
	}
}