package net.minemora.eggwarscore.scoreboard;

import java.util.HashMap;
import java.util.Map;

public class Placeholder {
	
	private final String text;
	private final boolean dynamic;
	private final boolean title;
	
	private static Map<String, Placeholder> placeholders = new HashMap<>();
	
	public Placeholder(String text, boolean dynamic, boolean title) {
		this.text = text;
		this.dynamic = dynamic;
		this.title = title;
		placeholders.put(text, this);
	}
	
	public Placeholder(String text, boolean dynamic) {
		this(text, dynamic, false);
	}
	
	public static Placeholder get(String text) {
		return placeholders.get(text);
	}
	
	public static Map<String, Placeholder> getPlaceholders() {
		return placeholders;
	}

	public boolean isDynamic() {
		return dynamic;
	}

	public String getText() {
		return text;
	}

	public boolean isTitle() {
		return title;
	}
}