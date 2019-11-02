package me.alchemi.dodgechallenger.listeners.events.island;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.drtshock.playervaults.vaultmanagement.VaultManager;
import com.songoda.skyblock.api.event.island.IslandCreateEvent;
import com.songoda.skyblock.api.event.island.IslandDeleteEvent;
import com.songoda.skyblock.api.island.IslandRole;

import me.alchemi.dodgechallenger.Config;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import me.alchemi.dodgechallenger.meta.IslandMeta;

public class IslandCreateDelete implements Listener {

	@EventHandler
	public static void islandCreate(IslandCreateEvent e) {
		e.getPlayer().setMetadata(IslandMeta.class.getName(), 
				new IslandMeta(
						Dodge.dataManager.newIsland(e.getIsland().getIslandUUID())));
	}
	
	@EventHandler
	public static void islandDelete(IslandDeleteEvent e) {
		
		for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.MEMBER)) {
			if (Config.Options.CLEAR_PLAYER_INVENTORY.asBoolean()) {
				Bukkit.getPlayer(uuid).getInventory().clear();
				Bukkit.getPlayer(uuid).getEnderChest().clear();
				if (Dodge.playerVaults) VaultManager.getInstance().deleteAllVaults(uuid.toString());
			}
		}
		for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.OPERATOR)) {
			if (Config.Options.CLEAR_PLAYER_INVENTORY.asBoolean()) {
				Bukkit.getPlayer(uuid).getInventory().clear();
				Bukkit.getPlayer(uuid).getEnderChest().clear();
				if (Dodge.playerVaults) VaultManager.getInstance().deleteAllVaults(uuid.toString());
			}
		}
		for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.OWNER)) {
			if (Config.Options.CLEAR_PLAYER_INVENTORY.asBoolean()) {
				Bukkit.getPlayer(uuid).getInventory().clear();
				Bukkit.getPlayer(uuid).getEnderChest().clear();
				if (Dodge.playerVaults) VaultManager.getInstance().deleteAllVaults(uuid.toString());
			}
		}
		
		
		Dodge.dataManager.removeIsland(e.getIsland().getIslandUUID());
		DodgeIslandManager.getManager().removeIsland(e.getIsland());
	}
	
}
