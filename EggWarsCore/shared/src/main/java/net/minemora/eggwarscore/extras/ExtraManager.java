package net.minemora.eggwarscore.extras;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minemora.eggwarscore.config.Config;
import net.minemora.eggwarscore.extras.deatheffect.DeathEffectManager;
import net.minemora.eggwarscore.extras.kit.KitManager;
import net.minemora.eggwarscore.extras.trail.TrailManager;
import net.minemora.eggwarscore.extras.wineffect.WinEffectManager;

public abstract class ExtraManager {
	
	private static Set<ExtraManager> managers = new HashSet<>();
	
	private Map<Integer, Extra> extras = new HashMap<>();
	
	private Map<String, Set<Extra>> permissionExtras = new HashMap<>();
	
	private final Config config;
	
	private final String path;
	
	private final String tableName;
	private final String columnName;
	private final boolean store;
	
	protected ExtraManager(Config config, String path, String tableName, String columnName, boolean store) {
		this.config = config;
		this.path = path;
		this.tableName = tableName;
		this.columnName = columnName;
		this.store = store;
		managers.add(this);
	}
	
	public void load() {
		if(getExtraList().isEmpty()) {
			return;
		}
		extras.clear();
		for(int id : getExtraList()) {
			extras.put(id, getNewExtra(id));
		}
		for(Extra extra : extras.values()) {
			if(extra.getUnlockType() == UnlockType.PERMISSION) {
				if(permissionExtras.containsKey(extra.getPermission())) {
					permissionExtras.get(extra.getPermission()).add(extra);
				}
				else {
					permissionExtras.put(extra.getPermission(), new HashSet<Extra>());
					permissionExtras.get(extra.getPermission()).add(extra);
				}
			}
		}
	}
	
	public abstract Extra getNewExtra(int id);
	
	protected Set<Integer> getExtraList() {
		Set<Integer> extraList = new HashSet<>();
		if (config.getConfig().get(path) != null) {
			for (String id : config.getConfig().getConfigurationSection(path).getValues(false).keySet()) {
				extraList.add(Integer.valueOf(id));
			}
		}
		return extraList;
	}
	
	public static void loadExtras() {
		TrailManager.getInstance().load();
		KitManager.getInstance().load();
		DeathEffectManager.getInstance().load();
		WinEffectManager.getInstance().load();
	}

	public Map<Integer, Extra> getExtras() {
		return extras;
	}

	public Map<String, Set<Extra>> getPermissionExtras() {
		return permissionExtras;
	}

	public Config getConfig() {
		return config;
	}

	public String getPath() {
		return path;
	}

	public static Set<ExtraManager> getManagers() {
		return managers;
	}

	public String getTableName() {
		return tableName;
	}

	public String getColumnName() {
		return columnName;
	}

	public boolean isStore() {
		return store;
	}
}