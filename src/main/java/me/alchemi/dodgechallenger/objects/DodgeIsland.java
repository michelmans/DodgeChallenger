package me.alchemi.dodgechallenger.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.alchemi.dodgechallenger.Config;
import me.alchemi.dodgechallenger.Config.DataBase;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.events.DeRankEvent;
import me.alchemi.dodgechallenger.events.RankupEvent;
import me.alchemi.dodgechallenger.managers.ConfigurationManager;
import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import me.alchemi.dodgechallenger.managers.RankManager;
import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.island.Island;

public class DodgeIsland {
	
	private final UUID island;
	private Container<Challenge> challenges = new Container<Challenge>();
	private Rank rank;
	
	public DodgeIsland(UUID island) {
		
		this.island = island;
		
		if (!DataBase.ENABLED.asBoolean()) {
			((ConfigurationManager) Dodge.dataManager).loadIsland(island);
		}
		
		challenges = Dodge.dataManager.getCompletedChallenges(island);
		
		rank = RankManager.getManager().getRank(Dodge.dataManager.getRank(island));
		DodgeIslandManager.getManager().registerIsland(this);
		
	}
	
	public DodgeIsland(UUID island, boolean offline) {
		
		this.island = island;
		
		if (!DataBase.ENABLED.asBoolean()) {
			((ConfigurationManager) Dodge.dataManager).loadIsland(island);
		}
		
		challenges = Dodge.dataManager.getCompletedChallenges(island);
		
		rank = RankManager.getManager().getRank(Dodge.dataManager.getRank(island));
		if (offline) DodgeIslandManager.getManager().registerIsland(this);
		
	}
	
	public Rank checkRank() {
		
		for (Rank rank : RankManager.getManager().getRanksReverse()) {
			
			List<Challenge> requiredChallenges = RankManager.getManager().isLast(rank) 
					? RankManager.getManager().getNextRank(rank).getRequires() : new ArrayList<Challenge>();
			
			if (!challenges.containsAll(requiredChallenges)) continue;
			
			int num = 0;
			
			for (Challenge c : rank.getChallenges()) {
				
				if (challenges.contains(c)) num++;
				
			}
		
			if (num >= 8 - Config.Options.RANKLEEWAY.asInt() && !RankManager.getManager().isLast(rank)) {
				
				Rank oRank = this.rank;
				setRank(RankManager.getManager().getNextRank(rank));
				
				if (oRank.getId() < this.rank.getId()) Bukkit.getPluginManager().callEvent(new RankupEvent(island));
				else if ( oRank.getId() > this.rank.getId()) Bukkit.getPluginManager().callEvent(new DeRankEvent(island, oRank));
				
				return rank;
			}
			
		}
		return rank;
		
	}
	
	public class RankRequired{ 
		
		final List<Challenge> challenges;
		final int amount;
		
		public RankRequired(List<Challenge> challenges, int amount) {
			this.challenges = challenges;
			this.amount = amount;
		}
		
		public int getAmount() {
			return amount;
		}
		
		public List<Challenge> getChallenges() {
			return challenges;
		}
		
	}
	
	public RankRequired challengeNeeded(Rank rank) {
		int num = 8 - Config.Options.RANKLEEWAY.asInt();
		for (Challenge c : rank.getChallenges()) {
			
			if (challenges.contains(c) && num != 0) num--;
			
		}
		
		List<Challenge> cRequired = new ArrayList<Challenge>();
		
		if ( !challenges.containsAll(rank.getRequires())) {
			
			for (Challenge c : rank.getRequires()) {
				
				if (!challenges.contains(c)) cRequired.add(c);
				
			}
			
		}
		
		return new RankRequired(cRequired, num);
	}
	
		/**
	 * @return the challenges
	 */
	public Container<Challenge> getChallenges() {
		return challenges;
	}
	
	public void addChallenge(Challenge challenge) {
		challenges.add(challenge);
	}
	
	public void removeChallenge(Challenge challenge) {
		if (challenges.contains(challenge)) {
			challenges.remove(challenge);
			Dodge.dataManager.setChallenges(island, challenges);
		}
	}
	
	public void clearChallenges() {
		challenges.clear();
		Dodge.dataManager.setChallenges(island, challenges);
	}

	/**
	 * @return the island
	 */
	public UUID getIsland() {
		return island;
	}
	
	public Island getFabledIsland() {
		return SkyBlockAPI.getIslandManager().getIslandByUUID(island);
	}

	/**
	 * @return the rank
	 */
	public Rank getRank() {
		return rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(Rank rank) {
		this.rank = rank;
	}
	
	public void save() {
		Dodge.dataManager.saveIsland(this);
	}
	
	public void remove() {
		DodgeIslandManager.getManager().removeIsland(this.island);
	}
	
	@Override
	public String toString() {
		return "DodgeIsland{Owner:" + Bukkit.getOfflinePlayer(getFabledIsland().getOwnerUUID()).getName() + ", IslandUUID:" + island.toString() + ", Challenges:" + challenges.toString() + "}";
	}
}
