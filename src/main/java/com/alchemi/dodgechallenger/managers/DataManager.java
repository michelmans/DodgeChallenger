package com.alchemi.dodgechallenger.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alchemi.al.configurations.SexyConfiguration;
import com.alchemi.al.objects.SexyRunnable;
import com.alchemi.dodgechallenger.main;
import com.alchemi.dodgechallenger.objects.Challenge;

import me.goodandevil.skyblock.api.island.Island;

public class DataManager extends DatabaseManager{

	private List<SexyRunnable> query = new ArrayList<SexyRunnable>();
	
	private Map<Island, SexyConfiguration> loadedConfs = new HashMap<Island, SexyConfiguration>();
	private final File database;
	
	public DataManager() {
		this.database = new File(main.instance.getDataFolder(), "islands");
	}
	
	public void loadIsland(Island island) {
		SexyConfiguration c = SexyConfiguration.loadConfiguration(new File(this.database, islandToId(island) + ".yml"));
		loadedConfs.put(island,  c);
	}
	
	@Override
	public void newIsland(Island island) {
		
		
		SexyConfiguration c = new SexyConfiguration(new File(this.database, islandToId(island) + ".yml"));
		c.set("owner", island.getOwnerUUID().toString());
		c.set("rank", 0);
		c.set("completed", new ArrayList<String>());
		
		loadedConfs.put(island, c);
		
		new IslandManager(island);
		
		try {
			c.save();
		} catch (IOException e1) {}
	}
	
	@Override
	public void removeIsland(Island island) {
		
		if (loadedConfs.get(island) != null) loadedConfs.get(island).getFile().delete();
		loadedConfs.remove(island);
		
	}
	
	@Override
	public int getRank(Island island) {
		return loadedConfs.containsKey(island) ? loadedConfs.get(island).getInt("rank", 0) : 0;
	}
	
	@Override
	public List<String> getCCompleted(Island island) {
		return loadedConfs.containsKey(island) ? loadedConfs.get(island).getStringList("completed") : new ArrayList<String>();
	}
	
	@Override
	public void completeChallenge(Island island, Challenge chall) {
		System.out.println("Completing " + chall.getDisplayName());
		
		if (!loadedConfs.containsKey(island)) return;
		
		SexyConfiguration c = loadedConfs.get(island);
		
		List<String> cc = c.getStringList("completed");
		cc.add(chall.toString());		
		c.set("owner", c.getString("owner"));
		c.set("rank", c.get("rank"));
		c.set("completed", cc);
		loadedConfs.put(island, c);
		
		query.add(new SexyRunnable() {
			
			@Override
			public void run(Object... args) {
				try {
					c.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	@Override
	public void setChallenges(Island island, List<Challenge> challenges) {
		if (!loadedConfs.containsKey(island)) return;
		
		SexyConfiguration config = loadedConfs.get(island);
		
		List<String> cc = new ArrayList<String>();
		for (Challenge c : challenges) {
			if (c == null) continue;
			cc.add(c.toString());
		}
		config.set("owner", config.get("owner"));
		config.set("rank", config.get("rank"));
		config.set("completed", cc);
		loadedConfs.put(island, config);
		
		query.add(new SexyRunnable() {
			
			@Override
			public void run(Object... args) {
				try {
					config.save();
				} catch (IOException e) {e.printStackTrace();}
			}
		});
	}
	
	
	@Override
	public void setRank(Island island, int newRank) {
		
		if (!loadedConfs.containsKey(island)) return;
		
		SexyConfiguration c = loadedConfs.get(island);
		c.set("owner", c.get("owner"));
		c.set("rank", newRank);
		c.set("completed", c.get("completed"));
		loadedConfs.put(island, c);
		
		query.add(new SexyRunnable() {
			
			@Override
			public void run(Object... args) {
				try {
					c.save(c.getFile());
				} catch (IOException e) {}
			}
		});
		
	}
	
	@Override
	public int querySize() {
		return query == null ? 0 : query.size();
	}
	
	@Override
	public void runQuery() {
		
		if (query == null || query.isEmpty()) return;
		
		for (SexyRunnable r : query) {
			r.run();
		}
		
	}
	
	@Override
	public void saveIsland(IslandManager island) {
		island.checkRank();
		SexyConfiguration c = loadedConfs.get(island.getIsland());
		c.set("owner", island.getIsland().getOwnerUUID().toString());
		c.set("rank", island.getRank());
		
		List<String> cc = new ArrayList<String>();
		for (Challenge ch : island.getChallenges()) cc.add(ch.toString());
		
		c.set("completed", cc);
		try {
			c.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
