package net.minemora.eggwarscore.team;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import net.minemora.eggwarscore.chat.ChatManager;
import net.minemora.eggwarscore.chat.PlaceholderReplacer;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.game.GamePlayer;

public final class TeamManager {

	private static Map<Integer, Team> teams = new HashMap<>();
	private static int maxPlayers;
	private static String tabPrefix;

	private TeamManager() {
	}

	public static void loadTeams() {
		for (String teamId : getTeamsList()) {
			int id = Integer.parseInt(teamId);
			teams.put(id, Team.deserealize(id));
		}
		maxPlayers = ConfigMain.get().getInt("game.max-players-per-team");
		tabPrefix = ConfigMain.get().getString("general.tab-prefix");
		
		ChatManager.loadPlaceholder("team-name", new PlaceholderReplacer() {
			@Override
			public String getReplace(Player player) {
				return GamePlayer.get(player.getName()).getGameTeam().getTeam().getName();
			}
		});
		
		ChatManager.loadPlaceholder("team-color", new PlaceholderReplacer() {
			@Override
			public String getReplace(Player player) {
				return GamePlayer.get(player.getName()).getGameTeam().getTeam().getColor().toString();
			}
		});
	}
	
	public static String getTabPrefix(Team team) {
		String finalText = tabPrefix.replaceAll("%team-color%", ""+team.getColor()).replaceAll("%team-name%",team.getName());
		return finalText;
	}

	public static Set<String> getTeamsList() {
		return ConfigMain.get().getConfigurationSection("team.teams").getValues(false).keySet();
	}

	public static Team get(int id) {
		return teams.get(id);
	}

	public static Map<Integer, Team> getTeams() {
		return teams;
	}

	public static int getMaxPlayers() {
		return maxPlayers;
	}

	public static String getTabPrefix() {
		return tabPrefix;
	}
}