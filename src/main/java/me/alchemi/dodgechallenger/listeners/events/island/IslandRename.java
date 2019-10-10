package me.alchemi.dodgechallenger.listeners.events.island;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import com.songoda.skyblock.api.event.island.IslandOwnershipTransferEvent;

public class IslandRename implements Listener {

	@EventHandler
	public void onIslandRename(IslandOwnershipTransferEvent e) {
		Dodge.dataManager.saveIsland(DodgeIslandManager.getManager().get(e.getIsland().getIslandUUID()));
	}
	
}
