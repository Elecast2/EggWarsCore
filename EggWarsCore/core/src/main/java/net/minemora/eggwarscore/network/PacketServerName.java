package net.minemora.eggwarscore.network;

import java.io.PrintWriter;

public class PacketServerName extends Packet {
	
	private String mode;
	private String serverName;
	private int sort;

	protected PacketServerName(PrintWriter out, String mode, String serverName, int sort) {
		super(out);
		this.mode = mode;
		this.serverName = serverName;
		this.sort = sort;
	}

	@Override
	public void send() {
		out.println("ServerName$" + mode + "$" + serverName + "$" + sort);
		out.flush();
	}

}
