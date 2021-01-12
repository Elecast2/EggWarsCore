package net.minemora.eggwarscore.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.minemora.eggwarscore.config.ConfigMain;
import net.minemora.eggwarscore.extras.Extra;
import net.minemora.eggwarscore.extras.ExtraManager;
import net.minemora.eggwarscore.shared.SharedHandler;

public class Database {
	
	private static Database database;
	
	private String prefix;
	private String url;
	private String username;
	private String password;
	
	private Connection connection;
	
	private Database() {}
	
	public void setup() {
		this.prefix = ConfigMain.get().getString("database.prefix");
		this.url = ConfigMain.get().getString("database.url");
		this.username = ConfigMain.get().getString("database.username");
		this.password = ConfigMain.get().getString("database.password");
		createConnection();
		createTables();
	}
	
	private void createConnection() {
		try {
		    Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		    return;
		}
		try {
		    this.connection = DriverManager.getConnection(url,username,password);
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}
	
	private void createTables() {
		String sql = "CREATE TABLE IF NOT EXISTS " + prefix + "players (uuid varchar(36) NOT NULL PRIMARY KEY, "
	    		+ "name varchar(32), "
	    		+ "kills int(11) NOT NULL DEFAULT '0', "
	    		+ "deaths int(11) NOT NULL DEFAULT '0', "
	    		+ "wins int(11) NOT NULL DEFAULT '0', "
	    		+ "destroyedEggs int(11) NOT NULL DEFAULT '0', "
	    		+ "money int(11) NOT NULL DEFAULT '0', "
	    		+ "exp int(11) NOT NULL DEFAULT '0', "
	    		+ "usedChestKeys int(11) NOT NULL DEFAULT '0', "
	    		+ "kit int(11) NOT NULL DEFAULT '0', "
	    		+ "trail int(11) NOT NULL DEFAULT '0', "
	    		+ "tag int(11) NOT NULL DEFAULT '0', "
	    		+ "winEffect int(11) NOT NULL DEFAULT '1', "
	    		+ "deathEffect int(11) NOT NULL DEFAULT '0', "
	    		+ "parkourDone tinyint(1) NOT NULL DEFAULT '0', "
	    		+ "parkourTime int(11) NOT NULL DEFAULT '0', "
	    		+ "mode int(11) NOT NULL DEFAULT '0', "
	    		+ "hidePlayers tinyint(1) NOT NULL DEFAULT '0'"
	    		+ ");";
		update(sql);
		for(ExtraManager manager : ExtraManager.getManagers()) {
			if(!manager.isStore()) {
				continue;
			}
			sql = "CREATE TABLE IF NOT EXISTS " + prefix + manager.getTableName() + " (id int NOT NULL AUTO_INCREMENT, uuid varchar(36), " 
					+ manager.getColumnName() + " int(11) NOT NULL DEFAULT '0', PRIMARY KEY (id))";
			update(sql);
		}
		//TODO if(DailyRewards.isEnabled() {
			sql = "CREATE TABLE IF NOT EXISTS " + prefix + "rewards(UUID varchar(36) NOT NULL PRIMARY KEY, "
					+ "daily int DEFAULT 0, weekly int DEFAULT 0, monthly int DEFAULT 0, "
					+ "facebook tinyint(1) DEFAULT 0, twitter tinyint(1) DEFAULT 0, youtube tinyint(1) DEFAULT 0)"; //TODO CONFIGURABLE
			update(sql);
		//}
	}
	//RUN THIS ASYNC
	public void loadStats(PlayerStats ps) {
		if(ps.getPlayer() == null) {
			return;
		}
		loadMainStats(ps);
		loadExtras(ps);
		ps.setLoaded(true);
	}
	
	private void loadMainStats(PlayerStats ps) {
		UUID uid = ps.getPlayer().getUniqueId();
		String selectSql = "SELECT * FROM " + prefix + "players WHERE uuid = '" + uid + "';";
		try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
			try (ResultSet result = stmt.executeQuery()){
				if(!result.next()) {
					update("INSERT INTO " + prefix + "players (uuid, name) VALUES ('" + uid + "', '" + ps.getPlayerName() 
							+ "') ON DUPLICATE KEY UPDATE uuid = '" + uid + "', name = '" + ps.getPlayerName() + "';");
					ps.setKills(0);
					ps.setDeaths(0);
					ps.setWins(0);
					ps.setDestroyedEggs(0);
					ps.setMoney(0);
					ps.setExp(0);
					ps.setUsedChestKeys(0);
					ps.setKit(0);
					ps.setTrail(0);
					ps.setWinEffect(1);
					ps.setDeathEffect(0);
					ps.setMode(0);
					ps.setParkourDone(false);
					ps.setParkourTime(0);
					ps.setHidePlayers(false);
				}
				else {
					if(!result.getString("name").equals(ps.getPlayerName())){
						update("UPDATE " + prefix + "players SET name = '" + ps.getPlayerName() + "' WHERE uuid = '" + uid + "';");
					}
					ps.setKills(result.getInt(Stat.KILLS.getColumnName()));
					ps.setDeaths(result.getInt(Stat.DEATHS.getColumnName()));
					ps.setWins(result.getInt(Stat.WINS.getColumnName()));
					ps.setDestroyedEggs(result.getInt(Stat.DESTROYED_EGGS.getColumnName()));
					ps.setMoney(result.getInt(Stat.MONEY.getColumnName()));
					ps.setExp(result.getInt(Stat.EXP.getColumnName()));
					ps.setUsedChestKeys(result.getInt(Stat.CHEST_KEYS.getColumnName()));
					ps.setKit(result.getInt(Stat.KIT.getColumnName()));
					ps.setTrail(result.getInt(Stat.TRAIL.getColumnName()));
					ps.setWinEffect(result.getInt(Stat.WIN_EFFECT.getColumnName()));
					ps.setDeathEffect(result.getInt(Stat.DEATH_EFFECT.getColumnName()));
					ps.setMode(result.getInt(Stat.MODE.getColumnName()));
					ps.setParkourDone(result.getBoolean(Stat.PARKOUR_DONE.getColumnName()));
					ps.setParkourTime(result.getInt(Stat.PARKOUR_TIME.getColumnName()));
					ps.setHidePlayers(result.getBoolean(Stat.HIDE_PLAYERS.getColumnName()));
				}				
			}
		} catch (SQLException e) {
			 e.printStackTrace();
		}
	}
	
	private void loadExtras(PlayerStats ps) {
		if(ps.getPlayer() == null) {
			return;
		}
		UUID uid = ps.getPlayer().getUniqueId();
		for(ExtraManager manager : ExtraManager.getManagers()) {
			if(!manager.isStore()) {
				continue;
			}
			String sql = "SELECT " + manager.getColumnName() + " FROM " + prefix + manager.getTableName() + " WHERE uuid = '" + uid + "';";
			try (PreparedStatement stmt = connection.prepareStatement(sql)) {
				try (ResultSet result = stmt.executeQuery()) {
					while (result.next()) {
						int id = result.getInt(manager.getColumnName());
						if(!manager.getExtras().containsKey(id)) {
							continue;
						}
						ps.addExtra(manager.getExtras().get(id));
					}
				}
			} catch (SQLException e) {
				 e.printStackTrace();
			}
		}
	}
	
	public void loadDailyRewards(PlayerStats ps) {
		//TODO if(!DailyRewards.isEnabled() {
		//    return;
		//}
		UUID uid = ps.getPlayer().getUniqueId();
		String selectSql = "SELECT daily,weekly,monthly,facebook,twitter,youtube FROM " + prefix + "rewards WHERE uuid = '" + uid + "';";
		try (PreparedStatement stmt = connection.prepareStatement(selectSql)) {
			try (ResultSet result = stmt.executeQuery()) {
				if(!result.next()) {
					update("INSERT INTO " + prefix + "rewards (uuid) VALUES ('" + uid + "') ON DUPLICATE KEY UPDATE uuid = '" + uid + "'");
					ps.setFacebook(false);
					ps.setTwitter(false);
					ps.setYoutube(false);
				}
				else {
					ps.setDailyReward(result.getInt("daily"));
					ps.setWeeklyReward(result.getInt("weekly"));
					ps.setMonthlyReward(result.getInt("monthly"));
					ps.setFacebook(result.getBoolean("facebook"));
					ps.setTwitter(result.getBoolean("twitter"));
					ps.setYoutube(result.getBoolean("youtube"));
				}
				ps.setDailyRewardsLoaded(true);
			}
		} catch (SQLException e) {
			 e.printStackTrace();
		}
	}
	
	public void addExtra(UUID uid, Extra extra) {
		String sql;
		sql = "INSERT INTO " + prefix + extra.getManager().getTableName() + " (uuid, " + extra.getManager().getColumnName() 
				+") VALUES ('" + uid + "', '" + extra.getId() +"');";
		asyncUpdate(sql);
	}
	
	public static void add(Stat stat, Player player, int value) {
		if(!stat.getType().equals(int.class)) {
			return;
		}
		getDatabase().add(player.getUniqueId(), stat.getColumnName(), value);
	}
	
	public static void set(Stat stat, Player player, Object value) {
		if(PlayerStats.get(player.getName()).getDatabaseSets() > 20) {
			return;
		}
		getDatabase().set(player.getUniqueId(), stat.getColumnName(), value);
		PlayerStats.get(player.getName()).setDatabaseSets(PlayerStats.get(player.getName()).getDatabaseSets() + 1);
	}
	
	public static void set(Stat stat, Player player, int value) {
		if(PlayerStats.get(player.getName()).getDatabaseSets() > 20) {
			return;
		}
		getDatabase().set(player.getUniqueId(), stat.getColumnName(), value);
		PlayerStats.get(player.getName()).setDatabaseSets(PlayerStats.get(player.getName()).getDatabaseSets() + 1);
	}
	
	public static void subtract(Stat stat, Player player, int value) {
		if(!stat.getType().equals(int.class)) {
			return;
		}
		getDatabase().subtract(player.getUniqueId(), stat.getColumnName(), value);
	}
	
	private void add(UUID uid, String column, int value) {
		String sql = "UPDATE " + prefix + "players SET " + column + " = " + column + " + " + value + " WHERE uuid = '" + uid + "';";
		asyncUpdate(sql);
	}
	
	private void set(UUID uid, String column, Object value) {
		String sql = "UPDATE " + prefix + "players SET " + column + " = " + value + " WHERE uuid = '" + uid + "';";
		asyncUpdate(sql);
	}
	
	private void subtract(UUID uid, String column, int value) {
		String sql = "UPDATE " + prefix + "players SET " + column + " = " + column + " - " + value + " WHERE uuid = '" + uid + "';";
		asyncUpdate(sql);
	}
	
	public static LinkedHashMap<String, Integer> getTopKills(int limit) {
		return getDatabase().getTop("kills", limit);
	}
	
	public static LinkedHashMap<String, Integer> getTopDeaths(int limit) {
		return getDatabase().getTop("deaths", limit);
	}
	
	public static LinkedHashMap<String, Integer> getTopWins(int limit) {
		return getDatabase().getTop("wins", limit);
	}
	
	public static LinkedHashMap<String, Integer> getTopEggs(int limit) {
		return getDatabase().getTop("destroyedEggs", limit);
	}
	
	private LinkedHashMap<String, Integer> getTop(String column, int limit) {
		LinkedHashMap<String, Integer> top = new LinkedHashMap<>();
		String sql = "SELECT name, " + column + " FROM " + prefix + "players ORDER BY " + prefix 
				+ "players." + column + " DESC LIMIT " + limit + ";";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			try (ResultSet result = stmt.executeQuery()){
				while (result.next()) {
					top.put(result.getString("name"), result.getInt(column));
				}
			}
		} catch (SQLException e) {
			 e.printStackTrace();
		}
		return top;
	}
	
	public void update(String sql) {
		try(PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.executeUpdate();
		} catch (SQLException ex) {
		    ex.printStackTrace();
		}
	}
	
	public void asyncUpdate(String sql) {
		new BukkitRunnable() {
			@Override
			public void run() {
				update(sql);
			}
		}.runTaskAsynchronously(SharedHandler.getPlugin());
	}
	
	public void close() {
		try {
	        if (connection!=null && !connection.isClosed()) {
	            connection.close();
	        }
	    } catch(Exception e) {
	        e.printStackTrace();
	    }
	}
	
	public static Database getDatabase() {
		if (database == null) {
			database = new Database();
        }
        return database;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}