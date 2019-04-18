package com.alchemi.dodgechallenger.listeners.commands.admin;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.dodgechallenger.Config;
import com.alchemi.dodgechallenger.gui.ChallengeGui;

public class CommandShow {

	public static boolean perform(CommandSender sender, OfflinePlayer player) {
		
		if (player == null) {
			sender.sendMessage(Messenger.cc(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + CommandAdmin.showUsage));
			return true;
		}
		
		new ChallengeGui(player, sender);
		
		return true;
		
	}

}
