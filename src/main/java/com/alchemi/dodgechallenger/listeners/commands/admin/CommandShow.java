package com.alchemi.dodgechallenger.listeners.commands.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.dodgechallenger.Config;
import com.alchemi.dodgechallenger.main;
import com.alchemi.dodgechallenger.gui.ChallengeGui;

public class CommandShow {

	public static boolean perform(CommandSender sender, Player player) {
		
		if (player == null) {
			sender.sendMessage(Messenger.cc(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + CommandAdmin.showUsage));
			return true;
		}
		
		(new ChallengeGui(main.instance, "&2&oChallenges", 54)).openGUI(sender, player);
		
		return true;
		
	}

}
