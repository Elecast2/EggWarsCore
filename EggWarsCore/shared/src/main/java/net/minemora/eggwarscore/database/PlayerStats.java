package net.minemora.eggwarscore.database;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.extras.Extra;
import net.minemora.eggwarscore.extras.ExtraManager;
import net.minemora.eggwarscore.extras.UnlockType;
import net.minemora.eggwarscore.extras.deatheffect.DeathEffect;
import net.minemora.eggwarscore.extras.deatheffect.DeathEffectManager;
import net.minemora.eggwarscore.extras.kit.Kit;
import net.minemora.eggwarscore.extras.kit.KitManager;
import net.minemora.eggwarscore.extras.trail.Trail;
import net.minemora.eggwarscore.extras.trail.TrailManager;
import net.minemora.eggwarscore.extras.wineffect.WinEffect;
import net.minemora.eggwarscore.extras.wineffect.WinEffectManager;
import net.minemora.eggwarscore.menu.BuyMenu;
import net.minemora.eggwarscore.menu.extras.DeathEffectMenu;
import net.minemora.eggwarscore.menu.extras.ExtraMenu;
import net.minemora.eggwarscore.menu.extras.KitMenu;
import net.minemora.eggwarscore.menu.extras.TrailMenu;
import net.minemora.eggwarscore.menu.extras.WinEffectMenu;
import net.minemora.eggwarscore.scoreboard.Scoreboard;
import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.shared.VaultManager;
import net.minemora.eggwarscore.utils.ChatUtils;

public abstract class PlayerStats {
	
	private static Map<String, PlayerStats> playersStats = new HashMap<>();
	
	public static final int LEVEL_RATE = 10; //TODO FROM CONFIG
	
	private String playerName;
	private int databaseSets = 0;
	private boolean loaded = false;
	private int mode;
	
	private boolean dailyRewardsLoaded = false;
	private int dailyReward;
	private int weeklyReward;
	private int monthlyReward;
	private boolean youtube;
	private boolean facebook;
	private boolean twitter;
	
	private int expMultiplier = 1;
	
	private int kills;
	private int deaths;
	private int wins;
	private int money;
	private int exp;
	private int usedChestKeys;
	private int destroyedEggs;
	
	private boolean parkourDone;
	private int parkourTime;
	
	private Trail trail;
	private TrailMenu trailMenu;
	private Set<Trail> trails = new HashSet<>();;
	private boolean randomTrail;
	
	private Kit kit;
	private KitMenu kitMenu;
	private Set<Kit> kits = new HashSet<>();;
	private boolean randomKit;
	
	private DeathEffect deathEffect;
	private DeathEffectMenu deathEffectMenu;
	private Set<DeathEffect> deathEffects = new HashSet<>();;
	private boolean randomDeathEffect;
	
	private WinEffect winEffect;
	private WinEffectMenu winEffectMenu;
	private Set<WinEffect> winEffects = new HashSet<>();;
	private boolean randomWinEffect;
	
	protected PlayerStats(Player player) {
		this.playerName = player.getName();
		PlayerStats ps = this;
		new BukkitRunnable() {
			@Override
			public void run() {
				Database.getDatabase().loadStats(ps);
				new BukkitRunnable() {
					@Override
					public void run() {
						getPlayersStats().put(player.getName(), ps);
						loadExtras(player);
						loadMultipliers(player);
						updateExpBar(player);
						ps.loadPlayer(player);
					}
				}.runTask(SharedHandler.getPlugin());
			}
		}.runTaskAsynchronously(SharedHandler.getPlugin());
	}
	
	public void removePlayer() {
		getPlayersStats().remove(getPlayerName());
		Scoreboard.getPlaceholdersCache().remove(getPlayerName());
		if(trailMenu != null) {
			HandlerList.unregisterAll(trailMenu);
		}
		if(kitMenu != null) {
			HandlerList.unregisterAll(kitMenu);
		}
		if(deathEffectMenu != null) {
			HandlerList.unregisterAll(deathEffectMenu);
		}
		if(winEffectMenu != null) {
			HandlerList.unregisterAll(winEffectMenu);
		}
		if(BuyMenu.getInstance().getQuery().containsKey(getPlayerName())) {
			BuyMenu.getInstance().getQuery().remove(getPlayerName());
		}
	}
	
	public void updateMenu(Extra extra) {
		if(extra instanceof Trail) {
			updateMenu(TrailMenu.class);
		}
		if(extra instanceof Kit) {
			updateMenu(KitMenu.class);
		}
		if(extra instanceof DeathEffect) {
			updateMenu(DeathEffectMenu.class);
		}
		if(extra instanceof WinEffect) {
			updateMenu(WinEffectMenu.class);
		}
		return;
	}
	
	private void updateMenu(Class<? extends ExtraMenu> clazz) {
		if(clazz.equals(KitMenu.class)) {
			if(kitMenu != null) {
				HandlerList.unregisterAll(kitMenu);
				kitMenu = null;
			}
		}
		else if(clazz.equals(TrailMenu.class)) {
			if(trailMenu != null) {
				HandlerList.unregisterAll(trailMenu);
				trailMenu = null;
			}
		}
		else if(clazz.equals(DeathEffectMenu.class)) {
			if(deathEffectMenu != null) {
				HandlerList.unregisterAll(deathEffectMenu);
				deathEffectMenu = null;
			}
		}
		else if(clazz.equals(WinEffectMenu.class)) {
			if(winEffectMenu != null) {
				HandlerList.unregisterAll(winEffectMenu);
				winEffectMenu = null;
			}
		}
	}
	
	public abstract void loadPlayer(Player player);
	
	public void addExtra(Extra extra) {
		if(extra instanceof Kit) {
			if(kits.contains((Kit)extra)) {
				return;
			}
			kits.add((Kit)extra);
		}
		else if(extra instanceof DeathEffect) {
			if(deathEffects.contains((DeathEffect)extra)) {
				return;
			}
			deathEffects.add((DeathEffect)extra);
		}
		else if(extra instanceof Trail) {
			if(trails.contains((Trail)extra)) {
				return;
			}
			trails.add((Trail)extra);
		}
		else if(extra instanceof WinEffect) {
			if(winEffects.contains((WinEffect)extra)) {
				return;
			}
			winEffects.add((WinEffect)extra);
		}
	}

	private void loadExtras(Player player) {
		for(ExtraManager manager : ExtraManager.getManagers()) {
			for(Extra extra : manager.getExtras().values()) {
				if(extra.getUnlockType() == UnlockType.LEVEL) {
					if(getLevel() >= extra.getLevel()) {
						addExtra(extra);
						continue;
					}
				}
				else if(extra.getUnlockType() == UnlockType.KEY) {
					if(getUsedChestKeys() >= extra.getLevel()) {
						addExtra(extra);
						continue;
					}
				}
				else if(extra.getUnlockType() == UnlockType.DEFAULT) {
					addExtra(extra);
					continue;
				}
			}
			for(String permission : manager.getPermissionExtras().keySet()) {
				if(VaultManager.hasPermission(player, permission)) {
					for(Extra extra : manager.getPermissionExtras().get(permission)) {
						addExtra(extra);
					}
				}
			}
		}
	}
	
	public void updateKeyExtras(Player player) {
		for(ExtraManager manager : ExtraManager.getManagers()) {
			for(Extra extra : manager.getExtras().values()) {
				if(extra.getUnlockType() == UnlockType.KEY) {
					if(getUsedChestKeys() >= extra.getLevel()) {
						addExtra(extra);
						updateMenu(extra);
						continue;
					}
				}
			}
		}
	}
	
	public void updateLevelExtras(Player player) {
		for(ExtraManager manager : ExtraManager.getManagers()) {
			for(Extra extra : manager.getExtras().values()) {
				if(extra.getUnlockType() == UnlockType.LEVEL) {
					if(getLevel() >= extra.getLevel()) {
						addExtra(extra);
						continue;
					}
				}
			}
		}
	}
	
	private void loadMultipliers(Player player) {
		if(VaultManager.hasPermission(player, "ewc.boost.exp.4")) {
			setExpMultiplier(4);
		}
		else if(VaultManager.hasPermission(player, "ewc.boost.exp.3")) {
			setExpMultiplier(3);
		}
		else if(VaultManager.hasPermission(player, "ewc.boost.exp.2")) {
			setExpMultiplier(2);
		}
	}
	
	public void buyExtra(Extra extra) {
		if(extra.getUnlockType() != UnlockType.PRICE) {
			return;
		}
		Player player = getPlayer();
		if(player == null) {
			return;
		}
		if(getMoney() < extra.getPrice()) {
			return;
		}
		addExtra(extra);
		Database.getDatabase().addExtra(player.getUniqueId(), extra);
		takeMoney(extra.getPrice());
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1.5f); //TODO configurable
		updateMenu(extra);
	}
	
	public boolean hasExtraEquiped(Extra extra) {
		if(extra instanceof Trail) {
			if(getTrail() == null) {
				return false;
			}
			if(getTrail().equals((Trail)extra)) {
				return true;
			}
			return false;
		}
		if(extra instanceof Kit) {
			if(getKit() == null) {
				return false;
			}
			if(getKit().equals((Kit)extra)) {
				return true;
			}
			return false;
		}
		if(extra instanceof DeathEffect) {
			if(getDeathEffect() == null) {
				return false;
			}
			if(getDeathEffect().equals((DeathEffect)extra)) {
				return true;
			}
			return false;
		}
		if(extra instanceof WinEffect) {
			if(getWinEffect() == null) {
				return false;
			}
			if(getWinEffect().equals((WinEffect)extra)) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public boolean hasExtra(Extra extra) {
		if(extra instanceof Trail) {
			if(getTrails().contains((Trail)extra)) {
				return true;
			}
			return false;
		}
		if(extra instanceof Kit) {
			if(getKits().contains((Kit)extra)) {
				return true;
			}
			return false;
		}
		if(extra instanceof DeathEffect) {
			if(getDeathEffects().contains((DeathEffect)extra)) {
				return true;
			}
			return false;
		}
		if(extra instanceof WinEffect) {
			if(getWinEffects().contains((WinEffect)extra)) {
				return true;
			}
			return false;
		}
		return false;
	}
	
	public ExtraMenu getExtraMenu(Extra extra) {
		if(extra instanceof Trail) {
			return getTrailMenu();
		}
		if(extra instanceof Kit) {
			return getKitMenu();
		}
		if(extra instanceof DeathEffect) {
			return getDeathEffectMenu();
		}
		if(extra instanceof WinEffect) {
			return getWinEffectMenu();
		}
		return null;
	}
	
	public void setTrail(int trail) {
		if(trail == -1) {
			setTrail(null);
			setRandomTrail(true);
		}
		else if (trail == 0) {
			setTrail(null);
			setRandomTrail(false);
		}
		else {
			setTrail((Trail)TrailManager.getInstance().getExtras().get(trail));
		}
	}
	
	public void setKit(int kit) {
		if(kit == -1) {
			setKit(null);
			setRandomKit(true);
		}
		else if (kit == 0) {
			setKit(null);
			setRandomKit(false);
		}
		else {
			setKit((Kit)KitManager.getInstance().getExtras().get(kit));
		}
	}
	
	public void setDeathEffect(int deathEffect) {
		if(deathEffect == -1) {
			setDeathEffect(null);
			setRandomDeathEffect(true);
		}
		else if (deathEffect == 0) {
			setDeathEffect(null);
			setRandomDeathEffect(false);
		}
		else {
			setDeathEffect((DeathEffect)DeathEffectManager.getInstance().getExtras().get(deathEffect));
		}
	}
	
	public void setWinEffect(int winEffect) {
		if(winEffect == -1) {
			setWinEffect(null);
			setRandomWinEffect(true);
		}
		else if (winEffect == 0) {
			setWinEffect(null);
			setRandomWinEffect(false);
		}
		else {
			setWinEffect((WinEffect)WinEffectManager.getInstance().getExtras().get(winEffect));
		}
	}
	
	public TrailMenu getTrailMenu() {
		if(trailMenu == null) {
			trailMenu = new TrailMenu(this);
		}
		return trailMenu;
	}
	
	public KitMenu getKitMenu() {
		if(kitMenu == null) {
			kitMenu = new KitMenu(this);
		}
		return kitMenu;
	}
	
	public DeathEffectMenu getDeathEffectMenu() {
		if(deathEffectMenu == null) {
			deathEffectMenu = new DeathEffectMenu(this);
		}
		return deathEffectMenu;
	}

	public WinEffectMenu getWinEffectMenu() {
		if(winEffectMenu == null) {
			winEffectMenu = new WinEffectMenu(this);
		}
		return winEffectMenu;
	}
	
	public void addUsedChestKey() {
		setUsedChestKeys(getUsedChestKeys() +1);
		Database.add(Stat.CHEST_KEYS, getPlayer(), 1);
	}
	
	public void addMoney(int money) {
		setMoney(getMoney() + money);
		Database.add(Stat.MONEY, getPlayer(), money);
		Player player = getPlayer();
		if(player!=null) {
			updateMoneySB(player);
			player.sendMessage(ChatUtils.format("&a+" + money + " Moras")); //TODO LANG
		}
	}
	
	public void takeMoney(int money) {
		setMoney(getMoney() - money);
		Database.subtract(Stat.MONEY, getPlayer(), money);
		Player player = getPlayer();
		if(player!=null) {
			updateMoneySB(player);
			player.sendMessage(ChatUtils.format("&c-" + money + " Moras")); //TODO LANG
		}
	}
	
	public abstract void updateMoneySB(Player player);
	
	public void addExp(int exp) {
		Player player = getPlayer();
		if(player == null) {
			return;
		}
		exp = exp*getExpMultiplier();
		Database.add(Stat.EXP, player, exp);
		int preLevel = getLevel();
		setExp(getExp() + exp);
		int postLevel = getLevel();
		if(postLevel > preLevel) {
			String[] levelup = {
					"",
					"&7&m--------------------------------------------------",
					"",
					"                 &a&lFELICIDADES!",
					"         &fHas subido de nivel a &6&lNivel " + getLevel() + " &f!",
					"                &5+1 Llave Secreta",
					"",
					"&7&m--------------------------------------------------"
			}; //TODO LANG
			player.sendMessage(ChatUtils.format(levelup));
			player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
			SharedHandler.getNmsHandler().sendTitleToPlayer(player, 20, 40, 20, 
					ChatUtils.format("&a&lNUEVO NIVEL"), ChatUtils.format("&7Has subido a nivel &6" + getLevel()));
		}
		else {
			player.sendMessage(ChatUtils.format("&7(&9"+(getExpOfLevel(getLevel()+1)-getExpForNextLevel())
					+"&7/&b"+getExpOfLevel(getLevel()+1)+"&7) &epara el siguiente nivel"));
			SharedHandler.getNmsHandler().sendTitleToPlayer(player, 20, 40, 20, "", 
					ChatUtils.format("&d+" + (exp) + " exp" 
					+ ((getExpMultiplier() > 1) ? " &7(&bBoost &ax" + getExpMultiplier() + "&7)" : ""))); //TODO LANG
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1); //TODO from config
		}
		updateExpBar(player);
		updateLevelExtras(player);
	}
	
	public void updateExpBar(Player player) {
		if(player==null) {
			return;
		}
		float percent;
		if((getExpOfLevel(getLevel()+1)-getExpForNextLevel()) <= 0) {
			percent = 0;
		}
		else {
			percent = ((float)(getExpOfLevel(getLevel()+1)-getExpForNextLevel()))/getExpOfLevel(getLevel()+1);
		}
		player.setLevel(getLevel());
		player.setExp(percent);
	}
	
	public int getLevel() {
		return (int)(Math.sqrt((LEVEL_RATE*LEVEL_RATE)+(4*LEVEL_RATE)*getExp())-LEVEL_RATE)/(2*LEVEL_RATE);
	}
	
	public int getExpOfLevel(int level) {
		return getExpForLevel(level) - getExpForLevel(level-1);
	}
	
	public int getExpForNextLevel() {
		return (getExpForLevel(getLevel()+1) - getExp());
	}
	
	public static int getExpForLevel(int level) {
		return LEVEL_RATE*level*(1+level);
	}
	
	public static PlayerStats get(String playerName) {
		return playersStats.get(playerName);
	}

	public static Map<String, PlayerStats> getPlayersStats() {
		return playersStats;
	}

	public static void setPlayersStats(Map<String, PlayerStats> playersStats) {
		PlayerStats.playersStats = playersStats;
	}
	
	public int getChestKeys() {
		return getLevel() - usedChestKeys;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public Player getPlayer() {
		return Bukkit.getPlayer(playerName);
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}
	
	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getDeaths() {
		return deaths;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public int getWins() {
		return wins;
	}

	public void setWins(int wins) {
		this.wins = wins;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}
	
	public int getDestroyedEggs() {
		return destroyedEggs;
	}

	public void setDestroyedEggs(int destroyedEggs) {
		this.destroyedEggs = destroyedEggs;
	}

	public int getDatabaseSets() {
		return databaseSets;
	}

	public void setDatabaseSets(int databaseSets) {
		this.databaseSets = databaseSets;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
	
	public Trail getTrail() {
		return trail;
	}

	public void setTrail(Trail trail) {
		this.trail = trail;
	}

	public Set<Trail> getTrails() {
		return trails;
	}

	public boolean isRandomTrail() {
		return randomTrail;
	}

	public void setRandomTrail(boolean randomTrail) {
		this.randomTrail = randomTrail;
	}

	public Kit getKit() {
		return kit;
	}

	public void setKit(Kit kit) {
		this.kit = kit;
	}

	public Set<Kit> getKits() {
		return kits;
	}

	public void setKits(Set<Kit> kits) {
		this.kits = kits;
	}

	public boolean isRandomKit() {
		return randomKit;
	}

	public void setRandomKit(boolean randomKit) {
		this.randomKit = randomKit;
	}

	public DeathEffect getDeathEffect() {
		return deathEffect;
	}

	public void setDeathEffect(DeathEffect deathEffect) {
		this.deathEffect = deathEffect;
	}
	
	public Set<DeathEffect> getDeathEffects() {
		return deathEffects;
	}

	public void setDeathEffects(Set<DeathEffect> deathEffects) {
		this.deathEffects = deathEffects;
	}

	public boolean isRandomDeathEffect() {
		return randomDeathEffect;
	}

	public void setRandomDeathEffect(boolean randomDeathEffect) {
		this.randomDeathEffect = randomDeathEffect;
	}

	public WinEffect getWinEffect() {
		return winEffect;
	}

	public void setWinEffect(WinEffect winEffect) {
		this.winEffect = winEffect;
	}

	public Set<WinEffect> getWinEffects() {
		return winEffects;
	}

	public void setWinEffects(Set<WinEffect> winEffects) {
		this.winEffects = winEffects;
	}

	public boolean isRandomWinEffect() {
		return randomWinEffect;
	}

	public void setRandomWinEffect(boolean randomWinEffect) {
		this.randomWinEffect = randomWinEffect;
	}

	public int getExpMultiplier() {
		return expMultiplier;
	}

	public void setExpMultiplier(int expMultiplier) {
		this.expMultiplier = expMultiplier;
	}

	public int getUsedChestKeys() {
		return usedChestKeys;
	}

	public void setUsedChestKeys(int usedChestKeys) {
		this.usedChestKeys = usedChestKeys;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public boolean isDailyRewardsLoaded() {
		return dailyRewardsLoaded;
	}

	public void setDailyRewardsLoaded(boolean dailyRewardsLoaded) {
		this.dailyRewardsLoaded = dailyRewardsLoaded;
	}

	public int getDailyReward() {
		return dailyReward;
	}

	public void setDailyReward(int dailyReward) {
		this.dailyReward = dailyReward;
	}

	public int getWeeklyReward() {
		return weeklyReward;
	}

	public void setWeeklyReward(int weeklyReward) {
		this.weeklyReward = weeklyReward;
	}

	public int getMonthlyReward() {
		return monthlyReward;
	}

	public void setMonthlyReward(int monthlyReward) {
		this.monthlyReward = monthlyReward;
	}

	public boolean isYoutube() {
		return youtube;
	}

	public void setYoutube(boolean youtube) {
		this.youtube = youtube;
	}

	public boolean isFacebook() {
		return facebook;
	}

	public void setFacebook(boolean facebook) {
		this.facebook = facebook;
	}

	public boolean isTwitter() {
		return twitter;
	}

	public void setTwitter(boolean twitter) {
		this.twitter = twitter;
	}

	public boolean isParkourDone() {
		return parkourDone;
	}

	public void setParkourDone(boolean parkourDone) {
		this.parkourDone = parkourDone;
	}

	public int getParkourTime() {
		return parkourTime;
	}

	public void setParkourTime(int parkourTime) {
		this.parkourTime = parkourTime;
	}

}
