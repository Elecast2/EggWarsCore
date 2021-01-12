package net.minemora.eggwarscore.network;

import java.io.PrintWriter;

import net.minemora.eggwarscore.game.TournamentManager;
import net.minemora.eggwarscore.player.TournamentTeam;

public class PacketTournamentInfo extends Packet {

	public PacketTournamentInfo(PrintWriter out) {
		super(out);
	}

	@Override
	public void send() {
		out.println("TournamentInfo$" + getTournamentInfo());
		out.flush();
	}

	private String getTournamentInfo() {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for(TournamentTeam team : TournamentManager.getInstance().getTeams().values()) {
			sb.append(team.getTeamId() + ":");
			sb.append(team.getGameTeamId() + ":");
			sb.append(team.getTeamName() + ":");
			int count2 = 0;
			for(String member : team.getMembers()) {
				sb.append(member);
				count2++;
				if(count2 < team.getMembers().size()) {
					sb.append(",");
				}
			}
			count++;
			if(count<TournamentManager.getInstance().getTeams().size()) {
				sb.append("$");
			}
		}
		return sb.toString();
	}
}
