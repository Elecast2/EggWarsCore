package net.minemora.eggwarscore.parkour;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.database.Stat;
import net.minemora.eggwarscore.listener.EggWarsListener;
import net.minemora.eggwarscore.lobby.Lobby;
import net.minemora.eggwarscore.player.LobbyPlayer;
import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.uc.UltraCosmeticsHook;
import net.minemora.eggwarscore.utils.ChatUtils;

public class Parkour extends EggWarsListener {
	
	private static Parkour instance;
	
	private Map<Integer,Location> points = new HashMap<>();
	
	private Map<String,Long> lastPlateUse = new HashMap<>();
	
	private Map<String,Integer> players = new HashMap<>();
	
	private Map<String,Integer> timeTrial = new HashMap<>();

	private Parkour() {
		super(EggWarsCoreLobby.getPlugin());
	}
	
	public static void setup() {
		//TODO if not enabled return, and set static boolean enabled to false
		for(String id : ConfigMain.get().getConfigurationSection("parkour.points").getValues(false).keySet()) {
			int i = Integer.parseInt(id);
			int x = ConfigMain.get().getInt("parkour.points." + id + ".x");
			int y = ConfigMain.get().getInt("parkour.points." + id + ".y");
			int z = ConfigMain.get().getInt("parkour.points." + id + ".z");
			getInstance().getPoints().put(i, new Location(Bukkit.getWorlds().get(0), x, y, z));
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		String name = event.getPlayer().getName();
		players.remove(name);
		timeTrial.remove(name);
		lastPlateUse.remove(name);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
    	if(event.getAction().equals(Action.PHYSICAL)) {
    		if(event.getClickedBlock().getType() == Material.GOLD_PLATE) {
    			if(points.values().contains(event.getClickedBlock().getLocation())) {
    				event.setCancelled(true);
        			Location loc = event.getClickedBlock().getLocation();
        			Player player = event.getPlayer();
        			String name = player.getName();
        			if(lastPlateUse.containsKey(name)) {
        	    		if((System.currentTimeMillis() - lastPlateUse.get(name)) < 1000) {
        	    			lastPlateUse.put(name, System.currentTimeMillis());
        	    			return;
        	    		}
        	    	}
        			lastPlateUse.put(name, System.currentTimeMillis());
        			int point = getPointNumber(loc);
        			pointAction(player, point);
    			}
    		}
    	}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			if(event.getCause().equals(DamageCause.VOID)) {
				Player player = (Player) event.getEntity();
				if(!players.containsKey(player.getName())) {
					return;
				}
				LobbyPlayer lp = LobbyPlayer.get(player.getName());
				player.teleport(Lobby.getLobby().getSpawn());
				player.sendMessage(ChatUtils.format("&cParkour Reiniciado"));
				if(players.get(player.getName()) == points.size()-1) {
					String[] dmsg = new String[] {
						"&cTan lejos que habias llegado... :(",
						"&cEn serio no pudiste con ese salto...",
						"&cHasta mi abuela habria podido con ese salto...",
						"&cTe caiste como todo un noob.",
						"&c¡Ya casi lo lograbas! pero obvio, eres noob...",
						"&c¿No pudiste con ese salto? claro, asi como no pudiste con el corazon de ella/el.",
						"&cTe alentaria... pero eres tan malo que nunca lo lograras.",
						"&cHe visto cosas horribles, luego esta ese salto que acabas de fallar.",
						"&c¿Como has fallado ese salto? estas deshonrando a tus padres.",
						"&c¡Vamos campeón! ¡Si se puede! ¡Si se puede! es sarcasmo..."
					};
					String msg = dmsg[ThreadLocalRandom.current().nextInt(dmsg.length)];
					player.sendMessage(ChatUtils.format(msg));
				}
				players.remove(player.getName());
				UltraCosmeticsHook.giveItem(player);
				if(lp.isTimeTrial()) {
					lp.setTimeTrial(false);
				}
			}
		}
	}
	
	@EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
    	if(event.isFlying()) {
    		if(players.containsKey(event.getPlayer().getName())) {
        		event.setCancelled(true);
        		event.getPlayer().sendMessage(ChatUtils.format("&c¡No puedes volar mientras haces el Parkour!"));
        		event.getPlayer().setAllowFlight(false);
        	}
    	}
    }
	
	private void pointAction(Player player, int point) {
		if(point == 0) {
			return;
		}
		String name = player.getName();
		LobbyPlayer lp = LobbyPlayer.get(player.getName());
		if(point == 1) {
			if(!players.containsKey(name)) {
				UltraCosmeticsHook.clear(player);
				UltraCosmeticsHook.removeItem(player);
				player.closeInventory();
				if(player.getAllowFlight()) {
					player.setAllowFlight(false);
					player.sendMessage(ChatUtils.format("&7Modo de vuelo: &cdesactivado"));
				}
				players.put(name, 1);
				player.sendMessage(ChatUtils.format("&a¡Parkour Iniciado!"));
				if(timeTrial.containsKey(name)) {
					lp.setTimeTrial(true);
					startTimeTrial(player);
				}
				player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, (float) 0.5);
			}
			else {
				if(players.get(name) > 1) {
					if(lp.isTimeTrial()) {
						lp.setTimeTrial(false);
						player.sendMessage(ChatUtils.format("&c¡Contrareloj cancelado!"));
						players.put(name, 1);
						player.sendMessage(ChatUtils.format("&a¡Parkour Iniciado!"));
					}
					else {
						players.put(name, 1);
        				player.sendMessage(ChatUtils.format("&a¡Parkour Iniciado!"));
					}
				}
			}
		}
		else if (point == points.size()){
			if(!players.containsKey(name)) {
				player.sendMessage(ChatUtils.format("&c¡Debes hacer el Parkour en orden!"));
				return;
			}
			if(players.get(name) == points.size()-1) {
				players.put(name, points.size());
				player.sendMessage(ChatUtils.format("&a&lFELICIDADES &a¡Has alcanzado el &e&lPunto Final&a!"));
				player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, (float) 1.5);
				player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1.1f);
				Firework fw = (Firework) player.getWorld().spawn(player.getEyeLocation(), Firework.class);
				FireworkMeta fmeta = fw.getFireworkMeta();
				fmeta.addEffect(FireworkEffect.builder().with(Type.BALL)
						.withColor(Color.WHITE).withColor(Color.YELLOW).withColor(Color.GREEN).build());
				fmeta.setPower(0);
				fw.setFireworkMeta(fmeta);
				UltraCosmeticsHook.giveItem(player);
				if(!lp.isParkourDone()) {
					player.sendMessage(ChatUtils.format("&a&l¡Modo contrareloj desbloqueado!"));
					lp.setParkourDone(true);
					lp.updateParkourLine();
					new BukkitRunnable() {
						@Override
						public void run() {
							Database.set(Stat.PARKOUR_DONE, player, 1);
						}
					}.runTaskAsynchronously(getPlugin());
				}
				else {
					if(timeTrial.containsKey(name)) {
						int total = timeTrial.get(name);
						if(total < lp.getParkourTime()) {
							new BukkitRunnable() {
        						@Override
        						public void run() {
        							Database.set(Stat.PARKOUR_TIME, player, total);
        						}
        					}.runTaskAsynchronously(getPlugin());
							player.sendMessage(ChatUtils.format("&a&l¡Has superado tu propio Record!")); //TODO LANG
							lp.setParkourTime(total);
							lp.updateParkourLine();
						}
						else if(lp.getParkourTime() == 0) {
							new BukkitRunnable() {
        						@Override
        						public void run() {
        							Database.set(Stat.PARKOUR_TIME, player, total);
        						}
        					}.runTaskAsynchronously(getPlugin());
							player.sendMessage(ChatUtils.format("&a¡Has hecho tu primer tiempo, ahora supéralo!"));
							lp.setParkourTime(total);
							lp.updateParkourLine();
						}
						else {
							player.sendMessage(ChatUtils.format("&cNo superaste tu record..."));
						}
						timeTrial.remove(name);
						lp.setTimeTrial(false);
						player.sendMessage(ChatUtils.format("&eTu tiempo ha sido de: &a" + 
						String.format("%02d:%02d", total / 60, total % 60)));
					}
				}
			}
			else {
				if(players.get(name) != points.size()) {
					player.sendMessage(ChatUtils.format("&c¡Debes hacer el Parkour en orden!"));
					players.remove(name);
				}
			}
		}
		else {
			if(!players.containsKey(name)) {
				player.sendMessage(ChatUtils.format("&c¡Debes hacer el Parkour en orden!"));
				return;
			}
			if(players.get(name) == point-1) {
				players.put(name, point);
				player.sendMessage(ChatUtils.format("&a¡Has alcanzado el &e&lPunto " + point + "&a!"));
				float pitch = (float) (0.5 + ((point - 1)*((float)(1.5-0.5))/(points.size() - 1)));
				player.playSound(player.getLocation(), Sound.NOTE_PLING, 10, pitch);
			}
			else {
				if(players.get(name) != point) {
					player.sendMessage(ChatUtils.format("&c¡Debes hacer el Parkour en orden!"));
					players.remove(name);
				}
			}
		}
	}
	
	public void startTimeTrial(Player player) {
    	new BukkitRunnable() {
    		String name = player.getName();
    		LobbyPlayer lp = LobbyPlayer.get(player.getName());
    		int count = 0;
    		int seconds = 0;
    		@Override
    		public void run() {
    			if(!player.isOnline()) {
    				cancel();
    				return;
    			}
    			if(!lp.isTimeTrial()) {
    				timeTrial.remove(name);
    				cancel();
    				return;
    			}
    			if(count == 0) {
    				player.playSound(player.getLocation(), Sound.SHEEP_SHEAR, 1, (float) 1.9);
    				player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, (float) 1.5);
    				SharedHandler.getNmsHandler().sendActionBar(player, ChatUtils.format("&a" + String.format("%02d:%02d", seconds / 60, seconds % 60)));
    				timeTrial.put(name, seconds);
    			}
    			else if(count == 1) {
    				player.playSound(player.getLocation(), Sound.SHEEP_SHEAR, 1, (float) 1.9);
    			}
    			else if(count == 2) {
    				player.playSound(player.getLocation(), Sound.SHEEP_SHEAR, 1, (float) 1.9);
    			}
    			else if(count == 3) {
    				player.playSound(player.getLocation(), Sound.SHEEP_SHEAR, 1, (float) 1.9);
    			}
    			else if(count == 4) {
    				player.playSound(player.getLocation(), Sound.SHEEP_SHEAR, 1, (float) 1.9);
    			}
    			
    			count++;
        		if(count == 5) {
        			count = 0;
        			seconds++;
        			if(seconds == 301) { //TODO CONFIG
        				SharedHandler.getNmsHandler().sendTitleToPlayer(player, 20, 40, 20, ChatUtils.format("&c&lTIEMPO AGOTADO"), ""); //TODO LANG
        				timeTrial.remove(name);
        				cancel();
        				return;
        			}
        		}
    		}
    	}.runTaskTimerAsynchronously(getPlugin(), 0L, 4L);
    }
	
	public void timeTrial(Player player) {
		LobbyPlayer lp = LobbyPlayer.get(player.getName());
		if(timeTrial.containsKey(player.getName())) {
			player.sendMessage(ChatUtils.format("&c¡Ya has activado el modo contrareloj! &eEl contador iniciará cuando pises el &e&lPunto 1&e."));
			return;
		}
		if(lp.isParkourDone()) {
			player.sendMessage(ChatUtils.format("&a¡Modo contrareloj activado! &eEl contador iniciará cuando pises el &e&lPunto 1&e."));
			timeTrial.put(player.getName(), 0);
			if(players.containsKey(player.getName())) {
				players.remove(player.getName());
			}
		}
		else {
			player.sendMessage(ChatUtils.format("&c¡Debes pasar el Parkour al menos una vez para activar el modo Contrareloj!"));
		}
	}
	
	private int getPointNumber(Location loc) {
		for(int i : points.keySet()) {
			if(points.get(i).equals(loc)) {
				return i;
			}
		}
		return 0;
	}
	

	public static Parkour getInstance() {
		if(instance == null) {
			instance = new Parkour();
		}
		return instance;
	}

	public Map<Integer,Location> getPoints() {
		return points;
	}
	
	public Map<String,Integer> getPlayers() {
		return players;
	}
}