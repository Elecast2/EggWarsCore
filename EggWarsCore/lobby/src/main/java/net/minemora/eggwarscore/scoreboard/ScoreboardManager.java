package net.minemora.eggwarscore.scoreboard;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import net.minemora.eggwarscore.player.LobbyPlayer;
import net.minemora.eggwarscore.shared.VaultManager;

public final class ScoreboardManager {
	
	private static Scoreboard lobbyScoreboard;
	
	private ScoreboardManager() {};
	
	public static void loadScoreboards() {
		lobbyScoreboard = Scoreboard.deserealize("lobby");
	}

	public static void loadPlaceholders() {
		new Placeholder("player", false);
		new Placeholder("group", false);
		new Placeholder("boost", false);
		new Placeholder("level", true);
		new Placeholder("exp", true);
		new Placeholder("keys", true);
		new Placeholder("money", true);
		new Placeholder("kills", false);
		new Placeholder("deaths", false);
		new Placeholder("wins", false);
		new Placeholder("eggs", false);
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