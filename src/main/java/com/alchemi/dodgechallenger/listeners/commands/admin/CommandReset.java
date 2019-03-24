package com.alchemi.dodgechallenger.listeners.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.dodgechallenger.Config;
import com.alchemi.dodgechallenger.events.DeRankEvent;
import com.alchemi.dodgechallenger.managers.IslandManager;
import com.alchemi.dodgechallenger.managers.RankManager;
import com.alchemi.dodgechallenger.objects.Challenge;

import me.goodandevil.skyblock.api.SkyBlockAPI;

public class CommandReset {

	public static boolean perform(CommandSender sender, Player player, String[] copyOfRange) {
		return perform(sender, player, copyOfRange, false);
	}

	public static boolean perform(CommandSender sender, Player player, String[] copyOfRange, boolean all) {

		
		if (player == null) {
			if (all) sender.sendMessage(Messenger.cc(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + CommandAdmin.resetAllUsage));
			else sender.sendMessage(Messenger.cc(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + CommandAdmin.resetUsage));
			
			return true;
		}
		
		if (!all) {
			
			if (Challenge.getChallengeFromID(copyOfRange[0]) == null) {
				sender.sendMessage(Messenger.cc(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + CommandAdmin.resetUsage));
				return true;
			}
			
			Challenge c = Challenge.getChallengeFromID(copyOfRange[0]);
			IslandManager im = IslandManager.getByIsland(SkyBlockAPI.getIslandManager().getIsland(player));
			im.removeChallenge(c);
			
			if (im.getRank() == 0) return true;
			
			im.checkRank();
			return true;
			
		} else {
			IslandManager im = IslandManager.getByIsland(SkyBlockAPI.getIslandManager().getIsland(player));
			while (im.getChallenges().size() > 0) {
				im.removeChallenge(im.getChallenges().get(0));
			}
			
			int oRank = im.getRank();
			im.setRank(0);
			Bukkit.getPluginManager().callEvent(new DeRankEvent(im.getIsland(), RankManager.getRank(oRank)));
			return true;
		}
	}

}
