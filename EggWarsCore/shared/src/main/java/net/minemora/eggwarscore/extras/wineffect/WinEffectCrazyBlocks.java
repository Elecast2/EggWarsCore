package net.minemora.eggwarscore.extras.wineffect;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.minemora.eggwarscore.shared.SharedHandler;
import net.minemora.eggwarscore.utils.Utils;

public class WinEffectCrazyBlocks extends WinEffect {

	private static WinEffectCrazyBlocks instance;

	private WinEffectCrazyBlocks() {
		super("crazy-blocks", 4);
	}

	@Override
	public void play(Player player) {
		new BukkitRunnable() {
    		int count = 10;
    		Location loc = player.getLocation();
    		@SuppressWarnings("deprecation")
			@Override
    		public void run() {
    			if(player.getWorld().equals(Bukkit.getWorlds().get(0))) {
					cancel();
					return;
				}
				if(player.isOnline()) {
					loc = player.getLocation();
				}
    			count--;
    	    	for(Block block : Utils.getBlocksInRadius(loc.clone().add(0, -1, 0), (10-count), true)){
    	    		int rand = ThreadLocalRandom.current().nextInt(100);
    	    		if(rand<30) {
    	    			if(!block.getType().equals(Material.AIR)) {
	    		    		if(block.getRelative(BlockFace.UP).getType().equals(Material.AIR)) {
    		    				FallingBlock fblock = loc.getWorld().spawnFallingBlock(
    		    						block.getLocation().clone().add(0, 1.1, 0), block.getType(), block.getData());
    		    				fblock.setVelocity(new Vector(0,0.3,0));
    		    				fblock.setDropItem(false);
    		    				fblock.setHurtEntities(false);
    		    				LivingEntity bat = (LivingEntity) loc.getWorld().spawnEntity(block.getLocation(), EntityType.BAT);
    		    				bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
    		    				bat.setPassenger(fblock);
	    		    		}
	    	    		}
    	    		}
    	    	}
    	    	if(count==0) {
    	    		cancel();
    	    	}
    		}
    	}.runTaskTimer(SharedHandler.getPlugin(), 0L, 3L);
	}
	
	public static WinEffectCrazyBlocks getInstance() {
		if(instance == null) {
			instance = new WinEffectCrazyBlocks();
		}
		return instance;
	}

}
