package com.alchemi.dodgechallenger.events;

import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.al.objects.meta.PersistentMeta;
import com.alchemi.dodgechallenger.main;
import com.alchemi.dodgechallenger.meta.PrefixMeta;

import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;

public class PrefixStuff implements Listener {

	public static void setRankPrefix(Player player, String rank) {
		
		if (player == null) return;
		
		if (main.luckPermsEnabled) {
			
			User u = main.getInstance().loadLuckyUser(player);
			
			int maxi = 0;
			String pref = "";
			
			for (@Nonnull Entry<Integer, String> node : u.getAllNodes().stream()
					.filter(Node -> Node.isPrefix() && Node.appliesGlobally())
					.map(Node::getPrefix)
					.collect(Collectors.toSet())) {
				
				if (node.getKey() >= maxi) {
					pref = node.getValue();
					maxi = node.getKey();
				}
				
			}
			
			maxi = 0;
			String pref2 = "";
			for (@Nonnull Entry<Integer, String> node : u.getOwnNodes().stream()
					.filter(Node -> Node.isPrefix() && Node.isServerSpecific() && Node.getServer().toString().equals(Bukkit.getServer().getName()))
					.map(Node::getPrefix)
					.collect(Collectors.toSet())) {
	
				if (node.getKey() >= maxi) {
					pref2 = node.getValue();
					maxi = node.getKey();
				}
			}
			
			if (pref2.equals(rank + pref) || pref.contains(rank)) return;
			
			Node node = main.lucky.getNodeFactory().makePrefixNode(846, rank + pref).setServer(Bukkit.getServer().getName()).build();
			u.clearMatching(Node -> Node.isPrefix() && Node.getPrefix().getKey() == 846);
			u.setPermission(node);
			main.lucky.getUserManager().saveUser(u);
			
		} else {
			if (main.chatEnabled) {
				String pref = main.chat.getPlayerPrefix(player);
				if (!pref.contains(rank)) main.chat.setPlayerPrefix(player, rank + PersistentMeta.getMeta(player, PrefixMeta.class).asString());
			} else {
				String pref = player.getDisplayName();
				if (!pref.contains(Messenger.cc(rank))) player.setDisplayName(Messenger.cc(rank + PersistentMeta.getMeta(player, PrefixMeta.class).asString()));
			}
		}
	}

	public static void removeRankPrefix(Player player) {
		
		if (player == null) return; 
		
		if (main.luckPermsEnabled) {
			
			return;
			
		} else {
			if (main.chatEnabled) {
				if (PersistentMeta.getMeta(player, PrefixMeta.class) != null) main.chat.setPlayerPrefix(player, PersistentMeta.getMeta(player, PrefixMeta.class).asString());
				
			} else {
				
				if (PersistentMeta.getMeta(player, PrefixMeta.class) != null) player.setDisplayName(PersistentMeta.getMeta(player, PrefixMeta.class).asString());
			}
		}
	}

}
