package me.alchemi.dodgechallenger.listeners.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.dodgechallenger.Config;
import me.alchemi.dodgechallenger.events.DeRankEvent;
import me.alchemi.dodgechallenger.managers.IslandManager;
import me.alchemi.dodgechallenger.managers.RankManager;
import me.alchemi.dodgechallenger.objects.Challenge;

public class CommandReset {

	public static boolean perform(CommandSender sender, OfflinePlayer player, String[] copyOfRange) {
		return perform(sender, player, copyOfRange, false);
	}

	public static boolean perform(CommandSender sender, OfflinePlayer player, String[] copyOfRange, boolean all) {

		
		if (player == null) {
			
			if (all) sender.sendMessage(Messenger.formatString(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + CommandAdmin.resetAllUsage));
			else sender.sendMessage(Messenger.formatString(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + CommandAdmin.resetUsage));
			
			return true;
		}
		
		if (!all) {
			
			if (Challenge.getChallengeFromID(copyOfRange[0]) == null) {
				sender.sendMessage(Messenger.formatString(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + CommandAdmin.resetUsage));
				return true;
			}
			
			Challenge c = Challenge.getChallengeFromID(copyOfRange[0]);
			
			IslandManager im = null;
			if (player.isOnline()) im = IslandManager.getByPlayer(player.getPlayer());
			else im = IslandManager.getByIsland(me.goodandevil.skyblock.api.SkyBlockAPI.getIslandManager().getIsland(player));
			
			im.removeChallenge(c);
			
			if (im.getRank() == 0) return true;
			
			im.checkRank();
			return true;
			
		} else {
			
			IslandManager im = null;
			if (player.isOnline()) im = IslandManager.getByPlayer(player.getPlayer());
			else im = IslandManager.getByIsland(me.goodandevil.skyblock.api.SkyBlockAPI.getIslandManager().getIsland(player));
			
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
