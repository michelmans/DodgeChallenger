package me.alchemi.dodgechallenger.listeners.commands.admin;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.dodgechallenger.Config.Messages;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.managers.RankManager;
import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.Rank;
import me.alchemi.dodgechallenger.objects.placeholder.Stringer;

public class CommandRank {

	public static boolean perform(CommandSender sender, OfflinePlayer player, String[] copyOfRange) {
		
		if (player == null || copyOfRange.length < 1) {
			sender.sendMessage(Messenger.formatString(Messages.COMMANDS_WRONGFORMAT.value() + CommandAdmin.rankUsage));
			return true;
		}
		
		try {Integer.valueOf(copyOfRange[0]); } catch(NumberFormatException e) { 
			sender.sendMessage(Messenger.formatString(Messages.COMMANDS_WRONGFORMAT.value() + CommandAdmin.rankUsage)); 
			return true;                                         
		}
		
		Rank rank = RankManager.getManager().getRank(Integer.valueOf(copyOfRange[0]));
		
		if(rank == null) {
			sender.sendMessage(Messenger.formatString(Messages.COMMANDS_WRONGFORMAT.value() + CommandAdmin.rankUsage));
			return true;
		}
		
		Dodge.getInstance().getMessenger().sendMessage(new Stringer(Messages.ADMIN_COMPLETE_RANK)
				.rank(rank)
				.player(player.getName()), sender);
		
		for (Challenge c : rank.getChallenges()) c.forceComplete(player);
		
		return true;
	}

}
