package com.alchemi.dodgechallenger.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.alchemi.al.objects.meta.PersistentMeta;
import com.alchemi.dodgechallenger.Config;
import com.alchemi.dodgechallenger.main;
import com.alchemi.dodgechallenger.events.DeRankEvent;
import com.alchemi.dodgechallenger.events.RankupEvent;
import com.alchemi.dodgechallenger.meta.IslandMeta;
import com.alchemi.dodgechallenger.objects.Challenge;

import me.goodandevil.skyblock.api.island.Island;

public class IslandManager {

	private static HashMap<Island, IslandManager> islands = new HashMap<Island, IslandManager>();
	
	public static HashMap<Island, IslandManager> getIslands() {
		return islands;
	}

	private final Island island;
	private ArrayList<Challenge> challenges = new ArrayList<Challenge>();
	private int rank;
	
	public IslandManager(Island island) {
		
		if (!Config.DATABASE.ENABLED.asBoolean()) {
			((DataManager) main.dbm).loadIsland(island);
		}
		
		List<String> cc = main.dbm.getCCompleted(island);
		if (cc != null && !cc.isEmpty()) {
			for (String s : cc) {
				challenges.add(Challenge.getChallengeFromID(s));
			}
		}
		rank = main.dbm.getRank(island);
		islands.put(island, this);
		this.island = island;
		checkRank();
	}
	
	public IslandManager(me.goodandevil.skyblock.island.Island island) {
		
		if (!Config.DATABASE.ENABLED.asBoolean()) {
			((DataManager) main.dbm).loadIsland(island.getAPIWrapper());
		}
		
		List<String> cc = main.dbm.getCCompleted(island.getAPIWrapper());
		if (cc != null && !cc.isEmpty()) {
			for (String s : cc) {
				challenges.add(Challenge.getChallengeFromID(s));
			}
		}
		rank = main.dbm.getRank(island.getAPIWrapper());
		this.island = island.getAPIWrapper();
		
	}
	
	public int checkRank() {
		
		for (int i = RankManager.getRanks().size() - 1; i >= 0; i--) {
			RankManager rm = RankManager.getRank(i);
			int num = 0;
			
			for (Challenge c : rm.getChallenges()) {
				
				if (challenges.contains(c)) num++;
				
			}
			
			List<Challenge> req = rm.rank() < RankManager.getRanks().size() - 1 
					? RankManager.getRank(rm.rank() + 1).getRequires() : new ArrayList<Challenge>();
		
			if (num >= 8 - Config.OPTIONS.RANKLEEWAY.asInt() && challenges.containsAll(req)
					&& rm.rank() < RankManager.getRanks().size() - 1) {
				
				int oRank = rank;
				setRank(rm.rank() + 1);
				
				if (oRank < rank) Bukkit.getPluginManager().callEvent(new RankupEvent(island));
				else if ( oRank > rank) Bukkit.getPluginManager().callEvent(new DeRankEvent(island, RankManager.getRank(oRank)));
				
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
	
	public RankRequired challengeNeeded(RankManager rank) {
		int num = 8 - Config.OPTIONS.RANKLEEWAY.asInt();
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
	
	public static IslandManager getByIsland(Island island) {
		
		return islands.containsKey(island) ? islands.get(island) : null;
	}
	
	public static IslandManager getByPlayer(Player player) {
		return PersistentMeta.hasMeta(player, IslandMeta.class) ? (IslandManager) PersistentMeta.getMeta(player, IslandMeta.class).value() : null;
	}

	/**
	 * @return the challenges
	 */
	public ArrayList<Challenge> getChallenges() {
		return challenges;
	}
	
	public void addChallenge(Challenge challenge) {
		challenges.add(challenge);
	}
	
	public void removeChallenge(Challenge challenge) {
		if (challenges.contains(challenge)) {
			challenges.remove(challenge);
			main.dbm.setChallenges(island, challenges);
		}
	}

	/**
	 * @return the island
	 */
	public Island getIsland() {
		return island;
	}

	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}
	
	public RankManager getRankManager() {
		if (RankManager.getRanks().size() <= rank) return RankManager.getRank(RankManager.getRanks().size() - 1);
		return RankManager.getRank(rank);
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public static void purge() {
		islands.clear();
	}
	
	public void save() {
		main.dbm.saveIsland(this);
	}
	
	public void remove() {
		islands.remove(island);
	}
	
}
