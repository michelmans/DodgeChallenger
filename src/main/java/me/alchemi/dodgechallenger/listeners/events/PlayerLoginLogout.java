package me.alchemi.dodgechallenger.listeners.events;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ShapelessRecipe;

import me.alchemi.al.objects.meta.PersistentMeta;
import me.alchemi.dodgechallenger.Config.Data;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.managers.ConfigurationManager;
import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import me.alchemi.dodgechallenger.meta.IslandMeta;
import me.alchemi.dodgechallenger.meta.TaskIntMeta;
import me.alchemi.dodgechallenger.objects.DodgeIsland;
import me.alchemi.dodgechallenger.objects.StorageSystem;
import me.goodandevil.skyblock.api.island.IslandManager;

public class PlayerLoginLogout implements Listener {
	
	@EventHandler
	public static void playerLogin(PlayerJoinEvent e) {
		for (ShapelessRecipe r : Dodge.getInstance().recipes) {
			
			e.getPlayer().discoverRecipe(r.getKey());
			
		}
		
		if (IslandManager.hasIsland(e.getPlayer())) {

			DodgeIsland island = DodgeIslandManager.getManager().getByPlayer(e.getPlayer());
			
			if (island == null) {
				
				UUID id = DodgeIslandManager.getIslandUUID(e.getPlayer());
				
				island = DodgeIslandManager.getManager().get(id);
				
				if (island == null) island = new DodgeIsland(id);
				
				e.getPlayer().setMetadata(IslandMeta.class.getName(), new IslandMeta(island));
				
			}
			
			e.getPlayer().setMetadata(IslandMeta.class.getName(), new IslandMeta(island));
			
		}
	}
	
	@EventHandler
	public static void playerLogout(PlayerQuitEvent e) {
		
		if (PersistentMeta.hasMeta(e.getPlayer(), TaskIntMeta.class)) 
			Bukkit.getScheduler().cancelTask(PersistentMeta.getMeta(e.getPlayer(), TaskIntMeta.class).asInt());
		
		if (DodgeIslandManager.getManager().hasIsland(e.getPlayer())) {
			DodgeIslandManager.getManager().getByPlayer(e.getPlayer());
		}
		
		if (Bukkit.getOnlinePlayers().size() <= 1) {
			
			if (StorageSystem.valueOf(Data.STORAGE.asString()) == StorageSystem.YML) {
			
				Dodge.getInstance().getMessenger().print("Running data queries: " + ((ConfigurationManager)Dodge.dataManager).querySize());
				((ConfigurationManager)Dodge.dataManager).runQuery();
				
			}
		}
	}

}
