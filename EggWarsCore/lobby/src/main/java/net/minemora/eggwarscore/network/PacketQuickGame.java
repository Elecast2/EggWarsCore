package net.minemora.eggwarscore.network;

import java.io.PrintWriter;

public class PacketQuickGame extends Packet {
	
	private String playerName;
	private String serverName;
	

	protected PacketQuickGame(PrintWriter out, String playerName, String serverName) {
		super(out);
		this.playerName = playerName;
		this.serverName = serverName;
	}

	@Override
	public void send() {
		out.println("QuickGame$" + playerName + "$" + serverName);
		out.flush();
	}
}