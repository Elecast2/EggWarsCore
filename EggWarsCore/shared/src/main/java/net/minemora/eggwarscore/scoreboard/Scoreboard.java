package net.minemora.eggwarscore.scoreboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import net.minemora.eggwarscore.config.ConfigScoreboard;
import net.minemora.eggwarscore.game.Multicast;
import net.minemora.eggwarscore.utils.ChatUtils;
import net.minemora.eggwarscore.utils.Utils;

public class Scoreboard {
	
	private String title;
	private Map<Integer,Line> lines = new HashMap<>();
	private Map<Placeholder,Set<Line>> placeholderLines = new HashMap<>();
	static Map<String,Map<Placeholder,String>> placeholdersCache = new HashMap<>();
	
	public Scoreboard(String title, List<String> lines) {
		this.title = title;
		int count = 1;
		for(String text : lines) {
			if(text.trim().length() == 0) {
				text = "&r" + indexToEntry(count);
			}
			Line line = new Line(text, count);
			this.lines.put(count, line);
			for(String phText : Placeholder.getPlaceholders().keySet()) {
				if(text.contains("%" + phText + "%")) {
					Placeholder placeholder = Placeholder.get(phText);
					if(placeholder.isDynamic()) {
						line.setDynamic(true);
					}
					if(placeholderLines.containsKey(placeholder)) {
						placeholderLines.get(placeholder).add(line);
					}
					else {
						placeholderLines.put(placeholder, new HashSet<>());
						placeholderLines.get(placeholder).add(line);
					}
				}
			}
			count++;
		}
	}
	
	public void set(Player player, Map<Placeholder,String> defaults) {
		if(!placeholdersCache.containsKey(player.getName())) {
			placeholdersCache.put(player.getName(), defaults);
		}
		else {
			placeholdersCache.get(player.getName()).putAll(defaults);
		}
		String finalTitle = title;
		for(Placeholder ph : defaults.keySet()) {
			if(ph.isTitle()) {
				if(finalTitle.contains("%" + ph.getText() + "%")) {
					finalTitle.replaceAll("%" + ph.getText() + "%", defaults.get(ph));
				}
			}
		}
		org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj = scoreboard.registerNewObjective(player.getName(), "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(ChatUtils.format(finalTitle));
		
		for(int i : lines.keySet()) {
			
			String finalText = getFinalText(player, lines.get(i).getText());
			
			if(lines.get(i).isDynamic()) {
				DynamicText dText = new DynamicText(finalText);
				String entry = ChatUtils.format("&m" + indexToEntry(i) + "&" + Utils.getLastColor(dText.getPrefix()));
				Team team = scoreboard.registerNewTeam(lines.get(i).getTeamName());
				team.addEntry(entry);
				team.setPrefix(ChatUtils.format(dText.getPrefix()));
				team.setSuffix(ChatUtils.format(dText.getSuffix()));
		        obj.getScore(entry).setScore(16-i);
			}
			else {
				Score score = obj.getScore(ChatUtils.format(finalText.length() > 32 ? finalText.substring(0, 32) : finalText));
				score.setScore(16-i);
			}
		}
		player.setScoreboard(scoreboard);
	}
	
	public void updateTitle(Multicast multicast, String text, String value) {
		for(Player player : multicast.getBukkitPlayers()) {
			updateTitle(player, text, value);
		}
	}
	
	public void update(Multicast multicast, String text, String value) {
		for(Player player : multicast.getBukkitPlayers()) {
			update(player, text, value);
		}
	}
	
	public void updateTitle(Set<Player> players, String text, String value) {
		for(Player player : players) {
			updateTitle(player, text, value);
		}
	}
	
	public void update(Set<Player> players, String text, String value) {
		for(Player player : players) {
			update(player, text, value);
		}
	}
	
	public void update(Player player, String text, String value) {
		if(!placeholdersCache.containsKey(player.getName())) {
			return;
		}
		Placeholder placeholder = Placeholder.get(text);
		placeholdersCache.get(player.getName()).put(placeholder, value);
		if(placeholder.isDynamic()) {
			if(placeholderLines.containsKey(placeholder)) {
				for(Line line : placeholderLines.get(placeholder)) {
					if(player.getScoreboard().getTeam(line.getTeamName()) == null) {
						return;
					}
					String finalText = getFinalText(player, line.getText());
					DynamicText dText = new DynamicText(finalText);
					Team team = player.getScoreboard().getTeam(line.getTeamName());
					String lastEntry = null;
					for(String entryText : team.getEntries()) {
						lastEntry = entryText;
						break;
					}
					String entry = ChatUtils.format("&m" + indexToEntry(line.getIndex()) 
					+ "&" + Utils.getLastColor(dText.getPrefix()));
					char a = lastEntry.charAt(5);
					char b = entry.charAt(5);
					if(a != b) {
						player.getScoreboard().getObjective(player.getName()).getScore(lastEntry).setScore(0);
						team.removeEntry(lastEntry);
						team.addEntry(entry);
						player.getScoreboard().getObjective(player.getName()).getScore(entry).setScore(16-line.getIndex());
					}
					team.setPrefix(ChatUtils.format(dText.getPrefix()));
					team.setSuffix(ChatUtils.format(dText.getSuffix()));
				}
			}
		}
	}
	
	public void updateTitle(Player player, String text, String value) {
		player.getScoreboard().getObjective(player.getName()).setDisplayName(ChatUtils.format(title.replaceAll("%" + text + "%", value)));
	}
	
	public static Scoreboard deserealize(String name) {
		return new Scoreboard(ConfigScoreboard.get().getString("scoreboards." + name + ".title"), 
				ConfigScoreboard.get().getStringList("scoreboards." + name + ".lines"));
	}
	
	public static String indexToEntry(int i) {
		char[] chars = new char[]{'a','b','c','d','e','f','1','2','3','4','5','6','7','8','9'};
		return ChatUtils.format("&" + chars[i-1] + "&f");
	}
	
	public static String getFinalText(Player player, String text) {
		for(String phText : Placeholder.getPlaceholders().keySet()) {
			if(text.contains("%" + phText + "%")) {
				String value;
				if(Scoreboard.placeholdersCache.get(player.getName()).containsKey(Placeholder.get(phText))) {
					value = Scoreboard.placeholdersCache.get(player.getName()).get(Placeholder.get(phText));
				}
				else {
					value = "";
				}
				text = text.replaceAll("%" + phText + "%", value);
			}
		}
		return text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<Integer,Line> getLines() {
		return lines;
	}

	public void setLines(Map<Integer,Line> lines) {
		this.lines = lines;
	}

	public Map<Placeholder,Set<Line>> getPlaceholderLines() {
		return placeholderLines;
	}

	public static Map<String,Map<Placeholder,String>> getPlaceholdersCache() {
		return placeholdersCache;
	}
}