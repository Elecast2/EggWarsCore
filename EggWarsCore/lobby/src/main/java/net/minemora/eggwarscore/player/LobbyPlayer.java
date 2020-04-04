package net.minemora.eggwarscore.player;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.EggWarsCoreLobby;
import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.holographicdisplays.HolographicDisplaysHook;
import net.minemora.eggwarscore.lobby.Lobby;
import net.minemora.eggwarscore.lobby.LobbyItem;
import net.minemora.eggwarscore.menu.DailyRewardsMenu;
import net.minemora.eggwarscore.menu.QuickGameMenu;
import net.minemora.eggwarscore.nms.APlayerHolo;
import net.minemora.eggwarscore.npc.NPC;
import net.minemora.eggwarscore.npc.NPCManager;
import net.minemora.eggwarscore.scoreboard.ScoreboardManager;
import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.utils.ChatUtils;

public class LobbyPlayer extends PlayerStats {
	
	private QuickGameMenu quickGameMenu;
	private DailyRewardsMenu dailyRewardsMenu;
	
	/*
	private Hologram quickGameHolo;
	private TextLine quickGameLine;
	
	private Hologram parkourHolo;
	private TextLine parkourLine;
	*/
	
	private APlayerHolo quickGameHolo;
	private APlayerHolo parkourHolo;
	
	private boolean timeTrial = false;
	
	private static boolean quickJoinHoloEnabled = false;
	private static boolean parkourHoloEnabled = false;
	private static Location quickJoinHoloLocation;
	private static Location parkourHoloLocation;
	
	
	public static void setupHolos() {
		NPC npc = NPCManager.getNPC("quick-join");
		if(npc != null) {
			quickJoinHoloEnabled = true;
			quickJoinHoloLocation = npc.getLocation().clone();
		}
		
		npc = NPCManager.getNPC("parkour");
		if(npc != null) {
			parkourHoloEnabled = true;
			parkourHoloLocation = npc.getLocation().clone();
		}
	}

	public LobbyPlayer(Player player) {
		super(player);
		player.setGameMode(GameMode.ADVENTURE);
		player.teleport(Lobby.getLobby().getSpawn());
		player.sendMessage(ChatUtils.format(ConfigLang.get().getStringList("lobby.motd")));
		restore();
	}
	
	@Override
	public void loadPlayer(Player player) {
		for(LobbyItem lobbyItem : Lobby.getLobby().getLobbyItems().values()) {
			player.getInventory().setItem(lobbyItem.getSlot(), lobbyItem.getItem());
		}
		ScoreboardManager.setLobbyScoreboard(player);
		loadNPCs(player);
		if(HolographicDisplaysHook.isEnabled()) {
			loadQuickGameHolo(player);
			loadParkourHolo(player);
		}
	}
	
	private void loadNPCs(Player player) {
		if(NPCManager.getNPCs().isEmpty()) {
			return;
		}
		new BukkitRunnable() {
			@Override
			public void run() {
				for(NPC npc : NPCManager.getNPCs().values()) {
					npc.spawn(player);
				}
			}
		}.runTaskAsynchronously(EggWarsCoreLobby.getPlugin());
	}
	
	private void loadQuickGameHolo(Player player) {
		if(!quickJoinHoloEnabled) {
			return;
		}
		quickGameHolo = SharedHandler.getNmsHandler().createPlayerHolo(player, 
				quickJoinHoloLocation, ChatUtils.format("&7Modo: " + GameManager.getModeDisplayName(getMode())));
	}
	
	private void loadParkourHolo(Player player) {
		if(!parkourHoloEnabled) {
			return;
		}
		String text;
		if(isParkourDone()) {
			if(getParkourTime() == 0) {
				text = ChatUtils.format("&aMarca tu primer tiempo"); //TODO LANG
			}
			else {
				text = ChatUtils.format("&7Tu mejor tiempo: &a" 
    					+ String.format("%02d:%02d", getParkourTime() / 60, getParkourTime() % 60)); //TODO LANG
			}
		}
		else {
			text = ChatUtils.format("&7Pasa el Parkour para desbloquear"); //TODO LANG
		}
		parkourHolo = SharedHandler.getNmsHandler().createPlayerHolo(player, parkourHoloLocation, text);
	}
	
	public void updateParkourLine() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(isParkourDone()) {
					if(getParkourTime() == 0) {
						parkourHolo.updateText(ChatUtils.format("&aMarca tu primer tiempo")); //TODO LANG
					}
					else {
						parkourHolo.updateText(ChatUtils.format("&7Tu mejor tiempo: &a" 
		    					+ String.format("%02d:%02d", getParkourTime() / 60, getParkourTime() % 60))); //TODO LANG
					}
				}
				else {
					parkourHolo.updateText(ChatUtils.format("&7Pasa el Parkour para desbloquear")); //TODO LANG
				}
			}
		}.runTaskAsynchronously(SharedHandler.getPlugin());
	}
	
	public void updateQuickGameLine() {
		new BukkitRunnable() {
			@Override
			public void run() {
				quickGameHolo.updateText(ChatUtils.format("&7Modo: " + GameManager.getModeDisplayName(getMode()))); //TODO LANG
			}
		}.runTaskAsynchronously(SharedHandler.getPlugin());
	}
	
	/*
	private void loadQuickGameHolo(Player player) {
		if(!quickJoinHoloEnabled) {
			return;
		}
		quickGameHolo = HologramsAPI.createHologram(EggWarsCoreLobby.getPlugin(), quickJoinHoloLocation);
		quickGameHolo.appendTextLine(ChatUtils.format("&f&k|&r &a&lPARTIDA RÁPIDA &f&k|")); //TODO LANG
		quickGameLine = quickGameHolo.appendTextLine(ChatUtils.format("&7Modo: " + GameManager.getModeDisplayName(getMode()))); //TODO LANG
		VisibilityManager visibilityManager = quickGameHolo.getVisibilityManager();
		visibilityManager.showTo(player);
		visibilityManager.setVisibleByDefault(false);
	}
	
	private void loadParkourHolo(Player player) {
		if(!parkourHoloEnabled) {
			return;
		}
		parkourHolo = HologramsAPI.createHologram(EggWarsCoreLobby.getPlugin(), parkourHoloLocation);
		parkourHolo.appendTextLine(ChatUtils.format("&f&k|&r &6&lCONTRARELOJ &f&k|")); //TODO LANG
		if(isParkourDone()) {
			if(getParkourTime() == 0) {
				parkourLine = parkourHolo.appendTextLine(ChatUtils.format("&aMarca tu primer tiempo")); //TODO LANG
			}
			else {
				parkourLine = parkourHolo.appendTextLine(ChatUtils.format("&7Tu mejor tiempo: &a" 
    					+ String.format("%02d:%02d", getParkourTime() / 60, getParkourTime() % 60))); //TODO LANG
			}
		}
		else {
			parkourLine = parkourHolo.appendTextLine(ChatUtils.format("&7Pasa el Parkour para desbloquear")); //TODO LANG
		}
		
		
		VisibilityManager visibilityManager = parkourHolo.getVisibilityManager();
		visibilityManager.showTo(player);
		visibilityManager.setVisibleByDefault(false);
	}
	
	public void updateParkourLine() {
		if(isParkourDone()) {
			if(getParkourTime() == 0) {
				parkourLine.setText(ChatUtils.format("&aMarca tu primer tiempo")); //TODO LANG
			}
			else {
				parkourLine.setText(ChatUtils.format("&7Tu mejor tiempo: &a" 
    					+ String.format("%02d:%02d", getParkourTime() / 60, getParkourTime() % 60))); //TODO LANG
			}
		}
		else {
			parkourLine.setText(ChatUtils.format("&7Pasa el Parkour para desbloquear")); //TODO LANG
		}
	}
	
	public void updateQuickGameLine() {
		quickGameLine.setText(ChatUtils.format("&7Modo: " + GameManager.getModeDisplayName(getMode()))); //TODO LANG
	}
	*/
	
	public void restore() {
		Player player = Bukkit.getPlayer(getPlayerName());
		if(player == null) {
			return;
		}
		player.setFoodLevel(20);
		player.setHealth(player.getMaxHealth());
		player.setFireTicks(0);
		player.getInventory().clear();
		player.getInventory().setHelmet(null);
		player.getInventory().setChestplate(null);
		player.getInventory().setLeggings(null);
		player.getInventory().setBoots(null);
		for (PotionEffect pe : player.getActivePotionEffects()) {
			player.removePotionEffect(pe.getType());
		}
	}
	
	public void remove() {
		if(quickGameMenu != null) {
			HandlerList.unregisterAll(quickGameMenu);
		}
		if(dailyRewardsMenu != null) {
			HandlerList.unregisterAll(dailyRewardsMenu);
		}
		/*
		if(quickGameHolo != null) {
			quickGameHolo.delete();
		}
		if(parkourHolo != null) {
			parkourHolo.delete();
		}
		*/
		removePlayer();
	}
	
	public static LobbyPlayer get(String name) {
		return (LobbyPlayer) PlayerStats.get(name);
	}

	@Override
	public void updateMoneySB(Player player) {
		ScoreboardManager.getLobbyScoreboard().update(player, "money", String.valueOf(getMoney()));
	}

	public QuickGameMenu getQuickGameMenu() {
		if(quickGameMenu == null) {
			quickGameMenu = new QuickGameMenu(this);
		}
		return quickGameMenu;
	}

	public DailyRewardsMenu getDailyRewardsMenu() {
		if(dailyRewardsMenu == null) {
			dailyRewardsMenu = new DailyRewardsMenu(this);
		}
		return dailyRewardsMenu;
	}

	/*
	public Hologram getQuickGameHolo() {
		return quickGameHolo;
	}

	public TextLine getQuickGameLine() {
		return quickGameLine;
	}
	*/

	public boolean isTimeTrial() {
		return timeTrial;
	}

	public void setTimeTrial(boolean timeTrial) {
		this.timeTrial = timeTrial;
	}
}