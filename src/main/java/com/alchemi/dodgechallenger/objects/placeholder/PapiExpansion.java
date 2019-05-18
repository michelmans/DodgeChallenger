package com.alchemi.dodgechallenger.objects.placeholder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.dodgechallenger.main;
import com.alchemi.dodgechallenger.managers.IslandManager;
import com.alchemi.dodgechallenger.managers.RankManager;
import com.alchemi.dodgechallenger.objects.Challenge;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.goodandevil.skyblock.api.SkyBlockAPI;

public class PapiExpansion extends PlaceholderExpansion{

	@Override
	public boolean canRegister() {
		return true;
	}
	
	@Override
	public String getAuthor() {
		return "Alchemi";
	}

	@Override
	public String getIdentifier() {
		return main.getInstance().getName();
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String id) {

		if (id.equals("rank")) {
			IslandManager im = getIslandManagerFromPlayer(p);
			if (im == null) return "0";
			im.checkRank();
			return String.valueOf(im.getRank());
		} else if (id.equals("stringed_rank")) {
			IslandManager im = getIslandManagerFromPlayer(p);
			if (im == null) return Messenger.cc(RankManager.getFirst().getDisplayName());
			im.checkRank();
			return Messenger.cc(im.getRankManager().getDisplayName());
		} else if (id.equals("stringed_rank_no_format")) {
			IslandManager im = getIslandManagerFromPlayer(p);
			if (im == null) return RankManager.getFirst().getDisplayName().replaceAll("&[0123456789abcdefklmnor]", "");
			im.checkRank();
			return im.getRankManager().getDisplayName().replaceAll("&[0123456789abcdefklmnor]", "");
		} else if (id.equals("amount_challenges_completed")) {
			IslandManager im = getIslandManagerFromPlayer(p);
			if (im == null) return "0";
			
			List<Challenge> cs = new ArrayList<Challenge>();
			for (Challenge c : im.getChallenges()) {
				if (!cs.contains(c)) cs.add(c); 
			}
			return String.valueOf(cs.size());
		} else if (id.matches("(challenge_completed_)\\w+")) {
			String c = id.replace("challenge_completed_", "");
			if (Challenge.getChallengeFromID(c) == null) return "false";
			
			IslandManager im = getIslandManagerFromPlayer(p);
			if (im == null) return "false";
			return String.valueOf(im.getChallenges().contains(Challenge.getChallengeFromID(c)));
		}
		
		return null;
	}
	
	@Override
	public String onRequest(OfflinePlayer p, String id) {
		
		if (id.equals("rank")) {
			IslandManager im = getIslandManagerFromOfflinePlayer(p);
			if (im == null) return "0";
			im.checkRank();
			return String.valueOf(im.getRank());
		} else if (id.equals("stringed_rank")) {
			IslandManager im = getIslandManagerFromOfflinePlayer(p);
			if (im == null) return Messenger.cc(RankManager.getFirst().getDisplayName());
			im.checkRank();
			return Messenger.cc(im.getRankManager().getDisplayName());
		} else if (id.equals("stringed_rank_no_format")) {
			IslandManager im = getIslandManagerFromOfflinePlayer(p);
			if (im == null) return RankManager.getFirst().getDisplayName().replaceAll("&[0123456789abcdefklmnor]", "");
			im.checkRank();
			return im.getRankManager().getDisplayName().replaceAll("&[0123456789abcdefklmnor]", "");
		} else if (id.equals("amount_challenges_completed")) {
			IslandManager im = getIslandManagerFromOfflinePlayer(p);
			if (im == null) return "0";
			
			List<Challenge> cs = new ArrayList<Challenge>();
			for (Challenge c : im.getChallenges()) {
				if (!cs.contains(c)) cs.add(c); 
			}
			return String.valueOf(cs.size());
		} else if (id.matches("(challenge_completed_)\\w+")) {
			String c = id.replace("challenge_completed_", "");
			if (Challenge.getChallengeFromID(c) == null) return "false";
			
			IslandManager im = getIslandManagerFromOfflinePlayer(p);
			if (im == null) return "false";
			return String.valueOf(im.getChallenges().contains(Challenge.getChallengeFromID(c)));
		}
		
		return null;
	}

	private static IslandManager getIslandManagerFromPlayer(Player player) {
		return IslandManager.getByPlayer(player);
		
	}
	
	private static IslandManager getIslandManagerFromOfflinePlayer(OfflinePlayer player) {
		if (!me.goodandevil.skyblock.api.island.IslandManager.hasIsland(player)) return null;
		return new IslandManager(SkyBlockAPI.getImplementation().getIslandManager().loadIsland(player));
	}
	
}
