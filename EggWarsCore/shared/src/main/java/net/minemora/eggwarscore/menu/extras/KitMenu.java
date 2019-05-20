package net.minemora.eggwarscore.menu.extras;

import java.util.Collection;

import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.database.Stat;
import net.minemora.eggwarscore.extras.Extra;
import net.minemora.eggwarscore.extras.kit.KitManager;

public class KitMenu extends ExtraMenu {

	public KitMenu(PlayerStats playerStats) {
		super(playerStats);
	}

	@Override
	public Collection<? extends Extra> getAllExtras() {
		return KitManager.getInstance().getExtras().values();
	}

	@Override
	public Collection<? extends Extra> getAvailableExtras() {
		return getPlayerStats().getKits();
	}

	@Override
	public Extra getCurrentExtra() {
		return getPlayerStats().getKit();
	}

	@Override
	public void removeExtra() {
		getPlayerStats().setKit(null);
		getPlayerStats().setRandomKit(false);
		Database.set(Stat.KIT, getPlayerStats().getPlayer(), 0);
	}

	@Override
	public void setExtraRandom() {
		getPlayerStats().setKit(null);
		getPlayerStats().setRandomKit(true);
		Database.set(Stat.KIT, getPlayerStats().getPlayer(), -1);
	}

	@Override
	public boolean isExtraRandom() {
		return getPlayerStats().isRandomKit();
	}

	@Override
	public String getMenuTitle() {
		return ConfigLang.get().getString("extras.kits-menu-title");
	}
}
