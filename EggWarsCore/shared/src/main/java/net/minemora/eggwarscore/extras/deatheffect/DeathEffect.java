package net.minemora.eggwarscore.extras.deatheffect;

import net.minemora.eggwarscore.config.extras.ConfigDeathEffects;
import net.minemora.eggwarscore.database.Database;
import net.minemora.eggwarscore.database.PlayerStats;
import net.minemora.eggwarscore.database.Stat;
import net.minemora.eggwarscore.extras.Action;
import net.minemora.eggwarscore.extras.IExtra;

public abstract class DeathEffect extends IExtra implements Action {
	
	public DeathEffect(String extraPath, int id) {
		super(DeathEffectManager.getInstance(), ConfigDeathEffects.getInstance(), "death-effects", extraPath, id);
	}

	@Override
	public void giveToPlayer(PlayerStats playerStats) {
		playerStats.setDeathEffect(this);
		playerStats.setRandomDeathEffect(false);
		Database.set(Stat.DEATH_EFFECT, playerStats.getPlayer(), getId());
	}

}