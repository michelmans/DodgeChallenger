package me.alchemi.dodgechallenger.listeners.tabcomplete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.objects.base.TabCompleteBase;
import me.alchemi.dodgechallenger.managers.RankManager;
import me.alchemi.dodgechallenger.objects.Challenge;

public class AdminTabComplete extends TabCompleteBase {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

		List<Object> list = new ArrayList<Object>();

		if (!(sender instanceof Player && sender.hasPermission(command.getPermission())))
			return Arrays.asList("");
		
		if (args.length == 1) {
			
			if (sender.hasPermission("dodgec.reload")) list.add("reload");
			if (sender.hasPermission("dodgec.default")) list.add("defaults");
			
			for (Player p : Bukkit.getOnlinePlayers()) {
				list.add(p.getName());
			}
				
		} else if (args.length == 2) {
			
			list.add("complete");
			list.add("rank");
			list.add("reset");
			list.add("resetall");
			list.add("show");
			
		} else if (args.length > 2) {
			if (args[1].equals("complete")) {
				list.addAll(Challenge.getChallenges());
				
			} else if (args[1].equals("rank")) {
				for (int i = 0; i < RankManager.getManager().ranks(); i++) list.add(String.valueOf(i));
				
			} else if (args[1].equals("reset")) {
				list.addAll(Challenge.getChallenges());
				
			}
		}

		return returnSortSuggest(list, args);
	}

}
