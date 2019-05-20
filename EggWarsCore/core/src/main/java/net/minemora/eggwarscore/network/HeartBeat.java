package net.minemora.eggwarscore.network;

import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import net.minemora.eggwarscore.EggWarsCore;
import net.minemora.eggwarscore.utils.CmdColor;

public class HeartBeat extends TimerTask {
	
	private PrintWriter out;
	private NetworkClient client;
	private Timer timer;
	private int count = 0;

    public HeartBeat(PrintWriter out, NetworkClient client) {
    	this.out = out;
    	this.client = client;
        this.timer = new Timer();
        timer.schedule(this, 10000, 10000);
    }

	@Override
	public void run() {
		if(client.isDead()) {
			timer.cancel();
			return;
		}
		if(count == 3) {
			EggWarsCore.getPlugin().getLogger().severe(CmdColor.RED 
					+ "Lobby connection time out for " + client.getHost() + ":" + client.getPort() + ", trying again..." + CmdColor.RESET);
			client.shutdown();
			new NetworkClient(client.getHost(), client.getPort()).start();
			timer.cancel();
			return;
		}
		out.println("HeartBeat");
		out.flush();
		count++;
	}
	
	public void isOk() {
		count = 0;
	}
	
	public Timer getTimer() {
		return timer;
	}
}