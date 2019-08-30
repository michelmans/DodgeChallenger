package me.alchemi.dodgechallenger.managers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import me.alchemi.al.configurations.SexyConfiguration;
import me.alchemi.al.objects.Callback;
import me.alchemi.al.objects.handling.SexyRunnable;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.Container;
import me.alchemi.dodgechallenger.objects.DodgeIsland;
import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.island.Island;

public class ConfigurationManager implements IDataManager{

	private List<SexyRunnable> query = new ArrayList<SexyRunnable>();
	
	private Map<UUID, SexyConfiguration> loadedConfs = new HashMap<UUID, SexyConfiguration>();
	private final File database;
	
	public ConfigurationManager() {
		this.database = new File(Dodge.getInstance().getDataFolder(), "islands");
	}
	
	public void loadIsland(Island island) {
		File islandFile = new File(this.database, island.getIslandUUID().toString() + ".yml");
		
		loadedConfs.put(island.getIslandUUID(), SexyConfiguration.loadConfiguration(islandFile));
	}
	
	public void loadIsland(UUID islandUUID) {
		File islandFile = new File(database, islandUUID.toString() + ".yml");
		loadedConfs.put(islandUUID, SexyConfiguration.loadConfiguration(islandFile));		
	}
	
	@Override
	public void newIsland(UUID island) {
		
		SexyConfiguration c = SexyConfiguration.loadConfiguration(new File(this.database, island.toString() + ".yml"));
		c.set("owner", island.toString());
		c.set("rank", 0);
		c.set("completed", new Container<Challenge>());
		
		loadedConfs.put(island, c);
		
		new DodgeIsland(island);
		
		try {
			c.save();
		} catch (IOException e1) {}
	}
	
	@Override
	public void removeIsland(UUID island) {
		
		if (loadedConfs.get(island) != null) loadedConfs.get(island).getFile().delete();
		loadedConfs.remove(island);
		
	}
	
	@Override
	public int getRank(UUID island) {
		return loadedConfs.containsKey(island) ? loadedConfs.get(island).getInt("rank", 0) : 0;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Container<Challenge> getCompletedChallenges(UUID island) {
		return loadedConfs.containsKey(island) ? (Container<Challenge>) loadedConfs.get(island).get("completed") : new Container<Challenge>();
	}
	
	@Override
	public void completeChallenge(UUID island, Challenge chall) {
		
		if (!loadedConfs.containsKey(island)) return;
		
		SexyConfiguration c = loadedConfs.get(island);
		
		List<String> cc = c.getStringList("completed");
		cc.add(chall.toString());		
		c.set("owner", c.getString("owner"));
		c.set("rank", c.get("rank"));
		c.set("completed", cc);
		loadedConfs.put(island, c);
		
		try {
			c.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void setChallenges(UUID island, Container<Challenge> challenges) {
		if (!loadedConfs.containsKey(island)) return;
		
		SexyConfiguration config = loadedConfs.get(island);
		
		config.set("owner", config.get("owner"));
		config.set("rank", config.get("rank"));
		config.set("completed", challenges);
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
	public void setRank(UUID island, int newRank) {
		
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
	
	public int querySize() {
		return query == null ? 0 : query.size();
	}
	
	public void runQuery() {
		
		if (query == null || query.isEmpty()) return;
		
		for (SexyRunnable r : query) {
			r.run();
		}
		
	}
	
	@Override
	public void saveIsland(DodgeIsland island) {
		island.checkRank();
		SexyConfiguration c = loadedConfs.get(island.getIsland());
		c.set("owner", SkyBlockAPI.getIslandManager().getIslandByUUID(island.getIsland()).getOwnerUUID().toString());
		c.set("rank", island.getRank().getId());
		c.set("completed", island.getChallenges());
		try {
			c.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void getRankAsync(UUID island, Callback<Integer> callback) {
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				callback.call(getRank(island));
				
			}
		}.runTaskAsynchronously(Dodge.getInstance());
	}

	@Override
	public void getCompletedChallengesAsync(UUID island, Callback<Container<Challenge>> callback) {
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				
				callback.call(getCompletedChallenges(island));
				
			}
		}.runTaskAsynchronously(Dodge.getInstance());
		
	}
}
