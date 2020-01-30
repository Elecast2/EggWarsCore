package net.minemora.eggwarscore.lobby;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.bungee.BungeeHandler;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.menu.LobbiesMenu;
import net.minemora.eggwarscore.menu.PlayMenu;
import net.minemora.eggwarscore.utils.ItemAction;
import net.minemora.eggwarscore.utils.Utils;

public final class LobbyItemManager {
	
	private LobbyItemManager() {}
	
	public static void setup() {
		for(LobbyItem li : Lobby.getLobby().getLobbyItems().values()) {
			switch (li.getId()) {
			case "join":
				li.setAction(new ItemAction() {
					@Override
					public void perform(Player player) {
						PlayMenu.getMenu().open(player);
						player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.5f, 1); //TODO FROM CONFIG
					}
				});
				break;
			case "lobbies":
				li.setAction(new ItemAction() {
					@Override
					public void perform(Player player) {
						LobbiesMenu.getInstance().open(player);
						LobbiesMenu.getInstance().updateItems();
					}
				});
				break;
			case "leave":
				li.setAction(new ItemAction() {
					@Override
					public void perform(Player player) {
						BungeeHandler.sendPlayer(player, Utils.choice(ConfigMain.get().getStringList("leave-servers")));
					}
				});
				break;
			}
		}
	}
}