package net.minemora.eggwarscore.network;

import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

public class InfoBeat extends TimerTask {
	
	private PrintWriter out;
	private NetworkClient client;
	private Timer timer;

    public InfoBeat(PrintWriter out, NetworkClient client) {
    	this.out = out;
    	this.client = client;
        this.timer = new Timer();
        timer.schedule(this, 3000, 3000);
    }

	@Override
	public void run() {
		if(client.isDead()) {
			timer.cancel();
			return;
		}
		new PacketServerInfo(out).send();
	}
}