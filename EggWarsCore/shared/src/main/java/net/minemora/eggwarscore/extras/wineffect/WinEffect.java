package net.minemora.eggwarscore.extras.wineffect;

import net.minemora.eggwarscore.config.extras.ConfigWinEffects;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.database.Stat;
import net.minemora.eggwarscore.extras.Action;
import net.minemora.eggwarscore.extras.IExtra;

public abstract class WinEffect extends IExtra implements Action {

	public WinEffect(String extraPath, int id) {
		super(WinEffectManager.getInstance(), ConfigWinEffects.getInstance(), "win-effects", extraPath, id);
	}

	@Override
	public void giveToPlayer(PlayerStats playerStats) {
		playerStats.setWinEffect(this);
		playerStats.setRandomWinEffect(false);
		Database.set(Stat.WIN_EFFECT, playerStats.getPlayer(), getId());
	}

}
