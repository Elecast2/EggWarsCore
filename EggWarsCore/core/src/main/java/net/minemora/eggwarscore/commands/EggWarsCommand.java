package net.minemora.eggwarscore.commands;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.md_5.bungee.api.chat.TextComponent;
import net.minemora.eggwarscore.arena.ArenaCreator;
import net.minemora.eggwarscore.arena.ArenaManager;
import net.minemora.eggwarscore.arena.Coordinates;
import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.generator.GeneratorManager;
import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.team.TeamManager;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.Utils;

public class EggWarsCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatUtils.format("&cOnly players can use this command"));
			return true;
		}
		Player player = (Player) sender;
		if (!player.hasPermission("ewc.command")) {
			player.sendMessage(ChatUtils.format(ConfigLang.get().getString("command.ewc.no-perm")));
			return true;
		}
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("create")) {
				if (args.length == 1) {
					player.sendMessage(ChatUtils.format(ConfigLang.get().getStringList("command.ewc.create.up")));
					Set<String> validWorlds = ArenaManager.getValidWorlds();
					if (validWorlds.isEmpty()) {
						player.sendMessage(
								ChatUtils.format(ConfigLang.get().getStringList("command.ewc.create.no-worlds")));
					} else {
						if (EggWarsCore.getPlugin().hasArenas()) {
							player.sendMessage(
									ChatUtils.format(ConfigLang.get().getStringList("command.ewc.create.mid")));
						} else {
							player.sendMessage(
									ChatUtils.format(ConfigLang.get().getStringList("command.ewc.create.no-arenas")));
						}
						TextComponent[] tcomps = new TextComponent[validWorlds.size()];
						int count = 0;
						for (String worldName : validWorlds) {
							TextComponent tcomp;
							if (ArenaManager.getArenaList().contains(worldName)) {
								tcomp = ChatUtils.jsonText("&a&l" + worldName + " ", "/ewc edit " + worldName,
										"Click to edit!");
							} else {
								tcomp = ChatUtils.jsonText("&c&l" + worldName + " ", "/ewc create " + worldName,
										"Click to configure!");
							}
							tcomps[count] = tcomp;
							count++;
						}
						player.spigot().sendMessage(tcomps);
					}
					player.sendMessage(ChatUtils.format(ConfigLang.get().getStringList("command.ewc.create.down")));
				} else if (args.length == 2) {
					if (!ArenaManager.getValidWorlds().contains(args[1])) {
						player.sendMessage(ChatUtils.format(ConfigLang.get()
								.getString("command.ewc.create.world-not-found").replaceAll("%world-name%", args[1])));
						return true;
					} else {
						new ArenaCreator(EggWarsCore.getPlugin(), args[1], player.getName());
						player.sendMessage(ChatUtils.format(ConfigLang.get().getStringList("command.ewc.edit.editing")
								.stream()
								.map(s -> s.replaceAll("%max-teams%", String.valueOf(TeamManager.getTeams().size()))
										.replaceAll("%world-name%", args[1]))
								.collect(Collectors.toList())));
					}
				}
			} else if (args[0].equalsIgnoreCase("edit")) {
				if (args.length == 2) {
					if (!ArenaManager.getValidWorlds().contains(args[1])) {
						player.sendMessage(ChatUtils.format(ConfigLang.get()
								.getString("command.ewc.create.world-not-found").replaceAll("%world-name%", args[1])));
						return true;
					} else {
						new ArenaCreator(EggWarsCore.getPlugin(), args[1], player.getName());
						player.sendMessage(ChatUtils.format(ConfigLang.get().getStringList("command.ewc.edit.editing")
								.stream()
								.map(s -> s.replaceAll("%max-teams%", String.valueOf(TeamManager.getTeams().size()))
										.replaceAll("%world-name%", args[1]))
								.collect(Collectors.toList())));
					}
				}
			} else if (args[0].equalsIgnoreCase("startgame")) {
				GamePlayer.get(player.getName()).getGameLobby().startGame();
				return true;
			} else {
				if (ArenaCreator.getArenaCreators().containsKey(player.getName())) {
					ArenaCreator arenaCreator = ArenaCreator.get(player.getName());
					if (args[0].equalsIgnoreCase("setname")) {
						if (args.length == 1) {
							player.sendMessage(ChatUtils.format(ConfigLang.get().getString("command.ewc.correct-usage")
									.replaceAll("%command%", "/ewc setname <name>")));
							return true;
						} else if (args.length > 1) {
							String name = args[1];
							if (args.length > 2) {
								for (int i = 2; i < args.length; i++) {
									name = name + " " + args[i];
								}
							}
							arenaCreator.getArena().setArenaName(name);
							player.sendMessage(ChatUtils.format(ConfigLang.get()
									.getString("command.ewc.successful.setname").replaceAll("%name%", name)));
						}
					} else if (args[0].equalsIgnoreCase("setcenter")) {
						arenaCreator.getArena().setCenter(new Coordinates(player.getLocation()));
						player.sendMessage(
								ChatUtils.format(ConfigLang.get().getString("command.ewc.successful.setcenter")
										.replaceAll("%location%", ChatUtils.formatLocation(player.getLocation()))));
					} else if (args[0].equalsIgnoreCase("setradius")) {
						if (args.length == 1) {
							player.sendMessage(ChatUtils.format(ConfigLang.get().getString("command.ewc.correct-usage")
									.replaceAll("%command%", "/ewc setradius <number>")));
							return true;
						} else if (args.length == 2) {
							int radius;
							try {
								radius = Integer.parseInt(args[1]);
							} catch (NumberFormatException e) {
								player.sendMessage(ChatUtils.format("&4" + args[1] + " &cis not a number"));
								return true;
							}
							arenaCreator.getArena().setRadius(radius);
							player.sendMessage(
									ChatUtils.format(ConfigLang.get().getString("command.ewc.successful.setradius")
											.replaceAll("%radius%", String.valueOf(radius))));
						}
					} else if (args[0].equalsIgnoreCase("setspectator")) {
						arenaCreator.getArena().setSpecSpawn(new Coordinates(player.getLocation()));
						player.sendMessage(
								ChatUtils.format(ConfigLang.get().getString("command.ewc.successful.setspectator")
										.replaceAll("%location%", ChatUtils.formatLocation(player.getLocation()))));
					} else if (args[0].equalsIgnoreCase("setspawn")) {
						if (args.length == 1) {
							player.sendMessage(
									ChatUtils.format(ConfigLang.get().getString("command.ewc.correct-usage").replaceAll(
											"%command%", "/ewc setspawn <1-" + TeamManager.getTeams().size() + ">")));
							return true;
						} else if (args.length == 2) {
							int point;
							try {
								point = Integer.parseInt(args[1]);
							} catch (NumberFormatException e) {
								player.sendMessage(ChatUtils.format("&4" + args[1] + " &cis not a number"));
								return true;
							}
							if (point > TeamManager.getTeams().size()) {
								player.sendMessage(ChatUtils
										.format("&cNumber can not be greater than " + TeamManager.getTeams().size()));
								return true;
							}
							arenaCreator.getArena().getSpawnPoints().put(point, new Coordinates(player.getLocation()));
							player.sendMessage(
									ChatUtils.format(ConfigLang.get().getString("command.ewc.successful.setspawn")
											.replaceAll("%location%", ChatUtils.formatLocation(player.getLocation()))
											.replaceAll("%point%", String.valueOf(point))));
						}
					} else if (args[0].equalsIgnoreCase("setblock")) {
						if (args.length == 1) {
							player.sendMessage(
									ChatUtils.format(ConfigLang.get().getString("command.ewc.correct-usage").replaceAll(
											"%command%", "/ewc setblock <1-" + TeamManager.getTeams().size() + ">")));
							return true;
						} else if (args.length == 2) {
							int point;
							try {
								point = Integer.parseInt(args[1]);
							} catch (NumberFormatException e) {
								player.sendMessage(ChatUtils.format("&4" + args[1] + " &cis not a number"));
								return true;
							}
							if (point > TeamManager.getTeams().size()) {
								player.sendMessage(ChatUtils
										.format("&cNumber can not be greater than " + TeamManager.getTeams().size()));
								return true;
							}
							arenaCreator.setBlockToDestroy(point);
							player.sendMessage(
									ChatUtils.format(ConfigLang.get().getString("command.ewc.info.setblock")));
						}
					} else if (args[0].equalsIgnoreCase("teams")) {
						player.sendMessage(ChatUtils.format(TeamManager.getTeams().values().stream()
								.map(t -> ("&f&l" + t.getId() + " &7&l= " + t.getColor() + "&l" + t.getName()))
								.collect(Collectors.toList())));
					} else if (args[0].equalsIgnoreCase("setshop")) {
						//TODO get entity from config and sucess message, also option to remove shop
						Entity entity = player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
						Utils.makeStatue(entity);
						arenaCreator.getArena().getShops().add(new Coordinates(entity.getLocation()));
					} else if (args[0].equalsIgnoreCase("removeshop")) {
						for(Entity ent : player.getNearbyEntities(6, 6, 6)) {
							if(ent.getType() == EntityType.VILLAGER) {
								ent.remove();
								//TODO arenaCreator.getArena().getShops().remove(new Coordinates(entity.getLocation()));
							}
						}
					} else if (args[0].equalsIgnoreCase("getsign")) {
						//TODO sucess message
						if (args.length == 3) {
							String type = args[1];
							if(!GeneratorManager.getGenerators().keySet().contains(type)) {
								player.sendMessage(ChatUtils.format(ConfigLang.get().getString("arenas-setup.generator.no-exists")
										.replaceAll("%name%", type)
										.replaceAll("%generators%", String.join(", ", GeneratorManager.getGenerators().keySet()))));
								return true;
							}
							int level;
							try {
								level = Integer.parseInt(args[2]);
							} catch (NumberFormatException e) {
								player.sendMessage(ChatUtils.format(ConfigLang.get().getString("arenas-setup.generator.no-number")));
								return true;
							}
							if(level < 0 || level > GeneratorManager.getGenerator(type).getLevelPrice().size()) {
								player.sendMessage(ChatUtils.format(ConfigLang.get().getString("arenas-setup.generator.out-of-range")
										.replaceAll("%max%", String.valueOf(GeneratorManager.getGenerator(type).getLevelPrice().size()))));
								return true;
							}
							ItemStack item = new ItemStack(Material.SIGN);
							ItemMeta meta = item.getItemMeta();
							meta.setDisplayName(ChatUtils.format("&f&lType: &6" + type + " &7- &f&lLevel: &a" + level));
							meta.setLore(Arrays.asList(new String[] {"generator",type,String.valueOf(level)}));
							item.setItemMeta(meta);
							player.getInventory().addItem(item);
						}
					} else if (args[0].equalsIgnoreCase("save")) {
						//TODO save config/world/all
						arenaCreator.saveToConfig();
						arenaCreator.saveWorld();
						ArenaCreator.getArenaCreators().remove(player.getName());
					}
				}
			}
		}
		return true;
	}
}