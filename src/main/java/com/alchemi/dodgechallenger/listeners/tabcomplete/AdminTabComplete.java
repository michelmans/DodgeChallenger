package com.alchemi.dodgechallenger.listeners.tabcomplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.alchemi.dodgechallenger.managers.RankManager;
import com.alchemi.dodgechallenger.objects.Challenge;

public class AdminTabComplete implements TabCompleter {

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> tabSuggest = new ArrayList<>();
		List<String> list = new ArrayList<>();
		
		if (!(sender instanceof Player))
			return tabSuggest;

		if (!sender.hasPermission("dodgec.admin") && !sender.isOp())
			return tabSuggest;
		
		if (args.length == 1) {
			
			if (sender.hasPermission("dodgec.reload") || sender.isOp()) list.add("reload");
			
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
				for (int i = 0; i < RankManager.getRanks().size(); i++) list.add(String.valueOf(i));
				
			} else if (args[1].equals("reset")) {
				list.addAll(Challenge.getChallenges());
				
			}
		}

		for (int i = list.size() - 1; i >= 0; i--)
			if(list.get(i).startsWith(args[args.length - 1]))
				tabSuggest.add(list.get(i));

		Collections.sort(tabSuggest);
		return tabSuggest;
	}

}
