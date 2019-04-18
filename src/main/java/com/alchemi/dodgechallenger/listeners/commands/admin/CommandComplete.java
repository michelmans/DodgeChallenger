package com.alchemi.dodgechallenger.listeners.commands.admin;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.dodgechallenger.Config;
import com.alchemi.dodgechallenger.objects.Challenge;

public class CommandComplete {

	public static boolean perform(CommandSender sender, OfflinePlayer player, String[] copyOfRange) {
		if (player == null || copyOfRange.length < 1 || Challenge.getChallengeFromID(copyOfRange[0]) == null) {
			sender.sendMessage(Messenger.cc(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + CommandAdmin.completeUsage));
			return true;
		}
		
		Challenge c = Challenge.getChallengeFromID(copyOfRange[0]);
		c.forceComplete(player);
		
		return true;
	}

}
