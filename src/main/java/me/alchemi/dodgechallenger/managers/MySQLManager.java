package me.alchemi.dodgechallenger.managers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import me.alchemi.al.database.Column;
import me.alchemi.al.database.ColumnModifier;
import me.alchemi.al.database.DataType;
import me.alchemi.al.database.Table;
import me.alchemi.al.database.mysql.MySQLDatabase;
import me.alchemi.al.objects.Callback;
import me.alchemi.al.objects.Container;
import me.alchemi.dodgechallenger.Config.Data;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.DodgeIsland;
import me.alchemi.dodgechallenger.objects.StorageSystem;

public class MySQLManager implements IDataManager {
	
	private MySQLDatabase database;
	
	private Table table;
	
	private Column islandUuid = new Column("island_uuid", DataType.TINYTEXT, ColumnModifier.NOT_NULL);
	private Column islandRank = new Column("island_rank", DataType.TINYINT, ColumnModifier.NOT_NULL, ColumnModifier.DEFAULT);
	private Column islandChallenges = new Column("island_challenges", DataType.LONGTEXT);
	
	public MySQLManager() {
		
		try {
			database = MySQLDatabase.newConnection(Dodge.getInstance(), Data.HOST.asString() + ":" + Data.PORT.asInt(), Data.DATABASE.asString(), Data.USERNAME.asString(), Data.PASSWORD.asString());
		} catch (SQLException e) {
			Data.STORAGE.set(StorageSystem.YML);
			Dodge.dataManager = new ConfigurationManager();
			Dodge.getInstance().getMessenger().print("MySQL database not reachable, switching to yml database.");
			e.printStackTrace();
			return;
		}
		
		islandRank.setDefValue(1);
		table = new Table("dodge_islands", islandUuid);
		table.addColumn(islandRank);
		table.addColumn(islandChallenges);
		
		database.createTable(table);
	}
	
	@Override
	public void newIsland(UUID island) {
		new DodgeIsland(island);
		addIsland(island, 0, new Container<Challenge>(Challenge.class));
	}

	private void addIsland(UUID island, int rank, Container<Challenge> challenges) {
		Map<Column, Object> islandSettings = new HashMap<Column, Object>();
		islandSettings.put(islandUuid, island.toString());
		islandSettings.put(islandRank, rank);
		islandSettings.put(islandChallenges, challenges.serialize_string());
		database.insertValues(table, islandSettings);
	}
	
	@Override
	public void removeIsland(UUID island) {
		database.removeRow(table, new HashMap<Column, Object>(){
			{
				put(islandUuid, island.toString());
			}
		});
	}

	@Override
	public void getRankAsync(UUID island, Callback<Integer> callback) {
		database.getValueAsync(table, islandRank, islandUuid, island.toString(), new Callback<ResultSet>() {
			
			@Override
			public void call(ResultSet callObject) {

				try {
					if (!callObject.isClosed() 
							&& callObject.first()) callback.call(callObject.getInt(islandRank.getName()));
				} catch (SQLException e) {
					e.printStackTrace();
					callback.call(0);
					
				}
				
			}
			
		});
	}
	
	@Override
	public int getRank(UUID island) {
		try {
			ResultSet result = database.getValue(table, islandRank, islandUuid, island.toString());
			
			if (result.next()) return result.getInt(islandRank.getName());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	@Override
	public void getCompletedChallengesAsync(UUID island, Callback<Container<Challenge>> callback) {
		
		database.getValueAsync(table, islandChallenges, islandUuid, island.toString(), new Callback<ResultSet>() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void call(ResultSet callObject) {
				
				try {
					
					if (callObject.next() && callObject.getString(islandChallenges.getName()) != null) callback.call((Container<Challenge>)Container.deserialize_string(callObject.getString(islandChallenges.getName())));
					else if (callObject.getString(islandChallenges.getName()) == null) callback.call(new Container<Challenge>(Challenge.class));
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
			}
			
		});
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Container<Challenge> getCompletedChallenges(UUID island) {
		
		try {
			ResultSet result = database.getValue(table, islandChallenges, islandUuid, island.toString());
			if (result == null) return new Container<Challenge>(Challenge.class);
			
			if (result.next()) return (Container<Challenge>) Container.deserialize_string(result.getString(islandChallenges.getName()));
			else return new Container<Challenge>(Challenge.class);
			
		} catch (SQLException e) {
			e.printStackTrace();
			return new Container<Challenge>(Challenge.class);
		}
		
	}
	
	@Override
	public void completeChallenge(UUID island, Challenge chall) {
		
		Container<Challenge> challenges = getCompletedChallenges(island);
		challenges.add(chall);
		
		setChallenges(island, challenges);
		
	}
	
	@Override
	public void setChallenges(UUID island, Container<Challenge> challenges) {
		
		database.updateValue(table, islandChallenges, challenges.serialize_string(), new HashMap<Column, Object>(){
			{
				put(islandUuid, island.toString());
			}
		});
		
	}
	
	@Override
	public void setRank(UUID island, int newRank) {
		
		database.updateValue(table, islandRank, newRank, new HashMap<Column, Object>(){
			{
				put(islandUuid, island.toString());
			}
		});
		
	}
	
	@Override
	public void saveIsland(DodgeIsland island) {}
	
	public MySQLDatabase getDatabase() {
		return database;
	}
}
