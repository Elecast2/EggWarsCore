package net.minemora.eggwarscore.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.config.ConfigTournament;
import net.minemora.eggwarscore.config.ConfigTournamentStats;
import net.minemora.eggwarscore.holographicdisplays.HolographicDisplaysHook;
import net.minemora.eggwarscore.player.TournamentLobbyPlayer;
import net.minemora.eggwarscore.player.TournamentTeam;
import net.minemora.eggwarscore.scoreboard.ScoreboardManager;
import net.minemora.eggwarscore.utils.CmdColor;
import net.minemora.reportsystem.VaultManager;

public class TournamentManager {
	
	private static TournamentManager instance;
	
	private Map<String,TournamentTeam> teams;
	
	private Map<String,TournamentGroup> groups;
	
	private String stage;
	
	private List<String> allowedGroups;

	private TournamentManager() {}
	
	public void setup() {
		ConfigTournament.getInstance().setup();
		ConfigTournamentStats.getInstance().setup();
		
		loadFromConfig();
		
		for(TournamentTeam team : teams.values()) {
			team.updateSign();
		}
		if(HolographicDisplaysHook.isEnabled()) {
			HolographicDisplaysHook.loadTournamentPointsPlaceholders();
		}
	}
	
	private void loadFromConfig() {
		teams = new HashMap<>();
		groups = new HashMap<>();
		for(String teamId : ConfigTournament.get().getConfigurationSection("teams").getKeys(false)) {
			int gameTeamId = ConfigTournament.get().getInt("teams." + teamId + ".game-team-id");
			String teamName = ConfigTournament.get().getString("teams." + teamId + ".team-name");
			List<String> members = ConfigTournament.get().getStringList("teams." + teamId + ".members");
			String stringLoc = ConfigTournament.get().getString("teams." + teamId + ".sign-location");
			String[] coords = stringLoc.split(",");
			Location signLoc = new Location(Bukkit.getWorld("world"), Integer.valueOf(coords[0]), Integer.valueOf(coords[1]), Integer.valueOf(coords[2]));
			int points = ConfigTournament.get().getInt("teams." + teamId + ".points");
			//System.out.println("loading team. id = " + teamId + ", points = " + points);
			teams.put(teamId, new TournamentTeam(teamId, gameTeamId, teamName, members, signLoc, points));
		}
		
		for(String groupId : ConfigTournament.get().getConfigurationSection("groups").getKeys(false)) {
			String groupName = ConfigTournament.get().getString("groups." + groupId + ".group-name");
			List<String> teamsId = ConfigTournament.get().getStringList("groups." + groupId + ".teams");
			List<TournamentTeam> teamsObject = new ArrayList<>();
			for(String id : teamsId) {
				teamsObject.add(teams.get(id));
			}
			int gameId = ConfigTournament.get().getInt("groups." + groupId + ".game-id");
			int gamesPlayed = ConfigTournament.get().getInt("groups." + groupId + ".games-played");
			groups.put(groupId, new TournamentGroup(groupId, groupName, gameId, teamsObject, gamesPlayed));
		}
		allowedGroups = ConfigTournament.get().getStringList("allowed-groups");
	}
	
	public void reloadTeams() {
		ConfigTournament.getInstance().reload();
		loadFromConfig();
		for(TournamentTeam team : teams.values()) {
			team.updateSign();
		}
		for(Player lp : Bukkit.getOnlinePlayers()) {
			TournamentLobbyPlayer tlp = (TournamentLobbyPlayer) TournamentLobbyPlayer.get(lp.getName());
			if(tlp == null) {
				continue;
			}
			tlp.setTeam(null);
			inner: for(TournamentTeam tteam : teams.values()) {
				if(tteam.getMembers().contains(lp.getName())) {
					tlp.setTeam(tteam);
					break inner;
				}
			}
			if(tlp.getTeam() == null) {
				if(!VaultManager.hasPermission(lp, "ewc.tournamentstaff")) {
					lp.kickPlayer("No tienes permisos para entrar a este lugar.");
				}
				else {
					tlp.setStaff(true);
					ScoreboardManager.getLobbyScoreboard().update(lp, "tournament-team", "STAFF");
					//System.out.println("updating scoreboard for " + lp.getName() + " (now staff)");
				}
			}
			else {
				ScoreboardManager.getLobbyScoreboard().update(lp, "tournament-team", tlp.getTeam().getTeamName());
				//System.out.println("updating scoreboard for " + lp.getName());
			}
		}
	}
	
	public void updateStats(String statType, String playerName, String value) {
		if(statType.equals("FINAL_KILL")) {
			addPoints(playerName, 1);
		}
		else if(statType.equals("DESTROY_EGG")) {
			addPoints(playerName, 2);
		}
		else if(statType.equals("TEAM_WIN")) {
			addPoints(playerName, 6);
			addGamePlayed(playerName);
		}
		else if(statType.equals("TEAM_DEATH")) {
			if(value.equals("2")) {
				addPoints(playerName, 2);
			}
			else if(value.equals("1")) {
				addPoints(playerName, 4);
			}
		}
	}
	
	public void addPoints(String playerName, int value) {
		TournamentTeam team = getTeamFromPlayerName(playerName);
		if(team == null) {
			System.out.println("team null when updating team points");
			return;
		}
		team.setPoints(team.getPoints() + value);
		EggWarsCoreLobby.getPlugin().getLogger().info(CmdColor.YELLOW + "Adding " + value + " points to team " + team.getTeamName() + CmdColor.RESET);
	}
	
	public void addGamePlayed(String playerName) {
		TournamentTeam team = getTeamFromPlayerName(playerName);
		if(team == null) {
			System.out.println("team null when updating group games");
			return;
		}
		TournamentGroup group = team.getTournamentGroup();
		if(group == null) {
			System.out.println("group null when updating group games");
			return;
		}
		group.setGamesPlayed(group.getGamesPlayed() + 1);
		EggWarsCoreLobby.getPlugin().getLogger().info(CmdColor.BLUE + "Adding 1 game played to group " + group.getGroupName()  + ", new value = " + group.getGamesPlayed() +  CmdColor.RESET);
	}
	
	private TournamentTeam getTeamFromPlayerName(String playerName) {
		for(TournamentTeam team : teams.values()) {
			if(team.getMembers().contains(playerName)) {
				return team;
			}
		}
		return null;
	}

	public static TournamentManager getInstance() {
		if(instance == null) {
			instance = new TournamentManager();
		}
		return instance;
	}
	
	public Map<String,TournamentTeam> getTeams() {
		return teams;
	}
	
	public Map<String,TournamentGroup> getGroups() {
		return groups;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public List<String> getAllowedGroups() {
		return allowedGroups;
	}	
	
}
