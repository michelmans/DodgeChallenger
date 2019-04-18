package com.alchemi.dodgechallenger.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

import com.alchemi.dodgechallenger.Config;
import com.alchemi.dodgechallenger.objects.Challenge;

public class RankManager implements Listener{

	private static List<RankManager> ranks = new ArrayList<RankManager>();
	public static Map<String, Integer> rankIDs = new HashMap<String, Integer>();
	
	private final ConfigurationSection section;
	
	private final String displayName;
	private final Material displayMaterial;
	private final String prefix;
	
	private List<Challenge> challenges = new ArrayList<Challenge>();
	private List<Challenge> requires = new ArrayList<Challenge>();
	private int size = 0;
	
	public RankManager(ConfigurationSection sec) {
		this.section = sec;
		this.displayName = sec.getString("name");
		this.prefix = Config.MESSAGES.RANK_TAG.value()
				.replace("$rank$", displayName)
				.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString());
		
		this.displayMaterial = Material.getMaterial(sec.getString("displayItem"));
		
		for (String chall : sec.getConfigurationSection("challenges").getValues(false).keySet()) {
			this.challenges.add(new Challenge(chall, sec.getConfigurationSection("challenges." + chall), this));
			if (!this.challenges.get(this.challenges.size()-1).hasOffset() &&
					Config.OPTIONS.RANK_IGNORE_STACKED.asBoolean()) this.size++;
		}
		
		for (String chall : sec.getStringList("requires")) {
			if (Challenge.getChallengeFromID(chall) != null) this.requires.add(Challenge.getChallengeFromID(chall));
		}
		
		ranks.add(this);
		rankIDs.put(displayName, ranks.size()-1);
	}
	
	public static List<RankManager> getRanks() {
		return ranks;
	}
	
	public static RankManager getFirst() {
		return ranks.get(0);
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public Material getDisplayMaterial() {
		return displayMaterial;
	}
	
	public int rank() {
		return getRankTier(displayName);
	}
	
	public static RankManager getRank(String rank) {
		return getRank(getRankTier(rank));
	}
	
	public static RankManager getRank(int tier) {
		return ranks.size() > tier ? ranks.get(tier) : ranks.get(ranks.size() - 1);
	}
	
	public static int getRankTier(String rank) {
		return rankIDs.size() > 0 ? rankIDs.containsKey(rank) ? rankIDs.get(rank) : 0 : 0; 
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public int getChalls() {
		return size;
	}
	
	public List<Challenge> getChallenges(){
		return challenges;
	}

	public ConfigurationSection getSection() {
		return section;
	}
	
	public List<Challenge> getRequires() {
		return requires;
	}

	public static void purge() {
		rankIDs.clear();
		ranks.clear();
		
		Challenge.purge();
	}
}
