package net.minemora.eggwarscore.network;

import java.io.PrintWriter;

public abstract class Packet {
	
	protected PrintWriter out;
	
	protected Packet(PrintWriter out) {
		this.out = out;
	}

	public abstract void send();
	
}