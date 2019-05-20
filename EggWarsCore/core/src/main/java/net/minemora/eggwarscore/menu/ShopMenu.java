package net.minemora.eggwarscore.menu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.shop.Offer;
import net.minemora.eggwarscore.shop.Shop;
import net.minemora.eggwarscore.shop.ShopSection;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.Utils;

public class ShopMenu extends Menu {
	
	private Shop shop;
	private Map<Integer,Map<Integer,Inventory>> sections = new HashMap<>();

	public ShopMenu(Shop shop) {
		super((shop.getSections().size() > 9) ? 3 : 2);
		this.shop = shop;
		ItemStack pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)15);
		ItemMeta meta = pane.getItemMeta();
		meta.setDisplayName(" ");
		pane.setItemMeta(meta);
		for(ShopSection section : shop.getSections().values()) {
			Map<Integer,Set<Offer>> timedOffers = new HashMap<>();
			sections.put(section.getSlot(), new HashMap<>());
			for(Offer offer : section.getOffers().values()) {
				if(offer.isTimed()) {
					if(!timedOffers.containsKey(offer.getTime())) {
						timedOffers.put(offer.getTime(), new HashSet<>());
					}
					timedOffers.get(offer.getTime()).add(offer);
				}
				else {
					if(!timedOffers.containsKey(0)) {
						timedOffers.put(0, new HashSet<>());
					}
					timedOffers.get(0).add(offer);
				}
			}
			for(int time : timedOffers.keySet()) {
				if(time != 0) {
					for(int time2 : timedOffers.keySet()) {
						if(time > time2) {
							timedOffers.get(time).addAll(timedOffers.get(time2));
						}
					}
				}
				int bars = 1;
				if(timedOffers.get(time).size() > 18) {
					bars += 2;
				}
				else if(timedOffers.get(time).size() > 9) {
					bars += 1;
				}
				Inventory inv = Bukkit.createInventory(null, 9*(getBars()+bars), ChatUtils.format(section.getName()));
				for(ShopSection section2 : shop.getSections().values()) {
					inv.setItem(section2.getSlot(), section2.getMenuItem());
				}
				for(int i = ((getBars()-1)*9); i < ((getBars())*9); i++) {
					inv.setItem(i, pane);
				}
				for(Offer offer : timedOffers.get(time)) {
					inv.setItem(((getBars()*9)+(offer.getOrder()-1)), offer.getMenuItem());
				}
				sections.get(section.getSlot()).put(time, inv);
			}
		}
		Inventory inv = Bukkit.createInventory(null, 9*(getBars()-1), ChatUtils.format(ConfigLang.get().getString("shop.menu-title")));
		for(ShopSection section : shop.getSections().values()) {
			inv.setItem(section.getSlot(), section.getMenuItem());
		}
		//for(int i = ((getBars()-1)*9); i < ((getBars())*9); i++) {
		//	inv.setItem(i, pane);
		//}
		setInventory(inv);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getClickedInventory() == null) {
			return;
		}
		if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {
			if (event.getClick().isShiftClick() || event.getClick() == 	ClickType.DOUBLE_CLICK) {
	        	InventoryView iv = event.getWhoClicked().getOpenInventory();
	        	if(iv.getTopInventory() != null) {
	        		if(iv.getTopInventory().equals(getInventory()) || isAShopInventory(iv.getTopInventory())) {
		        		event.setCancelled(true);
		        	}
	        	}
	        }
	    }
		if (event.getClickedInventory().equals(getInventory()) || isAShopInventory(event.getClickedInventory())) {
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();
			GamePlayer gp = GamePlayer.get(player.getName());
			if(gp.getGame()==null) {
				event.setCancelled(true);
				return;
			}
			if(event.getSlot()<sections.size()) {
				//player.closeInventory();
				openSection(player, event.getSlot());
				player.playSound(player.getLocation(), Sound.valueOf(ConfigMain.get().getString("shop.click-section-sound")), 10, 1);
			}
			else if(event.getSlot() >= ((9*getBars()))) {
				for(Integer i : sections.keySet()) {
					Inventory inv = getInventory(i, gp.getGame().getTimeElapsed());
					if(inv.equals(event.getClickedInventory())) {
						if(event.getSlot() < ((shop.getSections().get(i).getOffersAmount(gp.getGame().getTimeElapsed()))+(9*getBars()))) {
							Offer offer = shop.getSections().get(i).getOffers().get((event.getSlot()+1)-(9*getBars()));
							if(Utils.hasItems(player, offer.getMaterial(), offer.getPrice())) {
								Color color = gp.getColor();
								int rewardMultiplier = 1;
								if (event.getClick().isShiftClick()) {
									int mp = Utils.countItems(player, offer.getMaterial())/offer.getPrice();
									if(mp>=1) {
										if(offer.getMenuItem().getAmount() <= offer.getMenuItem().getMaxStackSize()) {
											if(mp*offer.getMenuItem().getAmount() >= offer.getMenuItem().getMaxStackSize()) {
												rewardMultiplier = offer.getMenuItem().getMaxStackSize()/offer.getMenuItem().getAmount();
											}
											else {
												rewardMultiplier = mp;
											}
										}
									}
								}
								int finalPrice = rewardMultiplier*offer.getPrice();
								Utils.removeItems(player, offer.getMaterial(), finalPrice);
								ItemStack reward = offer.getReward(color).clone();
								reward.setAmount(reward.getAmount()*rewardMultiplier);
								if(player.getInventory().firstEmpty() != -1) {
									player.getInventory().addItem(reward);
	    						}
	    						else {
	    							player.getWorld().dropItemNaturally(player.getLocation().add(0,1,0), reward);
	    						}
								player.playSound(player.getLocation(), Sound.valueOf(ConfigMain.get().getString("shop.buy-sound")), 10, 1);
							}
							else {
								player.sendMessage(ChatUtils.format(ConfigLang.get().getString("shop.no-material")));
								player.playSound(player.getLocation(), Sound.valueOf(ConfigMain.get().getString("shop.no-material-sound")), 10, 1);
							}
						}
						break;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryDrag(InventoryDragEvent event) {
		if (event.getInventory().equals(getInventory()) || isAShopInventory(event.getInventory())) {
			event.setCancelled(true);
		}
	}
	
	private boolean isAShopInventory(Inventory inv) {
		for(int i : sections.keySet()) {
			if(sections.get(i).containsValue(inv)) {
				return true;
			}
		}
		return false;
	}
	
	public Inventory getInventory(int section, long time) {
		Inventory inv = null;;
		for(int minTime : sections.get(section).keySet()) {
			if(time >= minTime) {
				inv = sections.get(section).get(minTime);
			}
		}
		if(inv==null) {
			inv = sections.get(section).get(0);
		}
		return inv;
	}
	
	public void openSection(Player player, int index) {
		GamePlayer gp = GamePlayer.get(player.getName());
		if(gp.getGame()==null) {
			return;
		}
		player.openInventory(getInventory(index, gp.getGame().getTimeElapsed()));
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}
}
