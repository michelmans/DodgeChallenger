package com.alchemi.dodgechallenger.listeners.commands.admin;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.dodgechallenger.Config;

public class CommandAdmin implements CommandExecutor{

	public static final String completeUsage = "&9/chadmin <player> complete <challenge>";
	public static final String rankUsage = "&9/chadmin <player> rank <rank>";
	public static final String resetUsage = "&9/chadmin <player> reset <challenge>";
	public static final String resetAllUsage = "&9/chadmin <player> resetall";
	public static final String showUsage = "&9/chadmin <player> show";
	public static final String reloadUsage = "&9/chadmin reload";
	
	public static final String completeDesc = "&aComplete a player's challenge.";
	public static final String rankDesc = "&aComplete all challenges for the player in a rank.";
	public static final String resetDesc = "&aReset a player's challenge.";
	public static final String resetAllDesc = "&aReset all challegnes for a player.";
	public static final String showDesc = "&aShow the challenges pages of a player.";
	public static final String reloadDesc = "&aReload all configs.";
	
	public static final String help = "&6==========&9Admin Commands&6==========\n"
					+ completeUsage + "\n    " + completeDesc + "\n"
					+ rankUsage + "\n    " + rankDesc + "\n"
					+ resetUsage + "\n    " + resetDesc + "\n"
					+ resetAllUsage + "\n    " + resetAllDesc + "\n"
					+ showUsage + "\n    " + showDesc + "\n"
					+ reloadUsage + "\n    " + reloadDesc + "\n"
									+ "&6=================================";
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (sender.hasPermission("dodgec.admin") || !(sender instanceof Player) || sender.isOp()) {
			if (args.length < 1) {
				sender.sendMessage(Messenger.cc(help));
			} else if (args.length == 1) {
				if (args[0].equals("reload")) {
					Config.reload();
					sender.sendMessage("Configs reloaded");
					return true;
				}
				
				Player player = Bukkit.getPlayer(args[0]);
				return CommandShow.perform(sender, player);
			} else if (args.length >= 2) {
				Player player = Bukkit.getPlayer(args[0]);
				
				if (args[1].equals("complete")) {
					return CommandComplete.perform(sender, player, Arrays.copyOfRange(args, 2, args.length));
				} else if (args[1].equals("rank")) {
					return CommandRank.perform(sender, player, Arrays.copyOfRange(args, 2, args.length));
				} else if (args[1].equals("reset")) {
					return CommandReset.perform(sender, player, Arrays.copyOfRange(args, 2, args.length));
				} else if (args[1].equals("resetall")) {
					return CommandReset.perform(sender, player, Arrays.copyOfRange(args, 2, args.length), true);
				} else if (args[1].equals("show")) {
					return CommandShow.perform(sender, player);
				} else {
					sender.sendMessage(Messenger.cc(help));
					return true;
				}
			}
		}
		
		return true;
	}

}
