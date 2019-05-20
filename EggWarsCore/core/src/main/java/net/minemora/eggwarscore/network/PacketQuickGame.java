package net.minemora.eggwarscore.network;

import java.io.PrintWriter;

public class PacketQuickGame extends Packet {
	
	private String playerName;

	public PacketQuickGame(PrintWriter out, String playerName) {
		super(out);
		this.playerName = playerName;
	}

	@Override
	public void send() {
		out.println("QuickGame$" + playerName);
		out.flush();
	}
}