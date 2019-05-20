package net.minemora.eggwarscore.network;

import java.io.PrintWriter;

public class PacketSendPlayer extends Packet {
	
	private String playerName;
	private int gameId;

	public PacketSendPlayer(PrintWriter out, String playerName, int gameId) {
		super(out);
		this.playerName = playerName;
		this.gameId = gameId;
	}

	@Override
	public void send() {
		out.println("SendPlayer$" + playerName + "$" + gameId);
		out.flush();
	}
}