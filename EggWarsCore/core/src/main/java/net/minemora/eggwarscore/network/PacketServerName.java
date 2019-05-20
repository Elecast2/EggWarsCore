package net.minemora.eggwarscore.network;

import java.io.PrintWriter;

public class PacketServerName extends Packet {
	
	private String serverName;

	protected PacketServerName(PrintWriter out, String serverName) {
		super(out);
		this.serverName = serverName;
	}

	@Override
	public void send() {
		out.println("ServerName$" + serverName);
		out.flush();
	}

}
