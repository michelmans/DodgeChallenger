package me.alchemi.dodgechallenger.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import me.alchemi.dodgechallenger.Config;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.DodgeIsland;

public class DatabaseManager implements IDataManager{

	private List<BukkitRunnable> query = new ArrayList<BukkitRunnable>();
	
	private Connection conn;
	private String host, database, username, password;
	private int port;
	
	public DatabaseManager() {
		host = Config.DataBase.HOST.asString();
		port = Config.DataBase.PORT.asInt();
		database = Config.DataBase.DATABASE.asString();
		username = Config.DataBase.USERNAME.asString();
		password = Config.DataBase.PASSWORD.asString();
		
		if (Config.DataBase.ENABLED.asBoolean()) {
			BukkitRunnable r = new BukkitRunnable() {
				
				@Override
				public void run() {
	
					try {
						
						openConnection();
						
						if (!checkDBExist()) createDB();
						if (!checkTableExist()) createTable();
						
					} catch (ClassNotFoundException | SQLException e) {
						e.printStackTrace();
						Config.DataBase.ENABLED.set(false);
					}
					
				}
			};
			r.runTaskAsynchronously(Dodge.getInstance());
		}
	}
	
	private boolean checkDBExist() throws SQLException {
		ResultSet result = conn.getMetaData().getCatalogs();
		
		while (result.next()) {
			if (result.getString(1).equals(database)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkTableExist() throws SQLException {
		ResultSet result = conn.createStatement().executeQuery("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='data' AND TABLE_SCHEMA='"+database+"';");
		return result.next();
	}
	
	private void createDB() throws SQLException {
		Dodge.getInstance().getMessenger().print("Database " + database + " doesn't exist, creating...");
		String sqlCreate = "CREATE DATABASE " + database + ";";
		Statement stmt = conn.createStatement();
		stmt.execute(sqlCreate);
	}
	
	private void createTable() throws SQLException {
		Dodge.getInstance().getMessenger().print("Table doesn't exist, creating...");
	    String sqlCreate = "CREATE TABLE " + database + ".data (island TEXT NOT NULL,\r\n"
	    		+ "grade TINYINT(255) UNSIGNED NOT NULL,\r\n"
	    		+ "challenges LONGTEXT);";

	    Statement stmt = conn.createStatement();
	    stmt.executeUpdate(sqlCreate);
	}
	
	private void openConnection() throws SQLException, ClassNotFoundException {
		if (conn != null && !conn.isClosed()) return;
		
		synchronized (this) {
			if (conn != null && !conn.isClosed()) return;
			
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/?useSSL=false", this.username, this.password);
		}
	}
	
	@Override
	public void newIsland(UUID island) {
		new DodgeIsland(island);
		addIsland(island, 0, new ArrayList<Challenge>());
	}

	private void addIsland(UUID island, int rank, List<Challenge> challenges) {
		BukkitRunnable r = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				try {
					Statement statement = conn.createStatement();
					
					if (challenges.size() > 0) {
						String exe = "INSERT INTO " + database + ".data (ISLAND, grade, challenges) VALUES ('%island%', '%rank%'%challenges%');";
						
						String is = island.toString();
						String challs = ", '";
						for (Challenge c : challenges) {
							if (c.equals(challenges.get(0))) challs = challs.concat(c.toString());
							else challs = challs.concat("," + c.toString());
						}
						
						exe = exe.replace("%island%", is);
						exe = exe.replace("%rank%", String.valueOf(rank));
						exe = exe.replace("%challenges%", challs);
						
						statement.executeUpdate(exe);
						return;
					}
					
					String exe = "INSERT INTO " + database + ".data (ISLAND, grade) VALUES ('%island%', '%rank%');";
					
					String is = island.toString();
					exe = exe.replace("%island%", is);
					exe = exe.replace("%rank%", String.valueOf(rank));
					
					statement.executeUpdate(exe);
					
				} catch (SQLException e) {e.printStackTrace();}
				
			}
		};
		query.add(r);
	}
	
	@Override
	public void removeIsland(UUID island) {
		BukkitRunnable r = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				try {
					String sqlDrop = "DELETE FROM database.data WHERE island='isname';";
					sqlDrop = sqlDrop.replaceAll("database", database);
					sqlDrop = sqlDrop.replaceAll("isname", island.toString());
					Dodge.getInstance().getMessenger().print(sqlDrop);
					conn.createStatement().executeUpdate(sqlDrop);
				} catch (SQLException e) {}
				
			}
		};
		r.runTaskAsynchronously(Dodge.getInstance());
	}

	@Override
	public void runQuery() {
		if (query == null || query.isEmpty()) return;
		
		for (BukkitRunnable r : query) {
			r.run();
		}
	}
	
	@Override
	public int querySize() {
		if (query == null) return 0;
		return query.size();
	}

	@Override
	public int getRank(UUID island) {
		int rank = 0;
		try {	
			openConnection();
			
			String sqlGet = "SELECT * FROM %database%.data WHERE island=%is%;";
			sqlGet = sqlGet.replaceAll("%database%", database);
			sqlGet = sqlGet.replaceAll("%is%", island.toString());
			
			ResultSet results = conn.createStatement().executeQuery(sqlGet);
			
			if (results.next()) {
				rank = results.getInt("grade");
			}
		} catch(SQLException | ClassNotFoundException e) {}
		
		return rank;
	}
	
	@Override
	public List<String> getCCompleted(UUID island) {
		String output = "";
		List<String> list = new ArrayList<String>();
		try {	
			String sqlGet = "SELECT * FROM %database%.data WHERE island='%is%';";
			sqlGet = sqlGet.replaceAll("%database%", database);
			sqlGet = sqlGet.replaceAll("%is%", island.toString());
			
			ResultSet results = conn.createStatement().executeQuery(sqlGet);
			if (results.next()) {
				output = results.getString("challenges");
			}
			
		} catch(SQLException e) {e.printStackTrace();}
		
		if (output.contains(",")) {
			for (String s : output.split(",")) {
				list.add(s);
			}
		} else if (output.equals("")) {
			return null;
		} else {
			list.add(output);
		}
		
		return list.size() > 0 ? list : null;
	}
	
	@Override
	public void completeChallenge(UUID island, Challenge chall) {
		
		BukkitRunnable r = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				try {
					String sqlComplete = "UPDATE %database%.data SET challenges='%challenges%' WHERE island='%is%'";
					sqlComplete = sqlComplete.replaceAll("%database%", database);
					sqlComplete = sqlComplete.replaceAll("%is%", island.toString());
					
					String challs = "";
					List<Challenge> challenges = DodgeIslandManager.getManager().get(island).getChallenges();
					for (Challenge c : challenges) {
						if (c.equals(challenges.get(0))) challs = c.toString();
						else challs = challs.concat("," + c.toString());
					}
					sqlComplete = sqlComplete.replaceAll("%challenges%", challs);
					
					conn.createStatement().executeUpdate(sqlComplete);
				} catch (SQLException e) {e.printStackTrace();}
				
			}
		};
		
		query.add(r);
	}
	
	@Override
	public void setChallenges(UUID island, List<Challenge> challenges) {
		
		BukkitRunnable r = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				try {
					String sqlComplete = "UPDATE %database%.data SET challenges='%challenges%' WHERE island='%is%'";
					sqlComplete = sqlComplete.replaceAll("%database%", database);
					sqlComplete = sqlComplete.replaceAll("%is%", island.toString());
					
					String challs = "";
					for (Challenge c : challenges) {
						if (c.equals(challenges.get(0))) challs = c.toString();
						else challs = challs.concat("," + c.toString());
					}
					sqlComplete = sqlComplete.replaceAll("%challenges%", challs);
					
					conn.createStatement().executeUpdate(sqlComplete);
				} catch (SQLException e) {e.printStackTrace();}
				
			}
		};
		
		query.add(r);
		
	}
	
	@Override
	public void setRank(UUID island, int newRank) {
		BukkitRunnable r = new BukkitRunnable() {
			
			@Override
			public void run() {
				
				try {
					String sqlComplete = "UPDATE %database%.data SET grade=%rank% WHERE island='%is%'";
					sqlComplete = sqlComplete.replaceAll("%database%", database);
					sqlComplete = sqlComplete.replaceAll("%is%", island.toString());
					sqlComplete = sqlComplete.replaceAll("%rank%", String.valueOf(newRank));
					
					conn.createStatement().executeUpdate(sqlComplete);
					
				} catch (SQLException e) {e.printStackTrace();}
				
			}
		};
		
		query.add(r);
	}
	
	@Override
	public void saveIsland(DodgeIsland island) {
		
	}
}
