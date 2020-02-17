package net.minemora.eggwarscore.game;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scoreboard.Objective;

import net.minemora.eggwarscore.team.Team;
import net.minemora.eggwarscore.team.TeamManager;

public class GameTeam extends Multicast {

	private Team team;
	private boolean eggDestroyed = false;
	private Inventory enderChest = Bukkit.createInventory(null, 27, "Ender Chest"); //TODO LANG Y SIZE CONFIG also if enabled

	public GameTeam(Team team /* TODO los demas constructores */) {
		this.team = team;
	}
	
	public int getAliveCount() {
		int alive = 0;
		for(String playerName : getPlayers()) {
			GamePlayer gp = GamePlayer.get(playerName);
			if(gp==null) {
				continue;
			}
			if(!gp.isDead()) {
				alive++;
			}
		}
		return alive;
	}
	
	public void addPlayer(String playerName) {
		GamePlayer gp = GamePlayer.get(playerName);
		if(gp.getGameTeam() != null) {
			gp.getGameTeam().getPlayers().remove(playerName);
		}
		getPlayers().add(playerName);
		gp.setGameTeam(this);
	}
	
	public void setTag(String playerName) {
		if(GamePlayer.get(playerName) == null) {
			return;
		}
		for(Player player : GamePlayer.get(playerName).getMulticast().getBukkitPlayers()) {
			org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
			if(scoreboard == null) {
				return;
			}
			Objective obj = scoreboard.getObjective(player.getName());
			org.bukkit.scoreboard.Team sbteam;
			if(scoreboard.getTeam(team.getName()) != null) {
				sbteam = scoreboard.getTeam(team.getName());
			}
			else {
				sbteam = scoreboard.registerNewTeam(team.getName());
				sbteam.setAllowFriendlyFire(false);
				sbteam.setCanSeeFriendlyInvisibles(true);
			}
			if(!sbteam.getEntries().contains(playerName)) {
				sbteam.addEntry(playerName);
			}
			sbteam.setPrefix(TeamManager.getTabPrefix(team));
	        obj.getScore(playerName).setScore(-1);
		}
	}
	
	public void removeTag(String playerName) {
		for(Player player : GamePlayer.get(playerName).getMulticast().getBukkitPlayers()) {
			org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
			if(scoreboard == null) {
				return;
			}
			org.bukkit.scoreboard.Team sbteam;
			if(scoreboard.getTeam(team.getName()) == null) {
				return;
			}
			sbteam = scoreboard.getTeam(team.getName());
			if(sbteam.getEntries().contains(playerName)) {
				sbteam.removeEntry(playerName);
			}
		}
	}
	
	public static void showTags(Player player) {
		org.bukkit.scoreboard.Scoreboard scoreboard = player.getScoreboard();
		if(scoreboard == null) {
			return;
		}
		Objective obj = scoreboard.getObjective(player.getName());
		Collection<GameTeam> gameTeams;
		if (GamePlayer.get(player.getName()).getMulticast() instanceof Game) {
			gameTeams = GamePlayer.get(player.getName()).getGame().getGameTeams().values();
		}
		else {
			gameTeams = GamePlayer.get(player.getName()).getGameLobby().getGameTeams().values();
		}
		for(GameTeam gameTeam : gameTeams) {
			if(gameTeam.getPlayers().size() == 0) {
				continue;
			}
			org.bukkit.scoreboard.Team sbteam;
			if(scoreboard.getTeam(gameTeam.getTeam().getName()) != null) {
				sbteam = scoreboard.getTeam(gameTeam.getTeam().getName());
			}
			else {
				sbteam = scoreboard.registerNewTeam(gameTeam.getTeam().getName());
				sbteam.setAllowFriendlyFire(false);
				sbteam.setCanSeeFriendlyInvisibles(true);
			}
			sbteam.setPrefix(TeamManager.getTabPrefix(gameTeam.getTeam()));
			for(String playerName : gameTeam.getPlayers()) {
				sbteam.addEntry(playerName);
		        obj.getScore(playerName).setScore(-1);
			}
		}
	}

	public Team getTeam() {
		return team;
	}
	public boolean isEggDestroyed() {
		return eggDestroyed;
	}

	public void setEggDestroyed(boolean eggDestroyed) {
		this.eggDestroyed = eggDestroyed;
	}

	public Inventory getEnderChest() {
		return enderChest;
	}

	public void setEnderChest(Inventory enderChest) {
		this.enderChest = enderChest;
	}
}