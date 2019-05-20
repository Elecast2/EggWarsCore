package net.minemora.eggwarscore.lobby;

import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.utils.ItemAction;
import net.minemora.eggwarscore.utils.ItemConfig;

public class LobbyItem extends ItemConfig {
	
	private int slot;
	private String id;
	private ItemAction action;
	
	public LobbyItem(String id, int slot) {
		super(ConfigMain.getInstance(), "lobby-items." + id);
		this.id = id;
		this.slot = slot;
	}

	public int getSlot() {
		return slot;
	}

	public void setSlot(int slot) {
		this.slot = slot;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ItemAction getAction() {
		return action;
	}
	
	public void setAction(ItemAction action) {
		this.action = action;
	}
}