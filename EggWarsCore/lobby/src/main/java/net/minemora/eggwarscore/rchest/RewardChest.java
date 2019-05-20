package net.minemora.eggwarscore.rchest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import net.minemora.eggwarscore.config.ConfigRewardChest;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.extras.deatheffect.DeathEffectManager;
import net.minemora.eggwarscore.extras.kit.KitManager;
import net.minemora.eggwarscore.extras.trail.TrailManager;
import net.minemora.eggwarscore.extras.wineffect.WinEffectManager;
import net.minemora.eggwarscore.menu.RewardChestMenu;
import net.minemora.eggwarscore.scoreboard.ScoreboardManager;
import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.utils.ChatUtils;

public class RewardChest {
	
	private static RewardChest instance;
	
	private Map<Integer, Reward> rewards = new HashMap<>();
	
	private RewardChestMenu menu;
	
	private RewardChest() {
		this.menu = new RewardChestMenu();
	}
	
	public static void setup() {
		getInstance().loadRewards();
	}
	
	private void loadRewards() {
		Set<String> keys = ConfigRewardChest.get().getConfigurationSection("rewards").getKeys(false);
		for(String key : keys) {
			if(key.contains("-")) {
				String[] range = key.split("-");
				int from = Integer.valueOf(range[0]);
				int to = Integer.valueOf(range[1]);
				for(int i = from; i <= to; i++) {
					rewards.put(i, new Reward(i, "rewards." + key));
				}
			}
			else {
				int id = Integer.valueOf(key);
				rewards.put(id, new Reward(id, "rewards." + id));
			}
		}
	}
	
	public void giveReward(Player player) {
		PlayerStats ps = PlayerStats.get(player.getName());
		if(ps == null) {
			return;
		}
		if(ps.getChestKeys() <= 0) {
			return;
		}
		int rewardId = ps.getUsedChestKeys() +1;
		if(!rewards.containsKey(rewardId)) {
			//TODO LANG SONIDOS ETC
			player.sendMessage(ChatUtils.format("&cNo hay mas premios disponibles por el momento."));
			return;
		}
		ps.addUsedChestKey();
		Reward reward = rewards.get(rewardId);
		if(reward.getType() == RewardType.MONEY) {
			ps.addMoney(reward.getIntValue());
		}
		else if(reward.getType() == RewardType.KIT
				|| reward.getType() == RewardType.WINEFFECT
				|| reward.getType() == RewardType.DEATHEFFECT
				|| reward.getType() == RewardType.TRAIL) {
			ps.updateKeyExtras(player);
		}
		//TODO IF COMMAND
		
		
		SharedHandler.getNmsHandler().sendTitleToPlayer(player, 20, 40, 20, ChatUtils.format(reward.getTitle()), 
				ChatUtils.format(reward.getSubTitle()));
		player.sendMessage(ChatUtils.format(reward.getMessage()));
		
		Firework fw = (Firework) player.getWorld().spawn(player.getEyeLocation(), Firework.class);
		FireworkMeta fmeta = fw.getFireworkMeta();
		fmeta.addEffect(FireworkEffect.builder().with(Type.BALL).withColor(Color.WHITE).withColor(Color.PURPLE).build());
		fmeta.setPower(0);
		fw.setFireworkMeta(fmeta);	
		player.playSound(player.getLocation(), Sound.LEVEL_UP, 10, 1); //TODO from config
		
		ScoreboardManager.getLobbyScoreboard().update(player, "keys", String.valueOf(ps.getChestKeys()));
	}
	
	public static String getTitle(RewardType type, String value) {
		return getInstance().replaceValue(type, getInstance().getString(type, "title"), value);
	}
	
	public static String getSubTitle(RewardType type, String value) {
		return getInstance().replaceValue(type, getInstance().getString(type, "sub-title"), value);
	}
	
	public static String getMessage(RewardType type, String value) {
		return getInstance().replaceValue(type, getInstance().getString(type, "message"), value);
	}
	
	private String replaceValue(RewardType type, String text, String value) {
		switch(type) {
		case MONEY:
			return text.replaceAll("%money%", value);
		case KIT:
			return text.replaceAll("%kit%", KitManager.getInstance().getExtras().get(Integer.valueOf(value)).getName());
		case TRAIL:
			return text.replaceAll("%trail%", TrailManager.getInstance().getExtras().get(Integer.valueOf(value)).getName());
		case DEATHEFFECT:
			return text.replaceAll("%death-effect%", DeathEffectManager.getInstance().getExtras().get(Integer.valueOf(value)).getName());
		case WINEFFECT:
			return text.replaceAll("%win-effect%", WinEffectManager.getInstance().getExtras().get(Integer.valueOf(value)).getName());
		default:
			return "";
		}
	}
	
	private String getString(RewardType type, String path) {
		ConfigurationSection section = ConfigRewardChest.get().getConfigurationSection("types");
		switch(type) {
		case MONEY:
			return section.getString("money." + path);
		case KIT:
			return section.getString("kit." + path);
		case TRAIL:
			return section.getString("trail." + path);
		case DEATHEFFECT:
			return section.getString("death-effect." + path);
		case WINEFFECT:
			return section.getString("win-effect." + path);
		default:
			return "";
		}
	}

	public Map<Integer, Reward> getRewards() {
		return rewards;
	}
	
	public static RewardChest getInstance() {
		if(instance == null) {
			instance = new RewardChest();
		}
		return instance;
	}

	public RewardChestMenu getMenu() {
		return menu;
	}
}