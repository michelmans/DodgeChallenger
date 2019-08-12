package me.alchemi.dodgechallenger.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

import me.alchemi.dodgechallenger.Config.Messages;
import me.alchemi.dodgechallenger.Config.Options;
import me.alchemi.dodgechallenger.managers.RankManager;
import me.alchemi.dodgechallenger.objects.placeholder.Stringer;

public class Rank implements Listener{

	private final ConfigurationSection section;
	
	private final String displayName;
	private final Material displayMaterial;
	private final String prefix;
	
	private List<Challenge> challenges = new ArrayList<Challenge>();
	private List<Challenge> requires = new ArrayList<Challenge>();
	private int size = 0;
	
	private int id;
	
	public Rank(ConfigurationSection sec) {
		this.section = sec;
		
		this.displayName = sec.getString("name");
		this.prefix = new Stringer(Messages.RANK_TAG).rank(this).create();
		
		this.displayMaterial = Material.getMaterial(sec.getString("displayItem"));
		
		for (String chall : sec.getConfigurationSection("challenges").getValues(false).keySet()) {
			this.challenges.add(new Challenge(chall, sec.getConfigurationSection("challenges." + chall), this));
			if (!this.challenges.get(this.challenges.size()-1).hasOffset() &&
					Options.RANK_IGNORE_STACKED.asBoolean()) this.size++;
		}
		
		for (String chall : sec.getStringList("requires")) {
			if (Challenge.getChallengeFromID(chall) != null) this.requires.add(Challenge.getChallengeFromID(chall));
		}
		this.id = RankManager.getManager().ranks();
		RankManager.getManager().registerRank(this);
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public Material getDisplayMaterial() {
		return displayMaterial;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public int getChalls() {
		return size;
	}
	
	public int getId() {
		return id;
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
}
