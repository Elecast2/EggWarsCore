package net.minemora.eggwarscore.network;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.utils.CmdColor;

public class TimeoutHandler extends TimerTask {
	
	private Timer timer;

    public TimeoutHandler() {
        this.timer = new Timer();
        timer.schedule(this, 10000, 10000);
    }
    
	@Override
	public void run() {
		Set<GamesConnection> toShutDown = new HashSet<>();
		for(Set<GamesConnection> gconns : GameManager.getGames().values()) {
			for(GamesConnection gconn : gconns) {
				if(gconn.getLastHeartBeat() == 0) {
					continue;
				}
				if(System.currentTimeMillis() - gconn.getLastHeartBeat() > 15000) {
					toShutDown.add(gconn);
					EggWarsCoreLobby.getPlugin().getLogger().severe(CmdColor.RED + "Client " 
							+ gconn.getSocket().getInetAddress().getHostAddress() + " timed out! [" + gconn.getServerName() + "]" 
							+ CmdColor.RESET);
				}
			}
		}
		for(GamesConnection gconn : toShutDown) {
			gconn.shutdown();
		}
		toShutDown.clear();
	}
}