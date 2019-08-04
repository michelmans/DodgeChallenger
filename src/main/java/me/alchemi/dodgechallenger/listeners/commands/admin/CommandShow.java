package me.alchemi.dodgechallenger.listeners.commands.admin;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.dodgechallenger.Config;
import me.alchemi.dodgechallenger.gui.ChallengeGui;

public class CommandShow {

	public static boolean perform(CommandSender sender, OfflinePlayer player) {
		
		if (player == null) {
			sender.sendMessage(Messenger.formatString(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + CommandAdmin.showUsage));
			return true;
		}
		
		new ChallengeGui(player, sender);
		
		return true;
		
	}

}
