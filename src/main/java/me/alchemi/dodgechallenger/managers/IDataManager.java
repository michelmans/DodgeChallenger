package me.alchemi.dodgechallenger.managers;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;

import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.DodgeIsland;
import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.island.IslandEnvironment;
import me.goodandevil.skyblock.island.IslandWorld;

public interface IDataManager {

	public void newIsland(UUID island);
	
	public void removeIsland(UUID island);
	
	public int getRank(UUID island);
	
	public List<String> getCCompleted(UUID island);
	
	public void completeChallenge(UUID island, Challenge challenge);
	
	public void setChallenges(UUID island, List<Challenge> challenges);
	
	public void setRank(UUID island, int newRank);
	
	public int querySize();
	
	public void runQuery();
	
	public void saveIsland(DodgeIsland island);
	
	public static String islandToId(Island island) {
		Location loc = island.getIsland().getLocation(IslandWorld.Normal, IslandEnvironment.Island);
		return String.valueOf(loc.getBlockX()) + "-" + String.valueOf(loc.getBlockY()) + "-" + String.valueOf(loc.getBlockZ());
	}
	
}
