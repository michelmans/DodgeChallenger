package me.alchemi.dodgechallenger.listeners.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.dodgechallenger.Config;
import me.alchemi.dodgechallenger.Config.Messages;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.gui.ChallengeGui;
import me.alchemi.dodgechallenger.listeners.commands.admin.CommandAdmin;
import me.alchemi.dodgechallenger.objects.placeholder.Stringer;
import com.songoda.skyblock.api.island.IslandManager;

public class CommandChallenge implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length > 0) {
			CommandAdmin.onCommand(sender, command, label, args);
			return true;
		}
		
		if (sender instanceof Player && sender.hasPermission("dodgec.challenges.view")) {
			if (IslandManager.hasIsland((Player)sender)) new ChallengeGui((Player) sender);
			else sender.sendMessage(Messenger.formatString(Config.Messages.COMMANDS_NOISLAND.value()));
				
		} else Dodge.getInstance().getMessenger().sendMessage(new Stringer(Messages.COMMANDS_NOPERMISSION)
				.command(label), sender);
		
		return true;
	}

}
