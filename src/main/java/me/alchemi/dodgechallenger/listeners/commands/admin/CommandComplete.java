package me.alchemi.dodgechallenger.listeners.commands.admin;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.dodgechallenger.Config.Messages;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.placeholder.Stringer;

public class CommandComplete {

	public static boolean perform(CommandSender sender, OfflinePlayer player, String[] copyOfRange) {
		if (player == null || copyOfRange.length < 1 || Challenge.getChallengeFromID(copyOfRange[0]) == null) {
			sender.sendMessage(Messenger.formatString(Messages.COMMANDS_WRONGFORMAT.value() + CommandAdmin.completeUsage));
			return true;
		}
		
		Challenge c = Challenge.getChallengeFromID(copyOfRange[0]);
		Dodge.getInstance().getMessenger().sendMessage(new Stringer(Messages.ADMIN_COMPLETE_CHALLENGE)
				.challenge(c)
				.player(player.getName()), sender);
		c.forceComplete(player);
		
		return true;
	}

}
