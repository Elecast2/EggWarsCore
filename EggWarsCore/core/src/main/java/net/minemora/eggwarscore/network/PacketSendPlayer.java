package net.minemora.eggwarscore.network;

import java.io.PrintWriter;

public class PacketSendPlayer extends Packet {
	
	private String playerName;
	private boolean accept;

	protected PacketSendPlayer(PrintWriter out, String playerName, boolean accept) {
		super(out);
		this.playerName = playerName;
		this.accept = accept;
	}

	@Override
	public void send() {
		out.println("SendPlayer$" + playerName + "$" + accept);
		out.flush();
	}
}