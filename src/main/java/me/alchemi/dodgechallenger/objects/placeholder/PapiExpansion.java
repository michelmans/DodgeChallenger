package me.alchemi.dodgechallenger.objects.placeholder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import me.alchemi.dodgechallenger.managers.RankManager;
import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.DodgeIsland;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.goodandevil.skyblock.api.island.IslandManager;
import net.md_5.bungee.api.ChatColor;

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
		return Dodge.getInstance().getName();
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}
	
	@Override
	public String onPlaceholderRequest(Player p, String id) {

		if (id.equals("rank")) {
			DodgeIsland island = getIslandManagerFromPlayer(p);
			if (island == null) return "0";
			return String.valueOf(island.getRank().getId());
			
		} else if (id.equals("stringed_rank")) {
			DodgeIsland island = getIslandManagerFromPlayer(p);
			if (island == null) return Messenger.formatString(RankManager.getManager().getFirst().getDisplayName());
			return Messenger.formatString(island.getRank().getDisplayName());
			
		} else if (id.equals("stringed_rank_no_format")) {
			DodgeIsland island = getIslandManagerFromPlayer(p);
			if (island == null) return ChatColor.stripColor(RankManager.getManager().getFirst().getDisplayName());
			return ChatColor.stripColor(island.getRank().getDisplayName());
			
		} else if (id.equals("prefix")) {
			DodgeIsland island = getIslandManagerFromPlayer(p);
			if (island == null) return RankManager.getManager().getFirst().getPrefix().trim();
			return island.getRank().getPrefix().trim();
			
		} else if (id.equals("amount_challenges_completed")) {
			DodgeIsland island = getIslandManagerFromPlayer(p);
			if (island == null) return "0";
			
			List<Challenge> cs = new ArrayList<Challenge>();
			for (Challenge c : island.getChallenges()) {
				if (!cs.contains(c)) cs.add(c); 
			}
			return String.valueOf(cs.size());
			
		} else if (id.matches("(challenge_completed_)\\w+")) {
			String c = id.replace("challenge_completed_", "");
			if (Challenge.getChallengeFromID(c) == null) return "false";
			
			DodgeIsland island = getIslandManagerFromPlayer(p);
			if (island == null) return "false";
			return String.valueOf(island.getChallenges().contains(Challenge.getChallengeFromID(c)));
			
		}
		
		return null;
	}
	
	@Override
	public String onRequest(OfflinePlayer p, String id) {
		
		if (id.equals("rank")) {
			DodgeIsland island = getIslandManagerFromOfflinePlayer(p);
			if (island == null) return "0";
			return String.valueOf(island.getRank().getId());
			
		} else if (id.equals("stringed_rank")) {
			DodgeIsland island = getIslandManagerFromOfflinePlayer(p);
			if (island == null) return Messenger.formatString(RankManager.getManager().getFirst().getDisplayName());
			return Messenger.formatString(island.getRank().getDisplayName());
			
		} else if (id.equals("stringed_rank_no_format")) {
			DodgeIsland island = getIslandManagerFromOfflinePlayer(p);
			if (island == null) return ChatColor.stripColor(RankManager.getManager().getFirst().getDisplayName());
			return ChatColor.stripColor(island.getRank().getDisplayName());
			
		} else if (id.equals("prefix")) {
			DodgeIsland island = getIslandManagerFromOfflinePlayer(p);
			if (island == null) return RankManager.getManager().getFirst().getPrefix();
			return island.getRank().getPrefix();
			
		} else if (id.equals("amount_challenges_completed")) {
			DodgeIsland island = getIslandManagerFromOfflinePlayer(p);
			if (island == null) return "0";
			
			List<Challenge> cs = new ArrayList<Challenge>();
			for (Challenge c : island.getChallenges()) {
				if (!cs.contains(c)) cs.add(c); 
			}
			return String.valueOf(cs.size());
			
		} else if (id.matches("(challenge_completed_)\\w+")) {
			String c = id.replace("challenge_completed_", "");
			if (Challenge.getChallengeFromID(c) == null) return "false";
			
			DodgeIsland island = getIslandManagerFromOfflinePlayer(p);
			if (island == null) return "false";
			return String.valueOf(island.getChallenges().contains(Challenge.getChallengeFromID(c)));
			
		}
		
		return null;
	}

	private static DodgeIsland getIslandManagerFromPlayer(Player player) {
		return DodgeIslandManager.getManager().getByPlayer(player);
		
	}
	
	private static DodgeIsland getIslandManagerFromOfflinePlayer(OfflinePlayer player) {
		if (!IslandManager.hasIsland(player)) return null;
		return new DodgeIsland(DodgeIslandManager.getIslandUUID(player));
	}
	
}
