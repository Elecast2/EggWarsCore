package net.minemora.eggwarscore.network;

import java.io.PrintWriter;
import java.util.Set;

public class PacketSendTeam extends Packet {
	
	private Set<String> players;
	private String leader;
	private int gameId;

	public PacketSendTeam(PrintWriter out, String leader, Set<String> players, int gameId) {
		super(out);
		this.players = players;
		this.leader = leader;
		this.gameId = gameId;
	}

	@Override
	public void send() {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for(String playerName : players) {
			sb.append(playerName);
			count++;
			if(count < players.size()) {
				sb.append(":");
			}
		}
		out.println("SendTeam$" + leader + "$" + sb.toString() + "$" + gameId);
		out.flush();
	}
}