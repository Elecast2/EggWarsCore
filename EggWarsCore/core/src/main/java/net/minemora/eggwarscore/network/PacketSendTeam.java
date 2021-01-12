package net.minemora.eggwarscore.network;

import java.io.PrintWriter;

public class PacketSendTeam extends Packet {
	
	private String leader;
	private boolean accept;

	protected PacketSendTeam(PrintWriter out, String leader, boolean accept) {
		super(out);
		this.leader = leader;
		this.accept = accept;
	}

	@Override
	public void send() {
		out.println("SendTeam$" + leader + "$" + accept);
		out.flush();
	}
}