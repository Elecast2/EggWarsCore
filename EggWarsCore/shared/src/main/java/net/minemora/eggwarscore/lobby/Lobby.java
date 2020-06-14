package net.minemora.eggwarscore.lobby;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.menu.ExtrasMenu;
import net.minemora.eggwarscore.utils.ItemAction;

public class Lobby {
	
	private static Lobby lobby;

	private Location spawn;
	private Map<Integer,LobbyItem> lobbyItems = new HashMap<>();
	
	private Lobby() {}
	
	public static Lobby getLobby() {
		if (lobby == null) {
			lobby = new Lobby();
        }
        return lobby;
	}

	public Location getSpawn() {
		return spawn;
	}

	public void setSpawn(Location spawn) {
		this.spawn = spawn;
	}

	public void setup() {
		setSpawn(new Location(Bukkit.getWorlds().get(0), ConfigMain.get().getDouble("lobby.spawn.x"),
				ConfigMain.get().getDouble("lobby.spawn.y"), ConfigMain.get().getDouble("lobby.spawn.z"),
				(float) ConfigMain.get().getDouble("lobby.spawn.yaw"),
				(float) ConfigMain.get().getDouble("lobby.spawn.pitch")));
		for (String id : ConfigMain.get().getConfigurationSection("lobby-items").getValues(false).keySet()) {
			boolean enabled = ConfigMain.get().getBoolean("lobby-items." + id + ".enabled");
			if(enabled) {
				int slot = ConfigMain.get().getInt("lobby-items." + id + ".slot");
				lobbyItems.put(slot, new LobbyItem(id, slot));
				if(id.equals("extras")) {
					lobbyItems.get(slot).setAction(
					new ItemAction() {
						@Override
						public void perform(Player player) {
							ExtrasMenu.getMenu().open(player);
							player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.5f, 1); //TODO FROM CONFIG
						}
					});
				}
				else if(id.equals("kits")) {
					lobbyItems.get(slot).setAction(
					new ItemAction() {
						@Override
						public void perform(Player player) {
							PlayerStats.get(player.getName()).getKitMenu().open(player);
							player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 0.5f, 1); //TODO FROM CONFIG
						}
					});
				}
			}
		}
	}

	public Map<Integer,LobbyItem> getLobbyItems() {
		return lobbyItems;
	}
}