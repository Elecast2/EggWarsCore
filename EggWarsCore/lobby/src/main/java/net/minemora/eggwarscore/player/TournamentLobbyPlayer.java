package net.minemora.eggwarscore.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.game.TournamentManager;
import net.minemora.eggwarscore.lobby.Lobby;
import net.minemora.eggwarscore.lobby.LobbyItem;
import net.minemora.eggwarscore.scoreboard.ScoreboardManager;
import net.minemora.reportsystem.VaultManager;

public class TournamentLobbyPlayer extends LobbyPlayer {
	
	private TournamentTeam team = null;
	private boolean isStaff = false;

	public TournamentLobbyPlayer(Player player) {
		super(player);
		for(TournamentTeam tteam : TournamentManager.getInstance().getTeams().values()) {
			if(tteam.getMembers().contains(player.getName())) {
				team = tteam;
				break;
			}
		}
		if(team == null) {
			if(!VaultManager.hasPermission(player, "ewc.tournamentstaff")) {
				player.kickPlayer("No tienes permisos para entrar a este lugar.");
			}
			else {
				isStaff = true;
			}
		} else {
			if(!TournamentManager.getInstance().getAllowedGroups().contains(team.getTournamentGroup().getGroupId())) {
				player.kickPlayer("Todavía no es tu turno para participar.");
			}
		}
	}
	
	@Override
	public void loadPlayer(Player player) {
		for(LobbyItem lobbyItem : Lobby.getLobby().getLobbyItems().values()) {
			player.getInventory().setItem(lobbyItem.getSlot(), lobbyItem.getItem());
		}
		ScoreboardManager.setLobbyScoreboard(player);
		loadNPCs(player);
		new BukkitRunnable() {
			@Override
			public void run() {
				for(Player lp : Bukkit.getOnlinePlayers()) {
					LobbyPlayer lgp = LobbyPlayer.get(lp.getName());
					if(lgp != null) {
						if(lgp.isHidePlayers()) {
							lp.hidePlayer(player);
						}
					}
				}
				if(isHidePlayers()) {
		    		for(Player lp : Bukkit.getOnlinePlayers()) {
		    			player.hidePlayer(lp);
		    		}
		    	}
			}
		}.runTask(EggWarsCoreLobby.getPlugin());
	}
	
	public boolean isPlayer() {
		return team != null;
	}

	public TournamentTeam getTeam() {
		return team;
	}

	public void setTeam(TournamentTeam team) {
		this.team = team;
	}

	public boolean isStaff() {
		return isStaff;
	}

	public void setStaff(boolean isStaff) {
		this.isStaff = isStaff;
	}

}
