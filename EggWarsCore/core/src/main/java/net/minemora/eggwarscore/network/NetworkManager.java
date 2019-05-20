package net.minemora.eggwarscore.network;

import java.util.HashSet;
import java.util.Set;

import net.minemora.eggwarscore.config.ConfigMain;

public final class NetworkManager {
	
	private static Set<NetworkClient> connections = new HashSet<>();
	
	private NetworkManager() {}
	
	public static void setup() {
		for(String lobby : ConfigMain.get().getConfigurationSection("network.lobbies").getValues(false).keySet()) {
			NetworkClient conn = new NetworkClient(ConfigMain.get().getString("network.lobbies."+lobby+".ip"),
					ConfigMain.get().getInt("network.lobbies."+lobby+".port"));
			conn.start();
			connections.add(conn);
		}
	}
	
	public static void disconnectAll() {
		for(NetworkClient conn : getConnections()) {
			if(conn.getWriter() == null) {
				conn.shutdown();
				continue;
			}
			conn.getWriter().println("Bye");
			conn.getWriter().flush();
			conn.shutdown();
		}
	}

	public static synchronized Set<NetworkClient> getConnections() {
		return connections;
	}
}