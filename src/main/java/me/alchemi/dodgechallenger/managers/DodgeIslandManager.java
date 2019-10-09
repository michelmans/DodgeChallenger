package me.alchemi.dodgechallenger.managers;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import me.alchemi.al.objects.meta.PersistentMeta;
import me.alchemi.dodgechallenger.meta.IslandMeta;
import me.alchemi.dodgechallenger.objects.DodgeIsland;
import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandManager;

public class DodgeIslandManager {

	private HashMap<UUID, DodgeIsland> islands = new HashMap<UUID, DodgeIsland>();
	
	private static DodgeIslandManager manager;
	
	private DodgeIslandManager() {}
	
	public HashMap<UUID, DodgeIsland> getIslands() {
		return islands;
	}
	
	public static DodgeIslandManager getManager() {
		if (manager == null) DodgeIslandManager.enable();
		return manager;
	}
	
	public void registerIsland(DodgeIsland island) {
		islands.put(island.getIsland(), island);
	}
	
	public DodgeIsland get(UUID uuid) {
		return islands.containsKey(uuid) ? islands.get(uuid) : null;
	}
	
	public DodgeIsland get(String uuid) {
		return get(UUID.fromString(uuid));
	}
	
	public DodgeIsland get(Island island) {
		return get(island.getIslandUUID());
	}

	public boolean hasIsland(OfflinePlayer player) {
		
		if (IslandManager.hasIsland(player)) {
			if (player.isOnline() && PersistentMeta.hasMeta(player.getPlayer(), IslandMeta.class)) {
				return true;
			} else {
				try {
					
					UUID id = getIslandUUID(player);
					return get(id) != null;
					
				} catch (IllegalAccessError e) {
					
					return false;
					
				}
			}
		}
		
		return false;
	}
	
	public DodgeIsland getByPlayer(OfflinePlayer player) throws IllegalAccessError {
		if (IslandManager.hasIsland(player)) {
			DodgeIsland is;
			
			if (player.isOnline() && PersistentMeta.hasMeta(player.getPlayer(), IslandMeta.class)) {
				is = get(PersistentMeta.getMeta(player.getPlayer(), IslandMeta.class).asString());
				if (is != null) {
					return is;
				}
			}
			
			UUID id = DodgeIslandManager.getIslandUUID(player);
			is = DodgeIslandManager.getManager().get(id);
			
			if (is != null) return is;
			
			is = new DodgeIsland(id);
			
			return is;
		}
		throw new IllegalAccessError("Could not get DodgeIsland for " + player.getName());
	}

	public void purge() {
		islands.clear();
	}
	
	public void removeIsland(Island island) {
		if (islands.containsKey(island.getIslandUUID())) islands.remove(island.getIslandUUID());
	}
	
	public void removeIsland(UUID uuid) {
		if (islands.containsKey(uuid)) islands.remove(uuid);
	}
	
	public static UUID getIslandUUID(OfflinePlayer player) {
		
		if (IslandManager.hasIsland(player)) {
			
			if (player.isOnline()) {
				
				return SkyBlockAPI.getIslandManager().getIsland(player).getIslandUUID();
				
			} else {
				
				Plugin skyblock = Bukkit.getPluginManager().getPlugin("FabledSkyBlock");
				if (skyblock == null) throw new IllegalAccessError("FabledSkyBlock not installed!");
				
				File skyblockFiles = skyblock.getDataFolder();
				
				FileConfiguration playerFile = YamlConfiguration.loadConfiguration(
						new File(new File(skyblockFiles, "player-data"), player.getUniqueId() + ".yml"));
				
				String ownerUUID = playerFile.getString("Island.Owner");
				
				FileConfiguration islandFile = YamlConfiguration.loadConfiguration(
						new File(new File(skyblockFiles, "island-data"), ownerUUID + ".yml"));
				
				return UUID.fromString(islandFile.getString("UUID"));
				
			}
			
		}
		throw new IllegalAccessError(player.getName() + " has no Island.");
		
	}

	public static void enable() {
		manager = new DodgeIslandManager();		
	}
	
}
