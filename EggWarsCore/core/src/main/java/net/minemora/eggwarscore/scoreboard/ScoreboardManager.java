package net.minemora.eggwarscore.scoreboard;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import net.minemora.eggwarscore.arena.ArenaManager;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.config.ConfigScoreboard;
import net.minemora.eggwarscore.game.Game;
import net.minemora.eggwarscore.game.GameLobby;
import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.team.TeamManager;

public final class ScoreboardManager {
	
	private static Scoreboard lobbyScoreboard;
	private static Scoreboard gameScoreboard;
	private static String aliveSymbol;
	private static String deathSymbol;
	
	private ScoreboardManager() {};
	
	public static void loadScoreboards() {
		lobbyScoreboard = Scoreboard.deserealize("waiting-lobby");
		gameScoreboard = Scoreboard.deserealize("game");
		aliveSymbol = ConfigScoreboard.get().getString("symbols.team-with-egg");
		deathSymbol = ConfigScoreboard.get().getString("symbols.team-vulnerable");
	}

	public static void loadPlaceholders() {
		new Placeholder("player", false);
		new Placeholder("money", true);
		new Placeholder("date", false);
		new Placeholder("players", true);
		new Placeholder("max-players", false);
		new Placeholder("current-kills", true);
		new Placeholder("current-map", false);
		for(int i = 1; i <= ConfigMain.get().getInt("general.max-votable-maps"); i++) {
			new Placeholder("map-name-" + i, false);
			new Placeholder("map-votes-" + i, true);
		}
		for(int i = 1; i <= TeamManager.getTeams().size(); i++) {
			new Placeholder("team-name-" + i, false);
			new Placeholder("team-alive-" + i, true);
			new Placeholder("symbol-" + i, true);
		}
		new Placeholder("timer", true, true);
	}
	
	public static void setLobbyScoreboard(Player player, GameLobby gameLobby) {
		GamePlayer gp = GamePlayer.get(player.getName());
		Map<Placeholder,String> defaults =  new HashMap<>();
		defaults.put(Placeholder.get("player"), player.getName());
		defaults.put(Placeholder.get("money"), String.valueOf(gp.getMoney()));
		defaults.put(Placeholder.get("date"), gameLobby.getDateString());
		defaults.put(Placeholder.get("players"), String.valueOf(gameLobby.getPlayersCount()));
		defaults.put(Placeholder.get("max-players"), String.valueOf(TeamManager.getMaxPlayers()*TeamManager.getTeams().size()));
		int i = 1;
		for(String worldName : gameLobby.getMapVotes().keySet()) {
			defaults.put(Placeholder.get("map-name-" + i), ArenaManager.getArena(worldName).getArenaName());
			defaults.put(Placeholder.get("map-votes-" + i), String.valueOf(gameLobby.getMapVotes().get(worldName)));
			i++;
		}
		while(i <= ConfigMain.get().getInt("general.max-votable-maps")) {
			defaults.put(Placeholder.get("map-name-" + i), " - - ");
			defaults.put(Placeholder.get("map-votes-" + i), "-");
			i++;
		}
		lobbyScoreboard.set(player, defaults);
	}
	
	public static void setGameScoreboard(Game game) {
		for(Player player : game.getBukkitPlayers()) {
			setGameScoreboard(player, game);
		}
	}
	
	public static void setGameScoreboard(Player player, Game game) {
		GamePlayer gp = GamePlayer.get(player.getName());
		Map<Placeholder,String> defaults =  new HashMap<>();
		defaults.put(Placeholder.get("player"), player.getName());
		defaults.put(Placeholder.get("money"), String.valueOf(gp.getMoney()));
		defaults.put(Placeholder.get("date"), game.getDateString());
		defaults.put(Placeholder.get("players"), String.valueOf(game.getPlayersCount()));
		defaults.put(Placeholder.get("max-players"), String.valueOf(TeamManager.getMaxPlayers()*TeamManager.getTeams().size()));
		defaults.put(Placeholder.get("current-kills"), String.valueOf(0));
		defaults.put(Placeholder.get("current-map"), game.getGameArena().getArena().getArenaName());
		defaults.put(Placeholder.get("timer"), "00:00");
		for(int i = 1; i <= TeamManager.getTeams().size(); i++) {
			defaults.put(Placeholder.get("team-name-" + i), TeamManager.getTeams().get(i).getFinalName());
			defaults.put(Placeholder.get("team-alive-" + i), String.valueOf(game.getGameTeams().get(i).getAliveCount()));
			defaults.put(Placeholder.get("symbol-" + i), game.getGameTeams().get(i).isEggDestroyed() ? deathSymbol : aliveSymbol);
		}	
		gameScoreboard.set(player, defaults);
	}
	
	public static String getAliveSymbol() {
		return aliveSymbol;
	}

	public static String getDeathSymbol() {
		return deathSymbol;
	}

	public static Scoreboard getLobbyScoreboard() {
		return lobbyScoreboard;
	}
	
	public static Scoreboard getGameScoreboard() {
		return gameScoreboard;
	}
}