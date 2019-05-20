package net.minemora.eggwarscore.utils;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minemora.eggwarscore.config.ConfigLang;

public interface ChatUtils {

	static String format(String m) {
		return ChatColor.translateAlternateColorCodes('&', m);
	}

	static String[] format(String[] m) {
		String[] result;
		result = new String[m.length];
		for (int i = 0; i < m.length; i++) {
			result[i] = ChatColor.translateAlternateColorCodes('&', m[i]);
		}
		return result;
	}

	static String[] format(List<String> m) {
		String[] result;
		result = new String[m.size()];
		for (int i = 0; i < m.size(); i++) {
			result[i] = ChatColor.translateAlternateColorCodes('&', m.get(i));
		}
		return result;
	}
	
	static List<String> formatList(List<String> m) {
		return m.stream().map(s -> ChatColor.translateAlternateColorCodes('&', s)).collect(Collectors.toList());
	}
	
	static String formatSeconds(double seconds) {
		seconds = Utils.round(seconds, 2);
		if(seconds % 1 == 0) {
			return String.valueOf((int)seconds) + " " 
					+ ConfigLang.get().getString("time.seconds." + (((int)seconds == 1) ? "singular" : "plural"));
		}
		else {
			return String.valueOf(seconds) + " " 
					+ ConfigLang.get().getString("time.seconds.plural");
		}
	}
	
	static String formatTime(int seconds) {
		return String.format("%02d:%02d", seconds / 60, seconds % 60);
	}

	static TextComponent jsonText(String text, String cmd, String hoverMsg) {
		TextComponent tcomp = new TextComponent(ChatUtils.format(text));
		tcomp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd));
		tcomp.setHoverEvent(
				new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatUtils.format(hoverMsg)).create()));
		return tcomp;
	}

	static String formatLocation(Location loc) {
		return "[x:" + loc.getBlockX() + ", y:" + loc.getBlockY() + ", z:" + loc.getBlockZ() + "]";
	}
}