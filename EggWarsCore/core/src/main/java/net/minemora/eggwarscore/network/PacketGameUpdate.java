package net.minemora.eggwarscore.network;

import java.io.PrintWriter;

public class PacketGameUpdate extends Packet {
	
	private StatType statType;
	private String value;
	private String playerName;

	public PacketGameUpdate(PrintWriter out, StatType statType, String playerName, String value) {
		super(out);
		this.statType = statType;
		this.value = value;
		this.playerName = playerName;
	}

	@Override
	public void send() {
		out.println("GameUpdate$" + statType.name() + "$" + playerName + "$" + value);
		out.flush();
	}
	
	public enum StatType {
		FINAL_KILL,
		DESTROY_EGG,
		TEAM_WIN,
		TEAM_DEATH;
	}

}
