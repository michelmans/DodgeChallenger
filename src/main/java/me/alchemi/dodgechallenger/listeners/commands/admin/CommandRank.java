package me.alchemi.dodgechallenger.listeners.commands.admin;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.dodgechallenger.Config;
import me.alchemi.dodgechallenger.managers.RankManager;
import me.alchemi.dodgechallenger.objects.Challenge;

public class CommandRank {

	public static boolean perform(CommandSender sender, OfflinePlayer player, String[] copyOfRange) {
		
		if (player == null || copyOfRange.length < 1) {
			sender.sendMessage(Messenger.formatString(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + CommandAdmin.rankUsage));
			return true;
		}
		
		try {Integer.valueOf(copyOfRange[0]); } catch(NumberFormatException e) { 
			sender.sendMessage(Messenger.formatString(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + CommandAdmin.rankUsage)); 
			return true;                                         
		}
		
		RankManager rm = RankManager.getRank(Integer.valueOf(copyOfRange[0]));
		
		if(rm == null) {
			sender.sendMessage(Messenger.formatString(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + CommandAdmin.rankUsage));
			return true;
		}
		
		for (Challenge c : rm.getChallenges()) c.forceComplete(player);
		
		return true;
	}

}
