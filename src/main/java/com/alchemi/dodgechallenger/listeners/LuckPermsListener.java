package com.alchemi.dodgechallenger.listeners;

import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.alchemi.dodgechallenger.main;
import com.alchemi.dodgechallenger.events.PrefixStuff;
import com.alchemi.dodgechallenger.managers.IslandManager;

import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.event.EventBus;
import me.lucko.luckperms.api.event.EventHandler;
import me.lucko.luckperms.api.event.node.NodeMutateEvent;

public class LuckPermsListener {

	private EventHandler<NodeMutateEvent> eHandler;
	
	public LuckPermsListener() {
		EventBus eBus = main.lucky.getEventBus();
		eHandler = eBus.subscribe(NodeMutateEvent.class, this::onNodeChange);
	}
	
	private void onNodeChange(NodeMutateEvent e) {
		
		if (e.getDataAfter().stream()
				.filter(Node::isPrefix)
				.collect(Collectors.toSet()).size() <= 0) return;
	
		try {
			UUID uuid = UUID.fromString(e.getTarget().getObjectName());
			Player player = Bukkit.getPlayer(uuid);
			
			if (player == null) return;
			
			IslandManager im = IslandManager.getByPlayer(player);
			if (im != null) {
				PrefixStuff.setRankPrefix(player, im.getRankManager().getPrefix());
			}
			
		} catch (IllegalArgumentException ex) { ex.printStackTrace(); }
		
	}
	
	public void unregister() {
		eHandler.unregister();
	}
	
}
