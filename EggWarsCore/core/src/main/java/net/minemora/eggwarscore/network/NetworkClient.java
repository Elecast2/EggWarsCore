package net.minemora.eggwarscore.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.bungee.BungeeHandler;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.game.GameLobby;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.game.GamePlayer;
import net.minemora.eggwarscore.game.TournamentManager;
import net.minemora.eggwarscore.game.TournamentTeam;
import net.minemora.eggwarscore.team.TeamManager;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.CmdColor;

public class NetworkClient extends Thread {
	
	private String host;
	private int port;
	private Socket socket;
	private HeartBeat beat;
	private boolean shutdown;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private String serverName;
	private int sort;
	
	private static Map<String,GameLobby> registeredPlayers = new HashMap<>();
	
	public NetworkClient(String host, int port) {
		this.host = host;
		this.port = port;
		this.serverName = ConfigMain.get().getString("network.server-name");
		this.sort = ConfigMain.get().getInt("network.sort");
	}
	
	public void run() {
		
		shutdown = false;

		try { 
			socket = new Socket(host, port);
			socket.setKeepAlive(true);
			socket.setTcpNoDelay(true);
			out = new PrintWriter(socket.getOutputStream(), true);
		    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    beat = new HeartBeat(out, this);  
		} catch (IOException e) {
			EggWarsCore.getPlugin().getLogger().severe(CmdColor.RED + "Cant reach Lobby " + host + ":" + port + ", trying again..." + CmdColor.RESET);
			try {
				sleep(5000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			this.run();
			return;
		}
		
		EggWarsCore.getPlugin().getLogger().info(CmdColor.GREEN + "Lobby Server connected! " + host + ":" + port + CmdColor.RESET);
		String mode = ConfigMain.get().getString("network.mode");
		new PacketServerName(out, mode, serverName, sort).send();
		new InfoBeat(out, this);
		String inputLine = null;
		
		while (!shutdown) {
			
			try {
				inputLine = in.readLine();
            } catch (IOException e) {
            	break;
            }
			
			processInput(inputLine);
		}
	}
	
	private void processInput(String inputLine) {
		if(inputLine == null) {
			return; //TODO shutdown aca?
		}
		if(inputLine.equals("HeartBeat")) {
			beat.isOk();
		}
		else if(inputLine.startsWith("SendPlayer")) {
			String[] sections = inputLine.split("\\$");
			String playerName = sections[1];
			int gameId = Integer.parseInt(sections[2]);
			if(GameManager.getGames().get(gameId).isFull()) {
				new PacketSendPlayer(out, playerName, false).send();
			}
			else {
				new PacketSendPlayer(out, playerName, true).send();
				getRegisteredPlayers().put(playerName.toLowerCase(), GameManager.getGames().get(gameId));
			}
		}
		else if(inputLine.startsWith("SendTeam")) {
			String[] sections = inputLine.split("\\$");
			String leader = sections[1];
			String[] players = sections[2].split(":");
			int gameId = Integer.parseInt(sections[3]);
			int maxPlayers = TeamManager.getMaxPlayers()*TeamManager.getTeams().size();
			if((maxPlayers - GameManager.getGames().get(gameId).getPlayersCount()) < players.length) {
				new PacketSendTeam(out, leader, false).send();
			}
			else {
				GameLobby gameLobby = GameManager.getGames().get(gameId);
				int teamsNeeded = 1;
				if(players.length > TeamManager.getMaxPlayers()) {
					while(teamsNeeded*TeamManager.getMaxPlayers() < players.length) {
						teamsNeeded++;
					}
				}
				Map<Integer, Set<String>> subTeams = new HashMap<>();
				for(int i = 0; i < teamsNeeded; i++) {
					subTeams.put(i, new HashSet<>());
				}
				int counter = 0;
				for(String playerName : players) {
					getRegisteredPlayers().put(playerName.toLowerCase(), gameLobby);
					Set<String> subTeam = subTeams.get(counter);
					subTeam.add(playerName.toLowerCase());
					if(subTeam.size() == TeamManager.getMaxPlayers()) {
						counter++;
					}
				}
				for(Set<String> subTeam : subTeams.values()) {
					gameLobby.getReservedTeams().put(subTeam, gameLobby.getEmptyGameTeam());
				}
				new PacketSendTeam(out, leader, true).send();
			}
		}
		else if(inputLine.startsWith("QuickGame")) {
			String[] sections = inputLine.split("\\$");
			String playerName = sections[1];
			String serverName = sections[2];
			Player player = Bukkit.getPlayer(playerName);
			if(player == null) {
				System.out.println("[quick] player is null, maybe disconnected?");//TODO QUITAR
				return;
			}
			player.sendMessage(ChatUtils.format("&a¡Partida encontrada!")); //TODO LANG
			player.sendMessage(ChatUtils.format("&7Enviando..."));
			if(serverName.equals(this.serverName)) {
				GamePlayer gp = GamePlayer.get(playerName);
				if(gp==null) {
					System.out.println("gameplayer is null, maybe disconnected?");//TODO QUITAR
					return;
				}
				if(!gp.isDead() && !gp.getGame().isEnding()) {
					return;
				}
				new BukkitRunnable() {
					@Override
					public void run() {
						for(Player lp : Bukkit.getOnlinePlayers()) {
							lp.hidePlayer(player);
							player.hidePlayer(lp);
						}
						gp.loadPlayerFromNetwork(player);
					}
				}.runTask(EggWarsCore.getPlugin());
			}
			else{
				BungeeHandler.sendPlayer(player, serverName);
			}
		}
		else if(inputLine.startsWith("TournamentInfo")) {
			String[] sections = inputLine.split("\\$");
			Map<String,TournamentTeam> teams = new HashMap<>();
			EggWarsCore.getPlugin().getLogger().info(CmdColor.GREEN + "Updating tournament info" + CmdColor.RESET);
			for(int i = 1; i< sections.length; i++) {
				String[] teamInfo = sections[i].split(":");
				String teamId = teamInfo[0];
				int gameTeamId = Integer.valueOf(teamInfo[1]);
				String teamName = teamInfo[2];
				List<String> members;
				if(teamInfo.length == 4) {
					members = Arrays.asList(teamInfo[3].split(","));
					EggWarsCore.getPlugin().getLogger().info(CmdColor.YELLOW + "Adding team " + teamName + " (id=" + teamId + ", gameTeamid=" + gameTeamId + ") with members: [" + teamInfo[3] + "]" + CmdColor.RESET);
				}
				else {
					members = new ArrayList<>();
					EggWarsCore.getPlugin().getLogger().info(CmdColor.YELLOW + "Adding team " + teamName + " (id=" + teamId + ", gameTeamid=" + gameTeamId + ") with no members" + CmdColor.RESET);
				}
				teams.put(teamId, new TournamentTeam(teamId, gameTeamId, teamName, members));
			}
			EggWarsCore.getPlugin().getLogger().info(CmdColor.GREEN + "Tournament info updated" + CmdColor.RESET);
			TournamentManager.getInstance().setTeams(teams);
		}
		else if(inputLine.equals("Bye")) {
			EggWarsCore.getPlugin().getLogger().severe(CmdColor.RED + "Lobby " + host + ":" + port + " disconnected!" + CmdColor.RESET);
			shutdown();
			NetworkManager.getConnections().remove(this);
			NetworkClient client2 = new NetworkClient(getHost(), getPort());
			NetworkManager.getConnections().add(client2);
			client2.start();
			beat.getTimer().cancel();
		}
	}
	
	public boolean isDead() {
		return shutdown;
	}
	
	public PrintWriter getWriter() {
		return out;
	}
	
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
	public void shutdown() {
		this.shutdown = true;
		if(socket == null) {
			EggWarsCore.getPlugin().getLogger().severe("Socket null when shuting down, please report this!");
			return;
		}
		try {
			out.close();
			in.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<String,GameLobby> getRegisteredPlayers() {
		return registeredPlayers;
	}

	public int getSort() {
		return sort;
	}
}