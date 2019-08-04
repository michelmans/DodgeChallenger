package me.alchemi.dodgechallenger.listeners.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.dodgechallenger.Config;
import me.alchemi.dodgechallenger.gui.ChallengeGui;
import me.goodandevil.skyblock.api.island.IslandManager;

public class CommandChallenge implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (sender instanceof Player && sender.hasPermission("dodgec.challenges.view")) {
			if (IslandManager.hasIsland((Player)sender)) new ChallengeGui((Player) sender);
			else sender.sendMessage(Messenger.formatString(Config.MESSAGES.COMMANDS_NO_ISLAND.value()));
				
		} else sender.sendMessage(Config.MESSAGES.COMMANDS_NO_PERMISSION.value().replace("$sender$", label));
		
		return true;
	}

}
