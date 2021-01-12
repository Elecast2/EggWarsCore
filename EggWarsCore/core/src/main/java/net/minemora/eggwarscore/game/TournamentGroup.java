package net.minemora.eggwarscore.game;

import java.util.List;

public class TournamentGroup {
	
	private String groupId;
	private String groupName;
	private int gameId;
	private List<TournamentTeam> teams;
	
	private int points = 0;
	
	public TournamentGroup(String groupId, String groupName, int gameId, List<TournamentTeam> teams) {
		this.groupId = groupId;
		this.groupName = groupName;
		this.gameId = gameId;
		this.teams = teams;
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

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

}
