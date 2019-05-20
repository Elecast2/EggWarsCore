package net.minemora.eggwarscore.extras;

public enum UnlockType {
	
	DISABLED("default"),
	DEFAULT("default"),
	LEVEL("level"),
	KEY("key"),
	PRICE("price"),
	PERMISSION("permission");
	
	private final String path;
	
	private UnlockType(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

}
