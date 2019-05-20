package net.minemora.eggwarscore.menu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.game.GameLobby;
import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.game.GameTeam;
import net.minemora.eggwarscore.team.Team;
import net.minemora.eggwarscore.team.TeamManager;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.Utils;

public class TeamMenu extends Menu {
	
	private GameLobby gameLobby;

	public TeamMenu(GameLobby gameLobby) {
		super(numberToBars(TeamManager.getTeams().size()));
		this.gameLobby = gameLobby;
		setInventory(Bukkit.createInventory(null, 9*getBars(), ChatUtils.format(ConfigLang.get().getString("team-selector.title"))));
		for(int i : TeamManager.getTeams().keySet()) {
			Team team = TeamManager.getTeams().get(i);
			ItemStack item = new ItemStack(Material.WOOL, 1, Utils.colorToIdColor(Utils.chatColorToColor(team.getColor())));
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(ChatUtils.format(ConfigLang.get().getString("team-selector.display-name"))
					.replaceAll("%team-color%", ""+team.getColor()).replaceAll("%team-name%", team.getName()));
			item.setItemMeta(meta);
			getInventory().setItem(i-1, item);
			updateItem(i-1);
		}
	}
	
	public void updateItem(int slot) {
		ItemStack item = getInventory().getItem(slot);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.addAll(ChatUtils.formatList(Utils.replaceAll(ConfigLang.get().getStringList("team-selector.lore"), 
				new String[] {"%players%","%max-players%"}, new String[] {
						String.valueOf(gameLobby.getGameTeams().get(slot+1).getPlayers().size()),
						String.valueOf(TeamManager.getMaxPlayers())})));
		for(String playerName : gameLobby.getGameTeams().get(slot+1).getPlayers()) {
			lore.add(gameLobby.getGameTeams().get(slot+1).getTeam().getColor() + playerName);
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
		getInventory().setItem(slot, item);
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
			if(gameLobby.getGame() != null) {
				return;
			}
			GameTeam team = gameLobby.getGameTeams().get(event.getSlot()+1);
			if(team.getPlayers().size() < TeamManager.getMaxPlayers()) {
				boolean fromTeam = false;
				int fromTeamSlot = 0;
				if(GamePlayer.get(event.getWhoClicked().getName()).getGameTeam() != null) {
					fromTeamSlot = GamePlayer.get(event.getWhoClicked().getName()).getGameTeam().getTeam().getId() - 1;
					if(event.getSlot() == fromTeamSlot) {
						event.getWhoClicked().sendMessage(ChatUtils.format(ConfigLang.get().getString("lobby.already-on-team")));
						event.getWhoClicked().closeInventory();
						return;
					}
					fromTeam = true;
				}
				team.addPlayer(event.getWhoClicked().getName());
				updateItem(event.getSlot());
				if(fromTeam) {
					updateItem(fromTeamSlot);
				}
				team.setTag(event.getWhoClicked().getName());
				if(ConfigMain.get().getBoolean("general.leather-armor-when-join-team")) {
					Utils.dyePlayer((Player) event.getWhoClicked(), Utils.chatColorToColor(team.getTeam().getColor()));
				}
				event.getWhoClicked().sendMessage(ChatUtils.format(ConfigLang.get().getString("lobby.joined-to-team")
						.replaceAll("%team-color%", ""+team.getTeam().getColor()).replaceAll("%team-name%", team.getTeam().getName())));
				((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), 
						Sound.valueOf(ConfigMain.get().getString("team.team-selector.select-sound")), 10, 1);
			}
			else {
				event.getWhoClicked().sendMessage(ChatUtils.format(ConfigLang.get().getString("lobby.team-is-full")));
				((Player)event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), 
						Sound.valueOf(ConfigMain.get().getString("team.team-selector.team-full-sound")), 10, 1);
			}
			event.getWhoClicked().closeInventory();
		}
	}

	public GameLobby getGameLobby() {
		return gameLobby;
	}
}