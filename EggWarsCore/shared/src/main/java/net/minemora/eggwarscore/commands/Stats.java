package net.minemora.eggwarscore.commands;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.utils.ChatUtils;

public class Stats implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(args.length==0) {
				PlayerStats ps = PlayerStats.get(p.getName());
				showStats(p, ps);
			}
			else {
				if(Bukkit.getPlayer(args[0])  != null) {
					PlayerStats ps = PlayerStats.get(args[0]);
					if(ps == null) {
						return true;
					}
					showStats(p, ps);
				}
				else {
					p.sendMessage(ChatUtils.format("&c" + args[0] + " no se encuentra conectado")); //TODO LANG
				}
			}			
			return true;
		}
		return true;
	}
	
	private void showStats(Player p, PlayerStats ps) {  //TODO LANG
		String[] msg = {
				"&6o--------[&e&lEstadísticas de &b&l" + ps.getPlayerName() + "&6]--------o",
				"",
				"     &fAsesinatos: &a" + ps.getKills(),
				"     &fMuertes: &a" + ps.getDeaths(),
				"     &fHuevos Rotos: &a" + ps.getDestroyedEggs(),
				"     &fVictorias: &a"+ ps.getWins(),
				"     &fK/D: &a" + getKDR(ps.getKills(), ps.getDeaths()),
				"",
				"&6o-----------------------------------------------o"
		};	
		p.sendMessage(ChatUtils.format(msg));
	}
	
	private double getKDR(int kills, int deaths) {
		if(deaths==0) {
			return kills;
		}
		return (double)round(((double)kills/deaths),2);
	}
	
	private double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
}