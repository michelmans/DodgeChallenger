package me.alchemi.dodgechallenger.listeners.commands.admin;

import java.util.Arrays;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.Library;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.dodgechallenger.Dodge;

public class CommandAdmin {

	public static final String completeUsage = "&9/c admin complete <player> <challenge>";
	public static final String rankUsage = "&9/c admin rank <player> <rank>";
	public static final String resetUsage = "&9/c admin reset <player> <challenge>";
	public static final String resetAllUsage = "&9/c admin resetall <player>";
	public static final String showUsage = "&9/c admin show <player>";
	public static final String reloadUsage = "&9/c admin reload";
	public static final String defaultUsage = "&9/c admin defaults";
	public static final String debugUsage = "&9/c admin debug <player>";
	
	public static final String completeDesc = "&aComplete a player's challenge.";
	public static final String rankDesc = "&aComplete all challenges for the player in a rank.";
	public static final String resetDesc = "&aReset a player's challenge.";
	public static final String resetAllDesc = "&aReset all challegnes for a player.";
	public static final String showDesc = "&aShow the challenges pages of a player.";
	public static final String reloadDesc = "&aReload all configs.";
	public static final String defaultDesc = "&aReset all configs to default.";
	public static final String debugDesc = "&aDebug a player's island.";
	
	public static final String help = "&6==========&9Admin Commands&6==========\n"
									+ completeUsage + ">    " + completeDesc + "\n"
									+ rankUsage + ">    " + rankDesc + "\n"
									+ resetUsage + ">    " + resetDesc + "\n"
									+ resetAllUsage + ">    " + resetAllDesc + "\n"
									+ showUsage + ">    " + showDesc + "\n"
									+ reloadUsage + ">    " + reloadDesc + "\n"
									+ defaultUsage + ">    " + defaultDesc + "\n"
									+ debugUsage + ">    " + debugDesc + "\n"
									+ "&6=================================";
	
	public static void onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission("dodgec.admin") || !(sender instanceof Player) || sender.isOp()) {
			if (args.length < 2) {
				sender.sendMessage(Messenger.formatString(help));
			} else if (args.length == 2) {
				
				if (args[1].equals("reload")) {
					Dodge.getInstance().conf.reload();
					sender.sendMessage("Configs reloaded");
					return;
				} else if (args[1].equals("defaults")) {
					Dodge.getInstance().conf.revertDefault();
					return;
				} else if (args[1].equals("debug") && sender instanceof Player) {
					CommandDebug.perform(sender, (Player) sender);
				}
				
				sender.sendMessage(Messenger.formatString(help));
				return;
				
			} else if (args.length >= 3) {
				OfflinePlayer player = Library.getOfflinePlayer(args[2]);
				
				if (args[1].equals("complete")) {
					CommandComplete.perform(sender, player, Arrays.copyOfRange(args, 3, args.length));
				} else if (args[1].equals("rank")) {
					CommandRank.perform(sender, player, Arrays.copyOfRange(args, 3, args.length));
				} else if (args[1].equals("reset")) {
					CommandReset.perform(sender, player, Arrays.copyOfRange(args, 3, args.length));
				} else if (args[1].equals("resetall")) {
					CommandReset.perform(sender, player, Arrays.copyOfRange(args, 3, args.length), true);
				} else if (args[1].equals("show")) {
					CommandShow.perform(sender, player);
				} else if (args[1].equals("debug")) {
					CommandDebug.perform(sender, player);
				} else {
					sender.sendMessage(Messenger.formatString(help));
					return;
				}
			}
		}
		
		return;
	}

}
