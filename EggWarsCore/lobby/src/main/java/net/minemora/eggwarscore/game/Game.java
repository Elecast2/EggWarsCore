package net.minemora.eggwarscore.game;

import org.bukkit.entity.Player;

import net.minemora.eggwarscore.bungee.BungeeHandler;
import net.minemora.eggwarscore.network.GamesConnection;

public class Game {
	
	private GamesConnection connection;
	private String mode;
	private int id;
	private int playerCount;
	private boolean inGame;
	private String serverName;
	private boolean restarting;
	private int freeTeams;
	
	public Game(GamesConnection connection, int id, String mode) {
		this.connection = connection;
		this.id = id;
		this.mode = mode;
	}
	
	public void sendPlayer(Player player) {
		BungeeHandler.sendPlayer(player, serverName);
		playerCount++;
	}
	
	public int getMaxPlayers() {
		return GameManager.getMaxPlayers(mode);
	}
	
	public GamesConnection getConnection() {
		return connection;
	}
	
	public void setConnection(GamesConnection connection) {
		this.connection = connection;
	}
	
	public String getMode() {
		return mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getPlayerCount() {
		return playerCount;
	}
	
	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public boolean isRestarting() {
		return restarting;
	}

	public void setRestarting(boolean restarting) {
		this.restarting = restarting;
	}

	public int getFreeTeams() {
		return freeTeams;
	}

	public void setFreeTeams(int freeTeams) {
		this.freeTeams = freeTeams;
	}

}
