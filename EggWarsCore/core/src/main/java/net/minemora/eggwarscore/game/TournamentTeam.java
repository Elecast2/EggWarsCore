package net.minemora.eggwarscore.game;

import java.util.List;

public class TournamentTeam {
	
	private String teamId;
	private String teamName;
	private int gameTeamId;
	private List<String> members;
	
	private int points = 0;
	
	public TournamentTeam(String teamId, int gameTeamId, String teamName, List<String> members) {
		this.teamId = teamId;
		this.gameTeamId = gameTeamId;
		this.teamName = teamName;
		this.members = members;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getTeamName() {
		return teamName;
	}

	public String getTeamId() {
		return teamId;
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}

	public int getGameTeamId() {
		return gameTeamId;
	}

	public void setGameTeamId(int gameTeamId) {
		this.gameTeamId = gameTeamId;
	}

}
