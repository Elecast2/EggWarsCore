package net.minemora.eggwarscore.network;

import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.game.Game;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.menu.GamesMenu;
import net.minemora.eggwarscore.utils.CmdColor;

public class GamesConnection extends ClientConnection {
	
	private Map<Integer,Game> games = new HashMap<>();
	private String mode;
	private String serverName = null;
	private long lastHeartBeat = 0;

	public GamesConnection(Socket clientSocket) {
		super(clientSocket);
		EggWarsCoreLobby.getPlugin().getLogger().info(CmdColor.GREEN + "Game Server connected! " + clientSocket.getInetAddress().getHostAddress() + CmdColor.RESET);
	}

	@Override
	public void processInput(String inputLine) {
		if(inputLine == null) {
			return; //TODO shutdown aca?
		}
		if(inputLine.startsWith("ServerInfo")) {
			String[] sections = inputLine.split("\\$");
			String mode = sections[1];
			String[] sGames = sections[2].split(",");
			for(String gameStr : sGames) {
				String[] data = gameStr.split(":");
				int id = Integer.parseInt(data[0]);
				int players = Integer.parseInt(data[1]);
				int status = Integer.parseInt(data[2]);
				if(!getGames().containsKey(id)) {
					getGames().put(id, new Game(this, id, mode));
					getGames().get(id).setServerName(serverName);
				}
				Game game = getGames().get(id);
				game.setPlayerCount(players);
				if(status == 1) {
					if(!game.isInGame()) {
						game.setInGame(true);
					}
				}
				else if(status == 2) {
					if(!game.isRestarting()) {
						game.setRestarting(true);;
					}
					if(game.isInGame()) {
						game.setInGame(false);
					}
				}
				else {
					if(game.isInGame()) {
						game.setInGame(false);
					}
				}
			}
			if(!GameManager.getGames().containsKey(mode)) {
				GameManager.getGames().put(mode, new HashSet<GamesConnection>());
			}
			if(!GameManager.getGamesMenus().containsKey(mode)) {
				GameManager.getGamesMenus().put(mode, new GamesMenu(mode));
			}
			if(!GameManager.getGames().get(mode).contains(this)) {
				this.mode = mode;
				GameManager.getGames().get(mode).add(this);
				GameManager.getGamesMenus().get(mode).update();
			}
			GameManager.getGamesMenus().get(mode).refresh();
		}
		else if(inputLine.equals("HeartBeat")) {
			getWriter().println("HeartBeat");
			getWriter().flush();
			this.lastHeartBeat = System.currentTimeMillis();
		}
		else if(inputLine.startsWith("SendPlayer")) {
			String[] sections = inputLine.split("\\$");
			String playerName = sections[1];
			boolean accept = Boolean.parseBoolean(sections[2]);
			if(GameManager.getSendQueue().containsKey(playerName)) {
				if(GameManager.getQuickPlayersFrom().containsKey(playerName)) {
					if(accept) {
						new PacketQuickGame(GameManager.getQuickPlayersFrom().get(playerName).getWriter(), 
								playerName, GameManager.getSendQueue().get(playerName).getServerName()).send();
					}
					else {
						System.out.println("[quick] game not acepted, maybe full?"); //TODO quitar
					}
					GameManager.getQuickPlayersFrom().remove(playerName);
				}
				else {
					if(Bukkit.getPlayer(playerName) == null) {
						System.out.println("[send] player is null, maybe disconnected?");//TODO QUITAR
						GameManager.getSendQueue().remove(playerName);
						return;
					}
					if(accept) {
						GameManager.getSendQueue().get(playerName).sendPlayer(Bukkit.getPlayer(playerName));
					}
					else {
						System.out.println("game not acepted, maybe full?"); //TODO quitar
					}
				}
				GameManager.getSendQueue().remove(playerName);
			}
		}
		else if(inputLine.startsWith("ServerName")) {
			String[] sections = inputLine.split("\\$");
			this.serverName = sections[1];
			for(Game game : games.values()) {
				game.setServerName(serverName);
			}
			EggWarsCoreLobby.getPlugin().getLogger().info(CmdColor.GREEN + "Server " + serverName + " is ready!" + CmdColor.RESET);
		}
		else if(inputLine.startsWith("QuickGame")) {
			String[] sections = inputLine.split("\\$");
			String playerName = sections[1];
			Game game = GameManager.getQuickGame(mode);
			if(game==null) {
				System.out.println("game null, maybe full?"); //TODO quitar
				return; //TODO ENVIAR PAQUETE DE NO SE ENCONTRO JUEGO INTENTAR DE NUEVO
			}
			GameManager.getQuickPlayersFrom().put(playerName, this);
			GameManager.getSendQueue().put(playerName, game);
			new PacketSendPlayer(game.getConnection().getWriter(), playerName, game.getId()).send();
		}
		
		else if(inputLine.equals("Bye")) {
			shutdown();
			EggWarsCoreLobby.getPlugin().getLogger().info(CmdColor.RED + "Server " + serverName + " disconnected!" + CmdColor.RESET);
		}
	}
	
	@Override
	public void onClose() {
		GameManager.getGames().get(mode).remove(this);
		GameManager.getGamesMenus().get(mode).update();
	}
	
	public static void disconnectAll() {
		Set<GamesConnection> toShutDown = new HashSet<>();
		for(Set<GamesConnection> gconns : GameManager.getGames().values()) {
			for(GamesConnection gconn : gconns) {
				gconn.getWriter().println("Bye");
				gconn.getWriter().flush();
				toShutDown.add(gconn);
			}
		}
		for(GamesConnection gconn : toShutDown) {
			gconn.shutdown();
		}
		toShutDown.clear();
	}

	public Map<Integer,Game> getGames() {
		return games;
	}

	public String getMode() {
		return mode;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public long getLastHeartBeat() {
		return lastHeartBeat;
	}
}
