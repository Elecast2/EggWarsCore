package net.minemora.eggwarscore.game;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;

import net.minemora.eggwarscore.network.NetworkClient;
import net.minemora.eggwarscore.network.NetworkManager;
import net.minemora.eggwarscore.network.PacketGameUpdate;

public class TournamentManager {
	
	private static TournamentManager instance;
	
	private Map<String,TournamentTeam> teams = new HashMap<>();

	private TournamentManager() {}
	
	public void setup() {

	}
	
	public TournamentTeam getTeamFromPlayerName(String playerName) {
		for(TournamentTeam team : teams.values()) {
			if(team.getMembers().contains(playerName)) {
				return team;
			}
		}
		return null;
	}
	
	public static void sendGameUpdate(PacketGameUpdate.StatType statType, String playerName, String value) {
		for(NetworkClient conn : NetworkManager.getConnections()) {
			new PacketGameUpdate(conn.getWriter(), statType, playerName, value).send();
		}
		if(Bukkit.getPlayer(playerName) != null) {
			GamePlayer gp = GamePlayer.get(playerName);
			if(gp == null) {
				return;
			}
			if(gp.getGameTeam() == null) {
				return;
			}
			int points = 0;
			if(statType == PacketGameUpdate.StatType.FINAL_KILL) {
				points = 1;
			}
			else if(statType == PacketGameUpdate.StatType.DESTROY_EGG) {
				points = 2;
			}
			else if(statType == PacketGameUpdate.StatType.TEAM_WIN) {
				points = 6;
			}
			else if(statType == PacketGameUpdate.StatType.TEAM_DEATH) {
				if(value.equals("2")) {
					points = 2;
				}
				else if(value.equals("1")) {
					points = 4;
				}
			}
			gp.getGameTeam().broadcast("&f&lTu equipo suma &e&l+&b&l" + points + " &e&lpuntos.");
		}
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
	
	public void setTeams(Map<String,TournamentTeam> teams) {
		this.teams = teams;
	}	
	
}