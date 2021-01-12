package net.minemora.eggwarscore;

import org.bukkit.plugin.java.JavaPlugin;

import net.minemora.eggwarscore.bungee.BungeeHandler;
import net.minemora.eggwarscore.commands.EggWarsCommand;
import net.minemora.eggwarscore.config.ConfigNPC;
import net.minemora.eggwarscore.config.ConfigRewardChest;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.game.GameManager;
import net.minemora.eggwarscore.holographicdisplays.HolographicDisplaysHook;
import net.minemora.eggwarscore.listener.PlayerListener;
import net.minemora.eggwarscore.lobby.LobbyItemManager;
import net.minemora.eggwarscore.network.NetworkServer;
import net.minemora.eggwarscore.npc.NPCManager;
import net.minemora.eggwarscore.parkour.Parkour;
import net.minemora.eggwarscore.player.LobbyPlayer;
import net.minemora.eggwarscore.protocollib.ProtocolLibHook;
import net.minemora.eggwarscore.rchest.RewardChest;
import net.minemora.eggwarscore.reportsystem.ReportSystemHook;
import net.minemora.eggwarscore.scoreboard.ScoreboardManager;
import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.uc.UltraCosmeticsHook;
import net.minemora.eggwarscore.moraparty.MoraPartyHook;

public class EggWarsCoreLobby extends JavaPlugin {
	
	private static EggWarsCoreLobby plugin;

	@Override
	public void onEnable() {
		plugin = this;
		SharedHandler.load(this);
		HolographicDisplaysHook.register();
		ScoreboardManager.loadPlaceholders();
		ScoreboardManager.loadScoreboards();
		BungeeHandler.setup(this);
		GameManager.setup();
		LobbyItemManager.setup();
		ConfigNPC.getInstance().setup();
		ConfigRewardChest.getInstance().setup();
		new PlayerListener(this);
		getCommand("ewc").setExecutor(new EggWarsCommand());
		NetworkServer.setup();
		ProtocolLibHook.setup();
		NPCManager.loadNPCs();
		RewardChest.setup();
		UltraCosmeticsHook.setup();
		Parkour.setup();
		LobbyPlayer.setupHolos();
		ReportSystemHook.setup();
		MoraPartyHook.setup();
	}

	@Override
	public void onDisable() {
		Database.getDatabase().close();
		NetworkServer.shutdown();
	}

	public static EggWarsCoreLobby getPlugin() {
		return plugin;
	}
}
