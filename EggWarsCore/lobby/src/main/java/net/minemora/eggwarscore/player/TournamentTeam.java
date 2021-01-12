package net.minemora.eggwarscore.player;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import net.minemora.eggwarscore.config.ConfigTournament;
import net.minemora.eggwarscore.game.TournamentGroup;
import net.minemora.eggwarscore.game.TournamentManager;

public class TournamentTeam {
	
	private String teamId;
	private String teamName;
	private int gameTeamId;
	private List<String> members;
	private Location signLoc;
	private int points;
	
	/*
	 * TODO
	 * boolean tournamnet en game, solicitar miembros
	 * en reloadteams reenviar miembros a game
	 * enviar paquetes gameupdate
	 * if torunamnet setear automaticamente team
	 * en lobby enviar a partida configurada
	 * opcion de numero de partida para cada grupo y fase
	 */
	
	
	public TournamentTeam(String teamId, int gameTeamId, String teamName, List<String> members, Location signLoc, int points) {
		this.teamId = teamId;
		this.gameTeamId = gameTeamId;
		this.teamName = teamName;
		this.members = members;
		this.signLoc = signLoc;
		this.points = points;
	}
	
	public TournamentGroup getTournamentGroup() {
		for(TournamentGroup group : TournamentManager.getInstance().getGroups().values()) {
			if(group.getTeams().contains(this)) {
				return group;
			}
		}
		return null;
	}
	
	public void addPlayer(String playerName) {
		members.add(playerName);
		ConfigTournament.get().set("teams." + teamId + ".members", members);
		ConfigTournament.getInstance().save();
	}
	
	public void removePlayer(String playerName) {
		members.remove(playerName);
		ConfigTournament.get().set("teams." + teamId + ".members", members);
		ConfigTournament.getInstance().save();
	}
	
	public void updateSign() {
		Block block = signLoc.getBlock();
		if(block.getState() instanceof Sign) {
			Sign sign = (Sign) block.getState();
			int i = 0;
			for(String name : members) {
				if(name.length() > 16) {
					name = name.substring(0, 16);
				}
				sign.setLine(i, name);
				i++;
				if(i>3) {
					break;
				}
			}
			if(i<4) {
				for(int i2 = i; i2<4; i2++) {
					sign.setLine(i2, "");
				}
			}
			sign.update();
		}
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
		ConfigTournament.get().set("teams." + teamId + ".points", points);
		ConfigTournament.getInstance().save();
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

	public Location getSignLoc() {
		return signLoc;
	}

	public void setSignLoc(Location signLoc) {
		this.signLoc = signLoc;
	}

	public int getGameTeamId() {
		return gameTeamId;
	}

	public void setGameTeamId(int gameTeamId) {
		this.gameTeamId = gameTeamId;
	}

}
