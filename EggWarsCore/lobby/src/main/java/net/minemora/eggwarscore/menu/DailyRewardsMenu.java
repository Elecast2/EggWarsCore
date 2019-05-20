package net.minemora.eggwarscore.menu;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.player.LobbyPlayer;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.Utils;

public class DailyRewardsMenu extends Menu {
	
	private LobbyPlayer lobbyPlayer;
	
	private ItemStack[] items = new ItemStack[6];

	public DailyRewardsMenu(LobbyPlayer lobbyPlayer) {
		super(5);
		this.lobbyPlayer = lobbyPlayer;
		Inventory inv = Bukkit.createInventory(null, 9*getBars(), ChatUtils.format("&2&lRecompensas Diarias")); //TODO LANG
		setInventory(inv);
		if(!lobbyPlayer.isDailyRewardsLoaded()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Database.getDatabase().loadDailyRewards(lobbyPlayer);
					new BukkitRunnable() {
						@Override
						public void run() {
							updateItems();
						}
					}.runTask(EggWarsCoreLobby.getPlugin());
				}
			}.runTaskAsynchronously(EggWarsCoreLobby.getPlugin());
		}
	}
	
	private void updateItems() {		
		setDailyItem(getItem(lobbyPlayer.getDailyReward(), 86400, "&aRecompensa del &e&lDia"));
		setWeeklyItem(getItem(lobbyPlayer.getWeeklyReward(), 604800, "&aRecompensa de la &e&lSemana"));
		setMonthlyItem(getItem(lobbyPlayer.getMonthlyReward(), 2592000, "&aRecompensa del &e&lMes"));
		setFacebookItem(getItem(lobbyPlayer.isFacebook(), "&aSíguenos en &9&lFacebook", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGViNDYxMjY5MDQ0NjNmMDdlY2ZjOTcyYWFhMzczNzNhMjIzNTliNWJhMjcxODIxYjY4OWNkNTM2N2Y3NTc2MiJ9fX0="));
		setTwitterItem(getItem(lobbyPlayer.isTwitter(), "&aSíguenos en &b&lTwitter", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2M3NDVhMDZmNTM3YWVhODA1MDU1NTkxNDllYTE2YmQ0YTg0ZDQ0OTFmMTIyMjY4MThjMzg4MWMwOGU4NjBmYyJ9fX0="));
		setYoutubeItem(getItem(lobbyPlayer.isYoutube(), "&aSíguenos en &c&lYouTube", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDJmNmMwN2EzMjZkZWY5ODRlNzJmNzcyZWQ2NDU0NDlmNWVjOTZjNmNhMjU2NDk5YjVkMmI4NGE4ZGNlIn19fQ=="));
	}
	
	private ItemStack getItem(int timeElapsed, int waitTime, String displayName) {
		long time = System.currentTimeMillis()/1000;
		if(time - timeElapsed > waitTime) {
			ItemStack item = new ItemStack(Material.WOOL, 1, (short)5);
			ItemMeta meta = item.getItemMeta();
   	    	meta.setDisplayName(ChatUtils.format(displayName));
   	    	meta.setLore(Arrays.asList(ChatUtils.format(new String[] {"&7Has click para reclamar tu recompensa"})));
   	    	item.setItemMeta(meta);
   	    	return item;
		}
		else {
			ItemStack item = new ItemStack(Material.WOOL, 1, (short)14);
			ItemMeta meta = item.getItemMeta();
   	    	meta.setDisplayName(ChatUtils.format(displayName));
   	    	int[] wait = getTime((int) (waitTime - (time - timeElapsed)));
   	    	meta.setLore(Arrays.asList(ChatUtils.format(new String[] {"&cEspera: " + wait[0] + " dias, "+ wait[1] 
   	    			+ " horas y " + wait[2] + " minutos"})));
   	    	item.setItemMeta(meta);
   	    	return item;
		}
	}
	
	private ItemStack getItem(boolean claimed, String displayName, String texture) {
		if(!claimed) {
			ItemStack item = Utils.getCustomTextureHead(texture);
			ItemMeta meta = item.getItemMeta();
   	    	meta.setDisplayName(ChatUtils.format(displayName));
   	    	meta.setLore(Arrays.asList(ChatUtils.format(new String[] {"&7Has click para reclamar tu recompensa"})));
   	    	item.setItemMeta(meta);
   			return item;
		}
		else {	        					
			ItemStack item = new ItemStack(Material.WOOL, 1, (short)14);
			ItemMeta meta = item.getItemMeta();
   	    	meta.setDisplayName(ChatUtils.format(displayName));
   	    	meta.setLore(Arrays.asList(ChatUtils.format(new String[] {"&cYa reclamaste esta recompensa"})));
   	    	item.setItemMeta(meta);
   			return item;
		}
	}
	
	private int[] getTime(int seconds) {
		int minutes = (seconds / 60) % 60;
		int hours = (seconds / (60 * 60)) % 24;
		int days = (seconds / (60 * 60 * 24));
		return new int[] {days,hours,minutes};
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getClickedInventory() == null) {
			return;
		}
	    if (event.getClick().isShiftClick()) {
	        if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {
	        	InventoryView iv = event.getWhoClicked().getOpenInventory();
	        	if(iv.getTopInventory() != null) {
	        		if(iv.getTopInventory().equals(getInventory())) {
		        		event.setCancelled(true);
		        	}
	        	}
	        }
	    }
		if (event.getClickedInventory().equals(getInventory())) {
			if(event.getCurrentItem() == null) {
				return;
			}
			if(event.getCurrentItem().getType() == Material.AIR) {
				return;
			}
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			if(event.getCurrentItem().equals(getDailyItem())) {
				long time = System.currentTimeMillis()/1000;
				if(time - lobbyPlayer.getDailyReward() > 86400) {
					lobbyPlayer.setDailyReward((int)time);
					updateTime("daily", player.getUniqueId());
					lobbyPlayer.addExp(5);
					player.sendMessage(ChatUtils.format("&f[&e&lRecompensas&f] &6Has recibido &e5 &dExp &6como recompensa diaria."));
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
				}
				else {
					player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1); //TODO CONFIG
					return;
				}
			}
			else if(event.getCurrentItem().equals(getWeeklyItem())) {
				long time = System.currentTimeMillis()/1000;
				if(time - lobbyPlayer.getWeeklyReward() > 604800) {
					lobbyPlayer.setWeeklyReward((int)time);
					updateTime("weekly", player.getUniqueId());
					lobbyPlayer.addExp(15);
					player.sendMessage(ChatUtils.format("&f[&e&lRecompensas&f] &6Has recibido &e15 &dExp &6como recompensa semanal."));
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
				}
				else {
					player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1); //TODO CONFIG
					return;
				}
			}
			else if(event.getCurrentItem().equals(getMonthlyItem())) {
				long time = System.currentTimeMillis()/1000;
				if(time - lobbyPlayer.getMonthlyReward() > 2592000) {
					lobbyPlayer.setMonthlyReward((int)time);
					updateTime("monthly", player.getUniqueId());
					lobbyPlayer.addExp(50);
					player.sendMessage(ChatUtils.format("&f[&e&lRecompensas&f] &6Has recibido &e50 &dExp &6como recompensa mensual."));
					player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
				}
				else {
					player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1); //TODO CONFIG
					return;
				}
			}
			else if(event.getCurrentItem().getItemMeta().getDisplayName().equals(getFacebookItem().getItemMeta().getDisplayName())) {
				if(lobbyPlayer.isFacebook()) {
					player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1); //TODO CONFIG
					return;
				}
				lobbyPlayer.setFacebook(true);
				updateSocial("facebook", player.getUniqueId());
				String[] msg = {
        				"&f&l&m=====================&7&l&m=====================",
        				"&r",
        				"    &7Has click en el siguiente link y dale a like a la pagina de Facebook para reclamar tu recompensa",
        				"&r",
        				"      &b&nhttps://www.facebook.com/MinemoraNetwork",
        				"&r",
        				"&7&l&m=====================&f&l&m====================="
        		};
        		player.sendMessage(ChatUtils.format(msg));
				new BukkitRunnable() {
		            @Override
		            public void run() {
		            	if(!player.isOnline()) {
		            		return;
		            	}
		            	lobbyPlayer.addExp(10);
		            	player.sendMessage(ChatUtils.format("&f[&e&lRecompensas&f] &6Has recibido &e10 &dExp &6como recompensa "
		            			+ "por seguirnos en &9&lFacebook"));
		            	player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
		            }
				}.runTaskLater(EggWarsCoreLobby.getPlugin(), 300);
			}
			else if(event.getCurrentItem().getItemMeta().getDisplayName().equals(getTwitterItem().getItemMeta().getDisplayName())) {
				if(lobbyPlayer.isTwitter()) {
					player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1); //TODO CONFIG
					return;
				}
				lobbyPlayer.setTwitter(true);
				updateSocial("twitter", player.getUniqueId());
				String[] msg = {
        				"&f&l&m=====================&7&l&m=====================",
        				"&r",
        				"    &7Has click en el siguiente link y siguenos en Twitter para reclamar tu recompensa",
        				"&r",
        				"      &b&nhttps://twitter.com/Minemora_net",
        				"&r",
        				"&7&l&m=====================&f&l&m====================="
        		};
        		player.sendMessage(ChatUtils.format(msg));
				new BukkitRunnable() {
		            @Override
		            public void run() {
		            	if(!player.isOnline()) {
		            		return;
		            	}
		            	lobbyPlayer.addExp(10);
		            	player.sendMessage(ChatUtils.format("&f[&e&lRecompensas&f] &6Has recibido &e10 &dExp &6como recompensa "
		            			+ "por seguirnos en &b&lTwitter"));
		            	player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
		            }
				}.runTaskLater(EggWarsCoreLobby.getPlugin(), 300);
			}
			else if(event.getCurrentItem().getItemMeta().getDisplayName().equals(getYoutubeItem().getItemMeta().getDisplayName())) {
				if(lobbyPlayer.isYoutube()) {
					player.playSound(player.getLocation(), Sound.NOTE_BASS, 10, 1); //TODO CONFIG
					return;
				}
				lobbyPlayer.setYoutube(true);
				updateSocial("youtube", player.getUniqueId());
				String[] msg = {
        				"&f&l&m=====================&7&l&m=====================",
        				"&r",
        				"    &7Has click en el siguiente link y suscribete a nuestro canal de YouTube para reclamar tu recompensa",
        				"&r",
        				"      &b&nhttps://www.youtube.com/channel/UCNLj-DV-MInqtonWKj3UTlA",
        				"&r",
        				"&7&l&m=====================&f&l&m====================="
        		};
        		player.sendMessage(ChatUtils.format(msg));
				new BukkitRunnable() {
		            @Override
		            public void run() {
		            	if(!player.isOnline()) {
		            		return;
		            	}
		            	lobbyPlayer.addExp(10);
		            	player.sendMessage(ChatUtils.format("&f[&e&lRecompensas&f] &6Has recibido &e10 &dExp &6como recompensa "
		            			+ "por seguirnos en &c&lYouTube"));
		            	player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1);
		            }
				}.runTaskLater(EggWarsCoreLobby.getPlugin(), 300);
			}
			player.closeInventory();
		}
	}
	
	private void updateTime(String col, UUID uid) {
		Database.getDatabase().asyncUpdate("UPDATE " + Database.getDatabase().getPrefix() + "rewards SET " + col 
				+ " = '" + System.currentTimeMillis()/1000 + "' WHERE uuid = '" + uid + "';");
		updateItems();
	}
	
	private void updateSocial(String col, UUID uid) {
		Database.getDatabase().asyncUpdate("UPDATE " + Database.getDatabase().getPrefix() + "rewards SET " + col 
				+ " = '1' WHERE uuid = '" + uid + "';");
		updateItems();
	}
	
	private ItemStack getDailyItem() {
		return items[0];
	}
	
	private ItemStack getWeeklyItem() {
		return items[1];
	}
	
	private ItemStack getMonthlyItem() {
		return items[2];
	}
	
	private ItemStack getFacebookItem() {
		return items[3];
	}
	
	private ItemStack getTwitterItem() {
		return items[4];
	}
	
	private ItemStack getYoutubeItem() {
		return items[5];
	}
	
	private void setDailyItem(ItemStack item) {
		items[0] = item;
		getInventory().setItem(10, item);
	}
	
	private void setWeeklyItem(ItemStack item) {
		items[1] = item;
		getInventory().setItem(13, item);
	}
	
	private void setMonthlyItem(ItemStack item) {
		items[2] = item;
		getInventory().setItem(16, item);
	}
	
	private void setFacebookItem(ItemStack item) {
		items[3] = item;
		getInventory().setItem(28, item);
	}
	
	private void setTwitterItem(ItemStack item) {
		items[4] = item;
		getInventory().setItem(31, item);
	}
	
	private void setYoutubeItem(ItemStack item) {
		items[5] = item;
		getInventory().setItem(34, item);
	}

	public LobbyPlayer getLobbyPlayer() {
		return lobbyPlayer;
	}

}
