package net.minemora.eggwarscore.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.bungee.BungeeHandler;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.game.GameLobby;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.game.GamePlayer;
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
	
	private static Map<String,GameLobby> registeredPlayers = new HashMap<>();
	
	public NetworkClient(String host, int port) {
		this.host = host;
		this.port = port;
		this.serverName = ConfigMain.get().getString("network.server-name");
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
		    new InfoBeat(out, this);
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
		
		new PacketServerName(out, serverName).send();
		
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
				NetworkClient.getRegisteredPlayers().put(playerName, GameManager.getGames().get(gameId));
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
		else if(inputLine.equals("Bye")) {
			EggWarsCore.getPlugin().getLogger().severe(CmdColor.RED + "Lobby " + host + ":" + port + " disconnected!" + CmdColor.RESET);
			shutdown();
			new NetworkClient(getHost(), getPort()).start();
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
}