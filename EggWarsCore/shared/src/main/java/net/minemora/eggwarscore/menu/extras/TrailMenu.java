package net.minemora.eggwarscore.menu.extras;


import java.util.Collection;

import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.database.Stat;
import net.minemora.eggwarscore.extras.Extra;
import net.minemora.eggwarscore.extras.trail.TrailManager;

public class TrailMenu extends ExtraMenu {

	public TrailMenu(PlayerStats playerStats) {
		super(playerStats);
	}

	@Override
	public Collection<? extends Extra> getAllExtras() {
		return TrailManager.getInstance().getExtras().values();
	}

	@Override
	public Collection<? extends Extra> getAvailableExtras() {
		return getPlayerStats().getTrails();
	}

	@Override
	public Extra getCurrentExtra() {
		return getPlayerStats().getTrail();
	}

	@Override
	public void removeExtra() {
		getPlayerStats().setTrail(null);
		getPlayerStats().setRandomTrail(false);
		Database.set(Stat.TRAIL, getPlayerStats().getPlayer(), 0);
	}

	@Override
	public void setExtraRandom() {
		getPlayerStats().setTrail(null);
		getPlayerStats().setRandomTrail(true);
		Database.set(Stat.TRAIL, getPlayerStats().getPlayer(), -1);
	}

	@Override
	public boolean isExtraRandom() {
		return getPlayerStats().isRandomTrail();
	}

	@Override
	public String getMenuTitle() {
		return ConfigLang.get().getString("extras.trails-menu-title");
	}
}