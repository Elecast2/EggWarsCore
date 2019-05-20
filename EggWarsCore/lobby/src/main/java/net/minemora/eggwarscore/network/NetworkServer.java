package net.minemora.eggwarscore.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.utils.CmdColor;

public final class NetworkServer extends Thread {
	
	private static NetworkServer instance;
	
	private ServerSocket serverSocket;
	private final int port;
	private TimeoutHandler toh;
	private boolean shutdown = false;
	
	private NetworkServer() {
		this(ConfigMain.get().getInt("network.port"));
	}
	
	private NetworkServer(int port) {
		this.port = port;
		this.toh = new TimeoutHandler();
	}
	
	public void run() {
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			EggWarsCoreLobby.getPlugin().getLogger().severe(CmdColor.RED + "Can't initialize network connection!" + CmdColor.RESET);
			EggWarsCoreLobby.getPlugin().getServer().getPluginManager().disablePlugin(EggWarsCoreLobby.getPlugin());
			e.printStackTrace();
			return;
		}
		while(!shutdown) {
			Socket socket = null;
            try {
                socket = serverSocket.accept();
                socket.setKeepAlive(true);
                socket.setTcpNoDelay(true);
            } catch (IOException e) {
                break;
            }
            new GamesConnection(socket).start();
        }
	}
	
	public static void setup() {
		getInstance().start();
	}
	
	public static NetworkServer getInstance() {
		if (instance == null) {
            instance = new NetworkServer();
        }
        return instance;
	}
	
	public static void shutdown() {
		getInstance().getTimeoutHandler().cancel();
		try {
			getInstance().getServerSocket().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		getInstance().setShutdown(true);
		GamesConnection.disconnectAll();
	}
	
	public void setShutdown(boolean shutdown) {
		this.shutdown = shutdown;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	public int getPort() {
		return port;
	}

	public TimeoutHandler getTimeoutHandler() {
		return toh;
	}
}