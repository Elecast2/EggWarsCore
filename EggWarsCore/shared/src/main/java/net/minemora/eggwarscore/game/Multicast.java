package net.minemora.eggwarscore.game;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.utils.ChatUtils;

public abstract class Multicast {

	private Set<String> players = new HashSet<>();
	
	public void broadcast(String message) {
		for(Player player: getBukkitPlayers()) {
			player.sendMessage(ChatUtils.format(ConfigLang.get().getString("prefix") + message));
		}
	}
	
	public void broadcast(Player filter, String message) {
		for(Player player: getBukkitPlayers()) {
			if(player.equals(filter)) {
				continue;
			}
			player.sendMessage(ChatUtils.format(ConfigLang.get().getString("prefix") + message));
		}
	}
	
	public void broadcastMessage(String message) {
		for(Player player: getBukkitPlayers()) {
			player.sendMessage(message);
		}
	}
	
	public void playSound(Sound sound, float volume, float pitch) {
		for(Player player: getBukkitPlayers()) {
			player.playSound(player.getLocation(), sound, volume, pitch);
		}
	}
	
	public void sendTitle(String title, String subtitle) {
		for(Player player: getBukkitPlayers()) {
			SharedHandler.getNmsHandler().sendTitleToPlayer(player, 20, 40, 20, title, subtitle);
		}
	}
	
	public Set<Player> getBukkitPlayers() {
		Set<Player> bukkitPlayers = new HashSet<>();
		for(String name : getPlayers()) {
			Player lp = Bukkit.getPlayer(name);
			if(lp != null) {
				bukkitPlayers.add(lp);
			}
		}
		return bukkitPlayers;
	}
	
	public void showPlayer(Player player) {
		for(Player p : getBukkitPlayers()) {
			p.showPlayer(player);
			player.showPlayer(p);
		}
	}
	
	public void revealPlayersToPlayer(Player player) {
		for(Player p : getBukkitPlayers()) {
			player.showPlayer(p);
		}
	}
	
	public int getPlayersCount() {
		return players.size();
	}
	
	public Set<String> getPlayers() {
		return players;
	}
	
	public void setPlayers(Set<String> players) {
		this.players = players;
	}
}