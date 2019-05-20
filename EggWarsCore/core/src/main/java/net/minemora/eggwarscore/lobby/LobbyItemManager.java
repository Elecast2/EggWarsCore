package net.minemora.eggwarscore.lobby;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.menu.OptionsMenu;
import net.minemora.eggwarscore.utils.ItemAction;

public final class LobbyItemManager {
	
	private LobbyItemManager() {}
	
	public static void setup() {
		for(LobbyItem li : Lobby.getLobby().getLobbyItems().values()) {
			switch (li.getId()) {
			case "team":
				li.setAction(new ItemAction() {
					@Override
					public void perform(Player player) {
						GamePlayer.get(player.getName()).getGameLobby().getTeamMenu().open(player);
						player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 10, 1); //TODO FROM CONFIG
					}
				});
				break;
			case "vote-for-map":
				li.setAction(new ItemAction() {
					@Override
					public void perform(Player player) {
						GamePlayer.get(player.getName()).getGameLobby().getMapVoteMenu().open(player);
						player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 10, 1); //TODO FROM CONFIG
					}
				});
				break;
			case "options":
				li.setAction(new ItemAction() {
					@Override
					public void perform(Player player) {
						OptionsMenu.getInstance().open(player);
						player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 10, 1); //TODO FROM CONFIG
					}
				});
				break;
			case "vote-for-shop":
				li.setAction(new ItemAction() {
					@Override
					public void perform(Player player) {
						return;
					}
				});
				break;
			case "leave":
				li.setAction(new ItemAction() {
					@Override
					public void perform(Player player) {
						GamePlayer.sendToLobby(player);
					}
				});
				break;
			}
		}
	}
}
