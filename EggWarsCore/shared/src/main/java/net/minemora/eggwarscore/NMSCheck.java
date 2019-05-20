package net.minemora.eggwarscore;

import org.bukkit.plugin.Plugin;

import net.minemora.eggwarscore.nms.NMS;

public final class NMSCheck {
	
	private NMSCheck() {}
	
	public static NMS getNMS(Plugin plugin) {
        String packageName = plugin.getServer().getClass().getPackage().getName();
        // Get full package string of CraftServer.
        // org.bukkit.craftbukkit.version
        String version = packageName.substring(packageName.lastIndexOf('.') + 1);
        // Get the last element of the package

        try {
            final Class<?> clazz = Class.forName("net.minemora.eggwarscore.nms." + version + ".NMSHandler");
            // Check if we have a NMSHandler class at that location.
            if (NMS.class.isAssignableFrom(clazz)) { // Make sure it actually implements NMS
            	return (NMS) clazz.getConstructor().newInstance(); // Set our handler
            }
        } catch (final Exception e) {
            e.printStackTrace();
            plugin.getLogger().severe("Could not find support for this Spigot version.");
            return null;
        }
        return null;
	}
}
