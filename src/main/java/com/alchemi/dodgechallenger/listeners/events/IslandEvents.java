package com.alchemi.dodgechallenger.listeners.events;

import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

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
import com.alchemi.dodgechallenger.listeners.PrefixListener;
import com.alchemi.dodgechallenger.managers.IslandManager;
import com.alchemi.dodgechallenger.managers.RankManager;
import com.alchemi.dodgechallenger.meta.IslandMeta;
import com.alchemi.dodgechallenger.meta.PrefixMeta;
import com.alchemi.dodgechallenger.meta.TaskIntMeta;

import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.event.island.IslandCreateEvent;
import me.goodandevil.skyblock.api.event.island.IslandDeleteEvent;
import me.goodandevil.skyblock.api.event.player.PlayerIslandJoinEvent;
import me.goodandevil.skyblock.api.event.player.PlayerIslandLeaveEvent;
import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandEnvironment;
import me.goodandevil.skyblock.api.island.IslandRole;
import me.goodandevil.skyblock.api.island.IslandWorld;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;

public class IslandEvents implements Listener{

	public static void setRankPrefix(Player player, String rank) {
		
		if (player == null) return;
		
		if (main.luckPermsEnabled) {
			
			User u = main.instance.loadLuckyUser(player);
			
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
					.filter(Node -> Node.isPrefix() && Node.isServerSpecific() && Node.getServer().toString().equals("skyblock"))
					.map(Node::getPrefix)
					.collect(Collectors.toSet())) {

				if (node.getKey() >= maxi) {
					pref2 = node.getValue();
					maxi = node.getKey();
				}
			}
			
			if (pref2.equals(rank + pref) || pref.contains(rank)) return;
			
			Node node = main.lucky.getNodeFactory().makePrefixNode(846, rank + pref).setServer("skyblock").build();
			u.clearMatching(Node -> Node.isPrefix() && Node.getPrefix().getKey() == 846);
			u.setPermission(node);
			main.lucky.getUserManager().saveUser(u);
			
		} else {
			if (main.chatEnabled) {
				String pref = main.chat.getPlayerPrefix(player);
				if (!pref.contains(rank)) main.chat.setPlayerPrefix(player, rank + Library.getMeta(player, PrefixMeta.class).asString());
			} else {
				String pref = player.getDisplayName();
				if (!pref.contains(Messenger.cc(rank))) player.setDisplayName(Messenger.cc(rank + Library.getMeta(player, PrefixMeta.class).asString()));
			}
		}
	}
	
	public static void removeRankPrefix(Player player) {
		
		if (player == null) return; 
		
		if (main.luckPermsEnabled) {
			
			return;
			
		} else {
			if (main.chatEnabled) {
				if (Library. getMeta(player, PrefixMeta.class) != null) main.chat.setPlayerPrefix(player, Library.getMeta(player, PrefixMeta.class).asString());
				
			} else {
				
				if (Library.getMeta(player, PrefixMeta.class) != null) player.setDisplayName(Library.getMeta(player, PrefixMeta.class).asString());
			}
		}
	}
	
	@EventHandler
	public static void playerLogin(PlayerJoinEvent e) {
		for (ShapelessRecipe r : main.instance.recipes) {
			
			e.getPlayer().discoverRecipe(r.getKey());
			
		}
		
		
		if (me.goodandevil.skyblock.api.island.IslandManager.hasIsland(e.getPlayer())) {

			if (!main.luckPermsEnabled) e.getPlayer().setMetadata(TaskIntMeta.class.getSimpleName(), new TaskIntMeta(Bukkit.getScheduler().scheduleSyncRepeatingTask(main.instance, new PrefixListener(e.getPlayer()), 0, 200)));
			if (IslandManager.getByPlayer(e.getPlayer()) == null) {
				e.getPlayer().setMetadata(IslandMeta.class.getSimpleName(), new IslandMeta(new IslandManager(SkyBlockAPI.getIslandManager().getIsland(e.getPlayer()))));
			}
			
			IslandManager.getByPlayer(e.getPlayer()).checkRank();
			
			if (Config.OPTIONS.SHOW_RANK.asBoolean()) {
				int rank = main.dbm.getRank(SkyBlockAPI.getIslandManager().getIsland(e.getPlayer()));
				
				e.getPlayer().setMetadata(PrefixMeta.class.getSimpleName(), new PrefixMeta(main.chatEnabled ? main.chat.getPlayerPrefix(e.getPlayer()) : e.getPlayer().getDisplayName()));
				
				setRankPrefix(e.getPlayer(), RankManager.getRank(rank).getPrefix());
			}
			
		}
	}
	
	@EventHandler
	public static void playerLogout(PlayerQuitEvent e) {
		
		if (Library.hasMeta(e.getPlayer(), TaskIntMeta.class)) Bukkit.getScheduler().cancelTask(Library.getMeta(e.getPlayer(), TaskIntMeta.class).asInt());
		
		removeRankPrefix(e.getPlayer());
		
		if (me.goodandevil.skyblock.api.island.IslandManager.hasIsland(e.getPlayer())) {
			IslandManager.getByPlayer(e.getPlayer()).save();
		}
		
		if (Bukkit.getOnlinePlayers().size() <= 1) {
			main.messenger.print("Running data queries: " + main.dbm.querySize());
			main.dbm.runQuery();
		}
	}
	
	@EventHandler
	public static void islandCreate(IslandCreateEvent e) {
		if (Config.OPTIONS.SHOW_RANK.asBoolean()) {
			setRankPrefix(e.getPlayer(), RankManager.getFirst().getPrefix());
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
		
		Island island = SkyBlockAPI.getIslandManager().getIslandAtLocation(e.getLocation());
		if (island != null && e.getLocation().getWorld().equals(island.getLocation(IslandWorld.OVERWORLD, IslandEnvironment.ISLAND).getWorld()) 
				&& e.getLocation().distance(island.getLocation(IslandWorld.OVERWORLD, IslandEnvironment.ISLAND)) <= 24) {
			e.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public static void islandJoin(PlayerIslandJoinEvent e) {
		
		if (Config.OPTIONS.SHOW_RANK.asBoolean()) {
			setRankPrefix(e.getPlayer(), RankManager.getRank(main.dbm.getRank(e.getIsland())).getPrefix());
		}
		
	}

	@EventHandler
	public static void onChallengeComplete(ChallengeCompleteEvent e) {
		e.getReward().give(e.getPlayer());
		
		IslandManager im = e.getIslandManager();
		im.addChallenge(e.getChallenge());
		
		main.dbm.completeChallenge(e.getIsland(), e.getChallenge());
		
		if (!e.getRepeat() && e.getPlayer().isOnline()) {
			if (Config.OPTIONS.BROADCAST_COMPLETION.asBoolean()) 
				main.messenger.broadcast((Config.OPTIONS.BROADCAST_FORMAT.asString() + Config.MESSAGES.CHALLENGE_BROADCAST_COMPLETED.value())
					.replace("$player$", e.getPlayer().getPlayer().getDisplayName())
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
				setRankPrefix(Bukkit.getPlayer(uuid), e.getRankManager().getPrefix());
			}
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.OPERATOR)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				setRankPrefix(Bukkit.getPlayer(uuid), e.getRankManager().getPrefix());
			}
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.OWNER)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				setRankPrefix(Bukkit.getPlayer(uuid), e.getRankManager().getPrefix());
			}
			
			removeRankPrefix(Bukkit.getPlayer(e.getIsland().getOwnerUUID()));
			setRankPrefix(Bukkit.getPlayer(e.getIsland().getOwnerUUID()), e.getRankManager().getPrefix());
		}
	}
	
	@EventHandler
	public static void onDeRank(DeRankEvent e) {
		main.dbm.setRank(e.getIsland(), e.getRankManager().rank());
		
		if (Config.OPTIONS.SHOW_RANK.asBoolean()) {
			
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.MEMBER)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				setRankPrefix(Bukkit.getPlayer(uuid), e.getRankManager().getPrefix());
			}
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.OPERATOR)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				setRankPrefix(Bukkit.getPlayer(uuid), e.getRankManager().getPrefix());
			}
			for (UUID uuid : e.getIsland().getPlayersWithRole(IslandRole.OWNER)) {
				removeRankPrefix(Bukkit.getPlayer(uuid));
				setRankPrefix(Bukkit.getPlayer(uuid), e.getRankManager().getPrefix());
			}
			
			removeRankPrefix(Bukkit.getPlayer(e.getIsland().getOwnerUUID()));
			setRankPrefix(Bukkit.getPlayer(e.getIsland().getOwnerUUID()), e.getRankManager().getPrefix());
		}
	}
	
	public static void islandLeave(PlayerIslandLeaveEvent e) {
		if (Config.OPTIONS.SHOW_RANK.asBoolean()) {
			removeRankPrefix(e.getPlayer());
		}
		
	}
		
}
