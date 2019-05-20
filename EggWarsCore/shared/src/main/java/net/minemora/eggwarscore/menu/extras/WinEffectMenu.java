package net.minemora.eggwarscore.menu.extras;

import java.util.Collection;

import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.database.Stat;
import net.minemora.eggwarscore.extras.Extra;
import net.minemora.eggwarscore.extras.wineffect.WinEffectManager;

public class WinEffectMenu extends ExtraMenu {

	public WinEffectMenu(PlayerStats playerStats) {
		super(playerStats);
	}

	@Override
	public Collection<? extends Extra> getAllExtras() {
		return WinEffectManager.getInstance().getExtras().values();
	}

	@Override
	public Collection<? extends Extra> getAvailableExtras() {
		return getPlayerStats().getWinEffects();
	}

	@Override
	public Extra getCurrentExtra() {
		return getPlayerStats().getWinEffect();
	}

	@Override
	public void removeExtra() {
		getPlayerStats().setWinEffect(null);
		getPlayerStats().setRandomWinEffect(false);
		Database.set(Stat.WIN_EFFECT, getPlayerStats().getPlayer(), 0);
	}

	@Override
	public void setExtraRandom() {
		getPlayerStats().setWinEffect(null);
		getPlayerStats().setRandomWinEffect(true);
		Database.set(Stat.WIN_EFFECT, getPlayerStats().getPlayer(), -1);
	}

	@Override
	public boolean isExtraRandom() {
		return getPlayerStats().isRandomWinEffect();
	}

	@Override
	public String getMenuTitle() {
		return ConfigLang.get().getString("extras.win-effects-menu-title");
	}

}
