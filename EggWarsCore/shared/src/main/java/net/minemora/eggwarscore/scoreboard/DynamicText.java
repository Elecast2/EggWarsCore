package net.minemora.eggwarscore.scoreboard;

public class DynamicText {

	private String prefix;
	private String suffix;
	
	public DynamicText(String text) {
		prefix = createPrefix(text);
		suffix = createSuffix(text);
		if(prefix.length()==16) {
			if(prefix.charAt(15) == '&') {
				prefix = prefix.substring(0, 15);
				suffix = "&" + suffix;
				if(suffix.length()>16) {
					suffix = suffix.substring(0, 16);
				}
			}
		}
	}
	
	private static String createPrefix(String text) {
		if(text.length() <= 16) {
			return text;
		}
		return text.substring(0, 16);
	}
	
	private static String createSuffix(String text) {
		if(text.length() > 16) {
			if(text.length() > 32) {
				return text.substring(16, 32);
			}
			else {
				return text.substring(16, text.length());
			}
		}
		else {
			return "";
		}
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}
}
