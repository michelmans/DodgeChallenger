package me.alchemi.dodgechallenger.objects.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import me.alchemi.dodgechallenger.managers.RankManager;
import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.DodgeIsland;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
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
			try {
				DodgeIsland island = getIslandManagerFromPlayer(p);
				return String.valueOf(island.getRank().getId()); 
			} catch (IllegalAccessError e) {
				return "0";
			}
			
		} else if (id.equals("stringed_rank")) {
			try {
				DodgeIsland island = getIslandManagerFromPlayer(p);
				return Messenger.formatString(island.getRank().getDisplayName());
			} catch(IllegalAccessError e) {
				return Messenger.formatString(RankManager.getManager().getFirst().getDisplayName());
			}
			
		} else if (id.equals("stringed_rank_no_format")) {
			try {
				DodgeIsland island = getIslandManagerFromPlayer(p);
				return ChatColor.stripColor(island.getRank().getDisplayName());
			} catch (IllegalAccessError e) {
				return ChatColor.stripColor(RankManager.getManager().getFirst().getDisplayName());
			}
			
		} else if (id.equals("prefix")) {
			try {
				DodgeIsland island = getIslandManagerFromPlayer(p);
				return island.getRank().getPrefix().trim();
			} catch (IllegalAccessError e) {
				return RankManager.getManager().getFirst().getPrefix().trim();
			}
			
		} else if (id.equals("amount_challenges_completed")) {
			try{
				DodgeIsland island = getIslandManagerFromPlayer(p);
				return String.valueOf(island.getChallenges().size());
			} catch (IllegalAccessError e) {
				return "0";
			}
			
		} else if (id.matches("(challenge_completed_)\\w+")) {
			String c = id.replace("challenge_completed_", "");
			if (Challenge.getChallengeFromID(c) == null) return "false";
			
			try {
				DodgeIsland island = getIslandManagerFromPlayer(p);
				return String.valueOf(island.getChallenges().contains(Challenge.getChallengeFromID(c)));
			} catch (IllegalAccessError e) {
				return "false";
			}
			
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
			
			return String.valueOf(island.getChallenges().size());
			
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
		return DodgeIslandManager.getManager().getByPlayer(player);
	}
	
}
