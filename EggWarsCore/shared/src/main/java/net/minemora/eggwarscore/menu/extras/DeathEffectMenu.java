package net.minemora.eggwarscore.menu.extras;

import java.util.Collection;

import net.minemora.eggwarscore.config.ConfigLang;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.database.Stat;
import net.minemora.eggwarscore.extras.Extra;
import net.minemora.eggwarscore.extras.deatheffect.DeathEffectManager;

public class DeathEffectMenu extends ExtraMenu {

	public DeathEffectMenu(PlayerStats playerStats) {
		super(playerStats);
	}

	@Override
	public Collection<? extends Extra> getAllExtras() {
		return DeathEffectManager.getInstance().getExtras().values();
	}

	@Override
	public Collection<? extends Extra> getAvailableExtras() {
		return getPlayerStats().getDeathEffects();
	}

	@Override
	public Extra getCurrentExtra() {
		return getPlayerStats().getDeathEffect();
	}

	@Override
	public void removeExtra() {
		getPlayerStats().setDeathEffect(null);
		getPlayerStats().setRandomDeathEffect(false);
		Database.set(Stat.DEATH_EFFECT, getPlayerStats().getPlayer(), 0);
	}

	@Override
	public void setExtraRandom() {
		getPlayerStats().setDeathEffect(null);
		getPlayerStats().setRandomDeathEffect(true);
		Database.set(Stat.DEATH_EFFECT, getPlayerStats().getPlayer(), -1);
	}

	@Override
	public boolean isExtraRandom() {
		return getPlayerStats().isRandomDeathEffect();
	}

	@Override
	public String getMenuTitle() {
		return ConfigLang.get().getString("extras.death-effects-menu-title");
	}

}
