package me.alchemi.dodgechallenger.listeners.commands.admin;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.Config.Messages;
import me.alchemi.dodgechallenger.events.DeRankEvent;
import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.DodgeIsland;
import me.alchemi.dodgechallenger.objects.Rank;
import me.alchemi.dodgechallenger.objects.placeholder.Stringer;
import me.goodandevil.skyblock.api.SkyBlockAPI;

public class CommandReset {

	public static boolean perform(CommandSender sender, OfflinePlayer player, String[] copyOfRange) {
		return perform(sender, player, copyOfRange, false);
	}

	public static boolean perform(CommandSender sender, OfflinePlayer player, String[] copyOfRange, boolean all) {

		
		if (player == null) {
			
			if (all) sender.sendMessage(Messenger.formatString(Messages.COMMANDS_WRONGFORMAT.value() + CommandAdmin.resetAllUsage));
			else sender.sendMessage(Messenger.formatString(Messages.COMMANDS_WRONGFORMAT.value() + CommandAdmin.resetUsage));
			
			return true;
		}
		
		DodgeIsland island = null;
		if (player.isOnline()) island = DodgeIslandManager.getManager().getByPlayer(player.getPlayer());
		else island = DodgeIslandManager.getManager().get(SkyBlockAPI.getIslandManager().getIsland(player));
		
		if (!all) {
			
			if (Challenge.getChallengeFromID(copyOfRange[0]) == null) {
				sender.sendMessage(Messenger.formatString(Messages.COMMANDS_WRONGFORMAT.value() + CommandAdmin.resetUsage));
				return true;
			}
			
			Challenge c = Challenge.getChallengeFromID(copyOfRange[0]);
			
			Dodge.getInstance().getMessenger().sendMessage(new Stringer(Messages.ADMIN_RESET)
					.player(player.getName())
					.challenge(c), sender);
			
			island.removeChallenge(c);
			
			if (island.getRank().getId() == 0) return true;
			
			island.checkRank();
			return true;
			
		} else {
			
			Dodge.getInstance().getMessenger().sendMessage(new Stringer(Messages.ADMIN_RESETALL)
					.player(player.getName()), sender);
			
			Rank oRank = island.getRank();
			island.clearChallenges();
			Bukkit.getPluginManager().callEvent(new DeRankEvent(island.getIsland(), oRank));
			return true;
			
		}
	}

}
