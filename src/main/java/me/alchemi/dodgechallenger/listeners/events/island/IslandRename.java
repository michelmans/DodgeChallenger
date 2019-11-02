package me.alchemi.dodgechallenger.listeners.events.island;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.songoda.skyblock.api.event.island.IslandOwnershipTransferEvent;

import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.managers.DodgeIslandManager;

public class IslandRename implements Listener {

	@EventHandler
	public void onIslandRename(IslandOwnershipTransferEvent e) {
		Dodge.dataManager.saveIsland(DodgeIslandManager.getManager().get(e.getIsland().getIslandUUID()));
	}
	
}
