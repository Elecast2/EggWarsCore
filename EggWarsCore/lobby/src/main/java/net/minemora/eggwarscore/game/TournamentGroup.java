package net.minemora.eggwarscore.game;

import java.util.List;

import net.minemora.eggwarscore.config.ConfigTournament;
import net.minemora.eggwarscore.player.TournamentTeam;

public class TournamentGroup {
	
	private String groupId;
	private String groupName;
	private int gameId;
	private List<TournamentTeam> teams;
	private int gamesPlayed;
	
	public TournamentGroup(String groupId, String groupName, int gameId, List<TournamentTeam> teams, int gamesPlayed) {
		this.groupId = groupId;
		this.groupName = groupName;
		this.gameId = gameId;
		this.teams = teams;
		this.gamesPlayed = gamesPlayed;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public List<TournamentTeam> getTeams() {
		return teams;
	}

	public void setTeams(List<TournamentTeam> teams) {
		this.teams = teams;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getGamesPlayed() {
		return gamesPlayed;
	}

	public void setGamesPlayed(int gamesPlayed) {
		this.gamesPlayed = gamesPlayed;
		ConfigTournament.get().set("groups." + groupId + ".games-played", gamesPlayed);
		ConfigTournament.getInstance().save();
	}

}
