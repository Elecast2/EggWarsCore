package net.minemora.eggwarscore.lobby;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.bungee.BungeeHandler;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.game.Game;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.game.TournamentGroup;
import net.minemora.eggwarscore.menu.LobbiesMenu;
import net.minemora.eggwarscore.menu.PlayMenu;
import net.minemora.eggwarscore.player.LobbyPlayer;
import net.minemora.eggwarscore.player.TournamentLobbyPlayer;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.ItemAction;
import net.minemora.eggwarscore.utils.Utils;
import net.minemora.reportsystem.VaultManager;

public final class LobbyItemManager {
	
	private LobbyItemManager() {}
	
	public static void setup() {
		for(LobbyItem li : Lobby.getLobby().getLobbyItems().values()) {
			switch (li.getId()) {
			case "join":
				li.setAction(new ItemAction() {
					@Override
					public void perform(Player player) {
						if(GameManager.isTournamentMode()) {
							if(VaultManager.hasPermission(player, "ewc.tournamentstaff")) {
								PlayMenu.getMenu().open(player);
								player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.5f, 1); //TODO FROM CONFIG
								return;
							}
                			TournamentLobbyPlayer tlp = (TournamentLobbyPlayer) LobbyPlayer.get(player.getName());
                			if(tlp == null) {
                				return;
                			}
                			if(tlp.getTeam() == null) {
                				//TODO send as spectator
                				return;
                			}
                			TournamentGroup group = tlp.getTeam().getTournamentGroup();
                			if(group == null) {
                				return;
                			}
                			if(group.getGamesPlayed() == 1) {
                				player.sendMessage(ChatUtils.format("&cYa jugaste todas las partidas, espera información en el discord para saber mas sobre como continuará el torneo"));
                				return;
                			}
                			Game game = GameManager.getGamesMenus().get("squad").getGamesBySlot().get(tlp.getTeam().getTournamentGroup().getGameId());
                			GameManager.attemptToSendPlayer(player, game);
                			return;
                		}
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