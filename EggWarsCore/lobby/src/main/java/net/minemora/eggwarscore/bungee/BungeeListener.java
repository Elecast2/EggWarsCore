package net.minemora.eggwarscore.bungee;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.minemora.eggwarscore.menu.LobbiesMenu;

public class BungeeListener implements PluginMessageListener {
	
	private static BungeeListener instance;
	
	private String channel;
	
	private BungeeListener() {}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals(channel)) {
			  return;
		}
		try {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();
			
			if (subchannel.equals("PlayerCount")) {
				String server = in.readUTF();
				int playerCount = in.readInt();
				for(int i : LobbiesMenu.getInstance().getLobbies().keySet()) {
					String serverName = LobbiesMenu.getInstance().getLobbies().get(i);
					if(server.equals(serverName)) {
						LobbiesMenu.getInstance().updateItem(i, playerCount);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setup(Plugin plugin, String channel) {
		this.channel = channel;
		plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "RedisBungee", this); //TODO CONFIGURABLE IF REDIS
	}

	public static BungeeListener getInstance() {
		if(instance == null) {
			instance = new BungeeListener();
		}
		return instance;
	}

	public String getChannel() {
		return channel;
	}

}
