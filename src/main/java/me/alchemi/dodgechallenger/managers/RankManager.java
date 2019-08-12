package me.alchemi.dodgechallenger.managers;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.Rank;

public class RankManager {

	private List<Rank> ranks = new ArrayList<Rank>();
	
	private static RankManager manager;
	
	public RankManager() {
		manager = this;
	}
	
	public void registerRank(Rank rank) {
		if (!ranks.contains(rank)) ranks.add(rank);
	}
	
	public List<Rank> getRanks() {
		return ranks;
	}
	
	public int ranks() {
		return ranks.size();
	}
	
	public Rank getFirst() {
		return ranks.get(0);
	}
	
	public boolean isLast(Rank rank) {
		return ranks.get(ranks.size() - 1).equals(rank);
	}
	
	public Rank getNextRank(Rank rank) {
		return !isLast(rank) ? ranks.get(ranks.indexOf(rank) + 1) : rank;
	}
	
	public Rank getPreviousRank(Rank rank) {
		return !rank.equals(getFirst()) ? ranks.get(ranks.indexOf(rank) - 1) : rank;
	}

	public Rank getRank(String rankString) {
		for (Rank rank : ranks) {
			if (rank.getDisplayName().equals(rankString)) return rank;
		}
		return null;
	}
	
	public Rank getRank(int tier) {
		if (ranks.size() == 0) return null;
		return ranks.size() > tier ? ranks.get(tier) : ranks.get(ranks.size() - 1);
	}
	
	public List<Rank> getRanksReverse(){
		return Lists.reverse(ranks);
	}
	
	public void purge() {
		ranks.clear();
		
		Challenge.purge();
	}

	public static RankManager getManager() {
		if (manager == null) manager = new RankManager();
		return manager;
	}
	
}
