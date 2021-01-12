package net.minemora.eggwarscore.commands;

import java.util.Random;
import java.util.SortedSet;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.game.TournamentManager;
import net.minemora.eggwarscore.lobby.Lobby;
import net.minemora.eggwarscore.network.GamesConnection;
import net.minemora.eggwarscore.network.PacketTournamentInfo;
import net.minemora.eggwarscore.npc.NPCManager;
import net.minemora.eggwarscore.player.TournamentTeam;
import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.utils.ChatUtils;

public class EggWarsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatUtils.format("&cOnly players can use this command"));
			return true;
		}
		Player player = (Player) sender;
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("npc")) {
				if (!player.hasPermission("ewc.command.admin")) {
					player.sendMessage(ChatUtils.format(ConfigLang.get().getString("command.ewc.no-perm"))); //TODO on lang
					return true;
				}
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
			else if (args[0].equalsIgnoreCase("reloadteams")) {
				if(!GameManager.isTournamentMode()) {
					return true;
				}
				if (!player.hasPermission("ewc.tournamentstaff")) {
					player.sendMessage(ChatUtils.format(ConfigLang.get().getString("command.ewc.no-perm"))); //TODO on lang
					return true;
				}
				TournamentManager.getInstance().reloadTeams();
				for(SortedSet<GamesConnection> gconnSet : GameManager.getGames().values()) {
					for(GamesConnection gconn : gconnSet) {
						new PacketTournamentInfo(gconn.getWriter()).send();
					}
				}
				player.sendMessage(ChatUtils.format("Equipos han sido reconfigurados"));
				
			}
			else if (args[0].equalsIgnoreCase("addplayer")) {
				if(!GameManager.isTournamentMode()) {
					return true;
				}
				if (!player.hasPermission("ewc.tournamentstaff")) {
					player.sendMessage(ChatUtils.format(ConfigLang.get().getString("command.ewc.no-perm"))); //TODO on lang
					return true;
				}
				if(args.length == 3) {
					TournamentTeam team = TournamentManager.getInstance().getTeams().get(args[1]);
					if(team == null) {
						return true;
					}
					team.addPlayer(args[2]);
				}
				TournamentManager.getInstance().reloadTeams();
				for(SortedSet<GamesConnection> gconnSet : GameManager.getGames().values()) {
					for(GamesConnection gconn : gconnSet) {
						new PacketTournamentInfo(gconn.getWriter()).send();
					}
				}
				player.sendMessage(ChatUtils.format("Equipos han sido reconfigurados"));
				
			}
			else if (args[0].equalsIgnoreCase("removeplayer")) {
				if(!GameManager.isTournamentMode()) {
					return true;
				}
				if (!player.hasPermission("ewc.tournamentstaff")) {
					player.sendMessage(ChatUtils.format(ConfigLang.get().getString("command.ewc.no-perm"))); //TODO on lang
					return true;
				}
				if(args.length == 3) {
					TournamentTeam team = TournamentManager.getInstance().getTeams().get(args[1]);
					if(team == null) {
						return true;
					}
					team.removePlayer(args[2]);
				}
				TournamentManager.getInstance().reloadTeams();
				for(SortedSet<GamesConnection> gconnSet : GameManager.getGames().values()) {
					for(GamesConnection gconn : gconnSet) {
						new PacketTournamentInfo(gconn.getWriter()).send();
					}
				}
				player.sendMessage(ChatUtils.format("Equipos han sido reconfigurados"));
				
			}
			else if (args[0].equalsIgnoreCase("intro")) {
				if(!GameManager.isTournamentMode()) {
					return true;
				}
				if (!player.hasPermission("ewc.tournamentstaff")) {
					player.sendMessage(ChatUtils.format(ConfigLang.get().getString("command.ewc.no-perm"))); //TODO on lang
					return true;
				}
				World world = Bukkit.getWorld("world");
				Location firework1 = new Location(world, 11,100,-20);
				Location firework2 = new Location(world, 14,99,-13);
				Location firework3 = new Location(world, 9,97,14);
				Location firework4 = new Location(world, 7,98,21);
				Random ran = new Random();
				world.setTime(18000);
				world.playSound(Lobby.getLobby().getSpawn(), Sound.ENDERDRAGON_DEATH, 3, 2);
				for(Player lp : Bukkit.getOnlinePlayers()) {
					lp.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 10, 1));
					lp.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 1));
					lp.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 10));
				}
				new BukkitRunnable() {
					@Override
					public void run() {
						for(Player player: Bukkit.getOnlinePlayers()) {
							SharedHandler.getNmsHandler().sendTitleToPlayer(player, 20, 60, 20, ChatUtils.format("&E&K|&f&lBienvenido a &5&LEGGWARS &6&LKINGS&E&K|"), "");
						}
					}
				}.runTaskLater(SharedHandler.getPlugin(), 90);
				
				new BukkitRunnable() {
					@Override
					public void run() {
						for(Player player: Bukkit.getOnlinePlayers()) {
							SharedHandler.getNmsHandler().sendTitleToPlayer(player, 0, 120, 50, ChatUtils.format("&E&K|&f&lBienvenido a &5&LEGGWARS &6&LKINGS&E&K|"),
									ChatUtils.format("&c¿Podrás llevarte la &ecorona&c?"));
							world.playSound(Lobby.getLobby().getSpawn(), Sound.ENDERDRAGON_GROWL, 3, 1);
						}
					}
				}.runTaskLater(SharedHandler.getPlugin(), 130);
				
				
				new BukkitRunnable() {
					int count = 0;
					@Override
					public void run() {
						if(ran.nextBoolean()) {
							randomFirework(firework1, ran);
						}
						if(ran.nextBoolean()) {
							randomFirework(firework2, ran);
						}
						if(ran.nextBoolean()) {
							randomFirework(firework3, ran);
						}
						if(ran.nextBoolean()) {
							randomFirework(firework4, ran);
						}
						count ++;
						if(count == 80) {
							cancel();
							world.setTime(6000);
						}
					}
				}.runTaskTimer(SharedHandler.getPlugin(), 90, 5);
			}
		}
		return true;
	}
	
	  private static void randomFirework(Location loc, Random random) {
		    Firework firework1 = (Firework)loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		    FireworkMeta fireworkMeta = firework1.getFireworkMeta();
		    FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(getColor(random.nextInt(17) + 1)).withFade(getColor(random.nextInt(17) + 1)).with(FireworkEffect.Type.values()[random.nextInt((FireworkEffect.Type.values()).length)]).trail(random.nextBoolean()).build();
		    fireworkMeta.addEffect(effect);
		    fireworkMeta.setPower(random.nextInt(2) + 1);
		    firework1.setFireworkMeta(fireworkMeta);
	}
		  
		  private static Color getColor(int i) {
		    switch (i) {
		      case 1:
		        return Color.AQUA;
		      case 2:
		        return Color.BLACK;
		      case 3:
		        return Color.BLUE;
		      case 4:
		        return Color.FUCHSIA;
		      case 5:
		        return Color.GRAY;
		      case 6:
		        return Color.GREEN;
		      case 7:
		        return Color.LIME;
		      case 8:
		        return Color.MAROON;
		      case 9:
		        return Color.NAVY;
		      case 10:
		        return Color.OLIVE;
		      case 11:
		        return Color.ORANGE;
		      case 12:
		        return Color.PURPLE;
		      case 13:
		        return Color.RED;
		      case 14:
		        return Color.SILVER;
		      case 15:
		        return Color.TEAL;
		      case 16:
		        return Color.WHITE;
		      case 17:
		        return Color.YELLOW;
		    } 
		    return null;
		  }
}