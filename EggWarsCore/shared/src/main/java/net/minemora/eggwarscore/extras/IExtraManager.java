package net.minemora.eggwarscore.extras;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minemora.eggwarscore.config.Config;

public abstract class IExtraManager extends ExtraManager {
	
	private Map<String, IExtra> iExtras = new HashMap<>();

	protected IExtraManager(Config config, String path, String tableName, String columnName, boolean store) {
		super(config, path, tableName, columnName, store);
	}
	
	@Override
	public void load() {
		if(getIExtraList().isEmpty()) {
			return;
		}
		getExtras().clear();
		getIExtras().clear();
		loadIExtras();
		for(String path : getIExtraList()) {
			if(!getIExtras().containsKey(path)) {
				continue;
			}
			IExtra iExtra = getIExtras().get(path);
			getExtras().put(iExtra.getId(), iExtra);
			
		}
		for(Extra extra : getExtras().values()) {
			if(extra.getUnlockType() == UnlockType.PERMISSION) {
				if(getPermissionExtras().containsKey(extra.getPermission())) {
					getPermissionExtras().get(extra.getPermission()).add(extra);
				}
				else {
					getPermissionExtras().put(extra.getPermission(), new HashSet<Extra>());
					getPermissionExtras().get(extra.getPermission()).add(extra);
				}
			}
		}
	}
	
	public Set<String> getIExtraList() {
		Set<String> IExtraList = new HashSet<>();
		if (getConfig().getConfig().get(getPath()) != null) {
			for (String id : getConfig().getConfig().getConfigurationSection(getPath()).getValues(false).keySet()) {
				IExtraList.add(id);
			}
		}
		return IExtraList;
	}
	
	public abstract void loadIExtras();

	public Map<String, IExtra> getIExtras() {
		return iExtras;
	}
	
	@Override
	public Extra getNewExtra(int id) {
		return null;
	}
}
