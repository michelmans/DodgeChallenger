package com.alchemi.dodgechallenger.listeners.events;

import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ShapelessRecipe;

import com.alchemi.al.Library;
import com.alchemi.al.configurations.Messenger;
import com.alchemi.dodgechallenger.Config;
import com.alchemi.dodgechallenger.main;
import com.alchemi.dodgechallenger.events.ChallengeCompleteEvent;
import com.alchemi.dodgechallenger.events.DeRankEvent;
import com.alchemi.dodgechallenger.events.RankupEvent;
import com.alchemi.dodgechallenger.meta.PrefixMeta;

import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.event.island.IslandCreateEvent;
import me.goodandevil.skyblock.api.event.island.IslandDeleteEvent;
import me.goodandevil.skyblock.api.event.player.PlayerIslandJoinEvent;
import me.goodandevil.skyblock.api.event.player.PlayerIslandLeaveEvent;
import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandEnvironment;
import me.goodandevil.skyblock.api.island.IslandManager;
import me.goodandevil.skyblock.api.island.IslandRole;
import me.goodandevil.skyblock.api.island.IslandWorld;

public class IslandEvents implements Listener{

	public static void setRankPrefix(Player player, int rank) {
		
		if (main.chatEnabled) {
			String pref = main.chat.getPlayerPrefix(player);
			if (!pref.contains(main.rankTags.get(rank))) main.chat.setPlayerPrefix(player, main.rankTags.get(rank) + Library.getMeta(player, PrefixMeta.class).asString());
		} else {
			String pref = player.getDisplayName();
			if (!pref.contains(Messenger.cc(main.rankTags.get(rank)))) player.setDisplayName(Messenger.cc(main.rankTags.get(rank) + Library.getMeta(player, PrefixMeta.class).asString()));
		}
	}
	
	public static void removeRankPrefix(Player player) {
		
		if (main.chatEnabled) {
			if (Library.getMeta(player, PrefixMeta.class) != null) main.chat.setPlayerPrefix(player, Library.getMeta(player, PrefixMeta.class).asString());
			
		} else {
			if (Library.getMeta(player, PrefixMeta.class) != null) player.setDisplayName(Library.getMeta(player, PrefixMeta.class).asString());
		}
	}
	
	@EventHandler
	public static void playerLogin(PlayerJoinEvent e) {
		for (ShapelessRecipe r : main.instance.recipes) {
			
			e.getPlayer().discoverRecipe(r.getKey());
			
		}
		
		
		if (IslandManager.hasIsland(e.getPlayer())) {
			if (com.alchemi.dodgechallenger.managers.IslandManager.getByIsland(SkyBlockAPI.getIslandManager().getIsland(e.getPlayer())) == null) {
				new com.alchemi.dodgechallenger.managers.IslandManager(SkyBlockAPI.getIslandManager().getIsland(e.getPlayer()));
			}
			
			System.out.println(com.alchemi.dodgechallenger.managers.IslandManager.getByIsland(SkyBlockAPI.getIslandManager().getIsland(e.getPlayer())).checkRank());
			
			if (main.chatEnabled) {
				
				main.chat.setPlayerPrefix(e.getPlayer(), main.chat.getGroupPrefix(e.getPlayer().getLocation().getWorld(), main.chat.getPlayerGroups(e.getPlayer())[0]));
				
			}
				
			
			if (Config.OPTIONS.SHOW_RANK.asBoolean()) {
				int rank = main.dbm.getRank(SkyBlockAPI.getIslandManager().getIsland(e.getPlayer()));
				e.getPlayer().setMetadata(PrefixMeta.NAME, new PrefixMeta(main.instance, main.chatEnabled ? main.chat.getPlayerPrefix(e.getPlayer()) : e.getPlayer().getDisplayName()));
				
				setRankPrefix(e.getPlayer(), rank);
			}
			
		}
	}
	
	@EventHandler
	public static void playerLogout(PlayerQuitEvent e) {
		
		removeRankPrefix(e.getPlayer());
		
		if (IslandManager.hasIsland(e.getPlayer())) {
			com.alchemi.dodgechallenger.managers.IslandManager.getByIsland(SkyBlockAPI.getIslandManager().getIsland(e.getPlayer())).save();
		}
		
		if (Bukkit.getOnlinePlayers().size() <= 1) {
			main.messenger.print("Running data queries: " + main.dbm.querySize());
			main.dbm.runQuery();
		}
	}
	
	@EventHandler
	public static void islandCreate(IslandCreateEvent e) {
		if (Config.OPTIONS.SHOW_RANK.asBoolean()) {
			setRankPrefix(e.getPlayer(), 0);
		}
		
		main.dbm.newIsland(e.getIsland());
	}
	
	@EventHandler
	public static void islandDelete(IslandDeleteEvent e) {
		
		if (Config.OPTIONS.SHOW_RANK.asBoolean()) {
			
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.MEMBER)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				if (Config.OPTIONS.CLEAR_PLAYER_INVENTORY.asBoolean()) {
					Bukkit.getPlayer(uuid).getInventory().clear();
					Bukkit.getPlayer(uuid).getEnderChest().clear();
				}
			}
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.OPERATOR)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				if (Config.OPTIONS.CLEAR_PLAYER_INVENTORY.asBoolean()) {
					Bukkit.getPlayer(uuid).getInventory().clear();
					Bukkit.getPlayer(uuid).getEnderChest().clear();
				}
			}
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.OWNER)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				if (Config.OPTIONS.CLEAR_PLAYER_INVENTORY.asBoolean()) {
					Bukkit.getPlayer(uuid).getInventory().clear();
					Bukkit.getPlayer(uuid).getEnderChest().clear();
				}
			}
		}
		
		main.dbm.removeIsland(e.getIsland());
	}
	
	@EventHandler
	public static void onEntitySpawn(EntitySpawnEvent e) {
		if (e.getEntityType().equals(EntityType.PIG_ZOMBIE)) {
			Random rand = new Random();
			if (rand.nextInt(100) <= 5) {
				e.setCancelled(true);
				e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.WITHER_SKELETON);
			} else if (rand.nextInt(100) <= 10) {
				e.setCancelled(true);
				e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.BLAZE);
			}
			
		} else if (e.getEntityType().equals(EntityType.SQUID)) {
			Random rand = new Random();
			if (rand.nextInt(100) <= 5) {
				e.setCancelled(true);
				e.getLocation().getWorld().spawnEntity(e.getLocation(), EntityType.GUARDIAN);
			}
		}
		
		if (!(e.getEntity() instanceof Mob) || (e.getEntity() instanceof Animals)) return;
		
		if (e.getLocation().getBlock().getLightLevel() > 7) {
			e.setCancelled(true);
		}
		
		Island island = SkyBlockAPI.getIslandManager().getIslandAtLocation(e.getLocation());
		if (island != null && e.getLocation().getWorld().equals(island.getLocation(IslandWorld.OVERWORLD, IslandEnvironment.ISLAND).getWorld()) 
				&& e.getLocation().distance(island.getLocation(IslandWorld.OVERWORLD, IslandEnvironment.ISLAND)) <= 24) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public static void islandJoin(PlayerIslandJoinEvent e) {
		
		if (Config.OPTIONS.SHOW_RANK.asBoolean()) {
			setRankPrefix(e.getPlayer(), main.dbm.getRank(e.getIsland()));
		}
	}

	@EventHandler
	public static void onChallengeComplete(ChallengeCompleteEvent e) {
		e.getReward().give(e.getPlayer());
		
		com.alchemi.dodgechallenger.managers.IslandManager im = e.getIslandManager();
		im.addChallenge(e.getChallenge());
		
		main.dbm.completeChallenge(e.getIsland(), e.getChallenge());
		
		if (!e.getRepeat()) {
			if (Config.OPTIONS.BROADCAST_COMPLETION.asBoolean()) 
				main.messenger.broadcast((Config.OPTIONS.BROADCAST_FORMAT.asString() + Config.MESSAGES.CHALLENGE_BROADCAST_COMPLETED.value())
					.replace("$player$", e.getPlayer().getDisplayName())
					.replace("$challenge$", e.getChallenge().getDisplayName())
					.replace("$rank$", e.getRankManager().getDisplayName())
					.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString()));
		}
		
		im.checkRank();
	}
	
	@EventHandler
	public static void onRankup(RankupEvent e) {
		if (Config.OPTIONS.BROADCAST_RANKUP.asBoolean()) 
			main.messenger.broadcast((Config.OPTIONS.BROADCAST_FORMAT.asString() + Config.MESSAGES.RANK_BROADCAST_RANKUP.value())
				.replace("$player$", Bukkit.getPlayer(e.getIsland().getOwnerUUID()).getDisplayName())
				.replace("$rank$", e.getRankManager().getDisplayName())
				.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString()));
		
		main.dbm.setRank(e.getIsland(), e.getRankManager().rank());
		e.getIslandManager().setRank(e.getRankManager().rank());
		SkyBlockAPI.getImplementation().getLeaderboardManager().resetLeaderboard();
		
		if (Config.OPTIONS.SHOW_RANK.asBoolean()) {
			
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.MEMBER)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				setRankPrefix(Bukkit.getPlayer(uuid), e.getRankManager().rank());
			}
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.OPERATOR)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				setRankPrefix(Bukkit.getPlayer(uuid), e.getRankManager().rank());
			}
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.OWNER)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				setRankPrefix(Bukkit.getPlayer(uuid), e.getRankManager().rank());
			}
			
			removeRankPrefix(Bukkit.getPlayer(e.getIsland().getOwnerUUID()));
			setRankPrefix(Bukkit.getPlayer(e.getIsland().getOwnerUUID()), e.getRankManager().rank());
		}
	}
	
	@EventHandler
	public static void onDeRank(DeRankEvent e) {
		main.dbm.setRank(e.getIsland(), e.getRankManager().rank());
		
		if (Config.OPTIONS.SHOW_RANK.asBoolean()) {
			
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.MEMBER)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				setRankPrefix(Bukkit.getPlayer(uuid), e.getRankManager().rank());
			}
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.OPERATOR)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				setRankPrefix(Bukkit.getPlayer(uuid), e.getRankManager().rank());
			}
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.OWNER)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				setRankPrefix(Bukkit.getPlayer(uuid), e.getRankManager().rank());
			}
			
			removeRankPrefix(Bukkit.getPlayer(e.getIsland().getOwnerUUID()));
			setRankPrefix(Bukkit.getPlayer(e.getIsland().getOwnerUUID()), e.getRankManager().rank());
		}
	}
	
	public static void islandLeave(PlayerIslandLeaveEvent e) {
		if (Config.OPTIONS.SHOW_RANK.asBoolean()) {
			removeRankPrefix(e.getPlayer());
		}
		
	}
		
}
