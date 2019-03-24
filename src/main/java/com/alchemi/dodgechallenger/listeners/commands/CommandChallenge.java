package com.alchemi.dodgechallenger.listeners.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.dodgechallenger.Config;
import com.alchemi.dodgechallenger.main;
import com.alchemi.dodgechallenger.gui.ChallengeGui;

import me.goodandevil.skyblock.api.island.IslandManager;

public class CommandChallenge implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player && sender.hasPermission("dodgec.challenges.view")) {
			if (IslandManager.hasIsland((Player)sender)) (new ChallengeGui(main.instance, "&2&oChallenges", 54)).openGUI((Player) sender);
			else sender.sendMessage(Messenger.cc(Config.MESSAGES.COMMANDS_NO_ISLAND.value()));
				
		} else sender.sendMessage(Config.MESSAGES.COMMANDS_NO_PERMISSION.value().replace("$sender$", label));
		
		return true;
	}

}
