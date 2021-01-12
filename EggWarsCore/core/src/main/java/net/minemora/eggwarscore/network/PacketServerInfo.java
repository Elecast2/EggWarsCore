package net.minemora.eggwarscore.network;

import java.io.PrintWriter;

import net.minemora.eggwarscore.game.GameLobby;
import net.minemora.eggwarscore.game.GameManager;

public class PacketServerInfo extends Packet {

	public PacketServerInfo(PrintWriter out) {
		super(out);
	}

	@Override
	public void send() {
		//index:players:status
		//status: 0 = waiting, 1: ingame, -1 = restarting
		//String mode = ConfigMain.get().getString("network.mode");
		out.println("ServerInfo$" + gamesToString());
		out.flush();
	}
	
	private String gamesToString() {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for(int i : GameManager.getGames().keySet()) {
			GameLobby gameLobby = GameManager.getGames().get(i);
			if(gameLobby.getGame() != null) {
				sb.append(gameLobby.getId() + ":" + gameLobby.getGame().getPlayersCount() + ":1:0");
			}
			else {
				if(GameManager.isSoftRestarting()) {
					sb.append(gameLobby.getId() + ":" + gameLobby.getPlayersCount() + ":2:0");
				}
				else{
					sb.append(gameLobby.getId() + ":" + gameLobby.getPlayersCount() + ":0:" + gameLobby.getFreeTeams());
				}
			}
			count++;
			if(count < GameManager.getGames().size()) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
}