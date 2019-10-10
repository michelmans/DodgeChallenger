package me.alchemi.dodgechallenger.listeners.events;

import java.util.Random;

import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import me.alchemi.dodgechallenger.Config;
import com.songoda.skyblock.api.SkyBlockAPI;
import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.api.island.IslandEnvironment;
import com.songoda.skyblock.api.island.IslandWorld;

public class CreatureSpawn implements Listener {

	@EventHandler
	public static void onEntitySpawn(CreatureSpawnEvent e) {
		
		if (e.getEntityType().equals(EntityType.PIG_ZOMBIE)) {
			Random rand = new Random();
			if (rand.nextInt(100) <= Config.Options.SPAWN_WITHERSKELETON.asInt()) {
				e.setCancelled(true);
				e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.WITHER_SKELETON);
			} else if (rand.nextInt(100) <= Config.Options.SPAWN_BLAZE.asInt()) {
				e.setCancelled(true);
				e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.BLAZE);
			}
			
		} else if (e.getEntityType().equals(EntityType.SQUID)) {
			Random rand = new Random();
			if (rand.nextInt(100) <= Config.Options.SPAWN_GUARDIAN.asInt()) {
				e.setCancelled(true);
				e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.GUARDIAN);
			}
		}
		
		if (!(e.getEntity() instanceof Mob) || (e.getEntity() instanceof Animals)) return;
		
		Island island = SkyBlockAPI.getIslandManager().getIslandAtLocation(e.getLocation());
		if (island != null 
				&& e.getLocation().getWorld().equals(island.getLocation(IslandWorld.OVERWORLD, IslandEnvironment.ISLAND).getWorld()) 
				&& e.getLocation().distance(island.getLocation(IslandWorld.OVERWORLD, IslandEnvironment.ISLAND)) <= 24
				&& e.getSpawnReason() == SpawnReason.NATURAL) {
			e.setCancelled(true);
		}
		
	}
	
}
