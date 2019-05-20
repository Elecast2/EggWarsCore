package net.minemora.eggwarscore.database;

public enum Stat {
	
	KILLS("kills", int.class),
	DEATHS("deaths", int.class),
	WINS("wins", int.class),
	DESTROYED_EGGS("destroyedEggs", int.class),
	MONEY("money", int.class),
	EXP("exp", int.class),
	CHEST_KEYS("usedChestKeys", int.class),
	KIT("kit", int.class),
	TRAIL("trail", int.class),
	WIN_EFFECT("winEffect", int.class),
	DEATH_EFFECT("deathEffect", int.class),
	MODE("mode", int.class),
	PARKOUR_DONE("parkourDone", int.class),
	PARKOUR_TIME("parkourTime", int.class);
	
	private final String columnName;
	private final Class<?> type;
	
	private Stat(String columnName, Class<?> type) {
		this.columnName = columnName;
		this.type = type;
	}
	
	public String getColumnName() {
		return columnName;
	}
	
	public Class<?> getType() {
	    return this.type;
	}
}