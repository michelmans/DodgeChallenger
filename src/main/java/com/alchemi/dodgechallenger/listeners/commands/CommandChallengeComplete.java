package com.alchemi.dodgechallenger.listeners.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.dodgechallenger.Config;
import com.alchemi.dodgechallenger.main;
import com.alchemi.dodgechallenger.objects.Challenge;

import me.goodandevil.skyblock.api.island.IslandManager;

public class CommandChallengeComplete implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player && sender.hasPermission("dodgec.challenges.complete")) {
			Player player = (Player) sender;
			if (IslandManager.hasIsland(player)) {
				if (args.length == 1) {
					if (Challenge.getChallengeFromID(args[0]) != null) {
						Challenge.getChallengeFromID(args[0]).complete(player);
					} else {
						player.sendMessage(Messenger.cc(Config.MESSAGES.COMMANDS_NO_CHALLENGE.value()
								.replace("$player$", player.getDisplayName())
								.replace("$challenge$", args[0])));
					}
				} else {
					player.sendMessage(Messenger.cc(Config.MESSAGES.COMMANDS_WRONG_FORMAT.value() + main.instance.getCommand("challengecomplete").getUsage()));
				}
			} else {
				player.sendMessage(Messenger.cc(Config.MESSAGES.COMMANDS_NO_ISLAND.value()));
			}
		} else {
			sender.sendMessage(Config.MESSAGES.COMMANDS_NO_PERMISSION.value().replace("$sender$", label));
		}
		return true;
	}

}
