package net.minemora.eggwarscore.scoreboard;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.player.LobbyPlayer;
import net.minemora.eggwarscore.player.TournamentLobbyPlayer;
import net.minemora.eggwarscore.shared.VaultManager;

public final class ScoreboardManager {
	
	private static Scoreboard lobbyScoreboard;
	
	private ScoreboardManager() {};
	
	public static void loadScoreboards() {
		lobbyScoreboard = Scoreboard.deserealize("lobby");
	}

	public static void loadPlaceholders() {
		loadPlaceholder("player", false);
		loadPlaceholder("group", false);
		loadPlaceholder("boost", false);
		loadPlaceholder("level", true);
		loadPlaceholder("exp", true);
		loadPlaceholder("keys", true);
		loadPlaceholder("money", true);
		loadPlaceholder("kills", false);
		loadPlaceholder("deaths", false);
		loadPlaceholder("wins", false);
		loadPlaceholder("eggs", false);
	}
	
	public static void loadPlaceholder(String placeholder, boolean dynamic) {
		new Placeholder(placeholder, dynamic);
	}
	
	public static void setLobbyScoreboard(Player player) {
		LobbyPlayer lp = LobbyPlayer.get(player.getName());
		Map<Placeholder,String> defaults =  new HashMap<>();
		defaults.put(Placeholder.get("player"), player.getName());
		defaults.put(Placeholder.get("group"), VaultManager.getPlayerGroup(player));
		defaults.put(Placeholder.get("boost"), getBoostString(lp.getExpMultiplier()));
		defaults.put(Placeholder.get("level"), String.valueOf(lp.getLevel()));
		defaults.put(Placeholder.get("exp"), String.valueOf(lp.getExp()));
		defaults.put(Placeholder.get("keys"), String.valueOf(lp.getChestKeys()));
		defaults.put(Placeholder.get("money"), String.valueOf(lp.getMoney()));
		defaults.put(Placeholder.get("kills"), String.valueOf(lp.getKills()));
		defaults.put(Placeholder.get("deaths"), String.valueOf(lp.getDeaths()));
		defaults.put(Placeholder.get("wins"), String.valueOf(lp.getWins()));
		defaults.put(Placeholder.get("eggs"), String.valueOf(lp.getDestroyedEggs()));
		if(GameManager.isTournamentMode()) {
			TournamentLobbyPlayer tlp = (TournamentLobbyPlayer) lp;
			if(tlp.isStaff() && !tlp.isPlayer()) {
				defaults.put(Placeholder.get("tournament-team"), "STAFF");
				defaults.put(Placeholder.get("tournament-points"), String.valueOf(0));
			}
			else {
				defaults.put(Placeholder.get("tournament-team"), tlp.getTeam().getTeamName());
				defaults.put(Placeholder.get("tournament-points"), String.valueOf(tlp.getTeam().getPoints()));
			}
		}
		lobbyScoreboard.set(player, defaults);
	}
	
	private static String getBoostString(int mult) { //TODO CONFIG AND LANG
		switch(mult) {		
		case 2:
			return "&ax2";
		case 3:
			return "&ax3";
		case 4:
			return "&ax4";
		default:
			return "&7desactivado";
		}
	}

	public static Scoreboard getLobbyScoreboard() {
		return lobbyScoreboard;
	}
}