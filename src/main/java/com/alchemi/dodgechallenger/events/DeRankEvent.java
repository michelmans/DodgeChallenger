package com.alchemi.dodgechallenger.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.alchemi.dodgechallenger.managers.IslandManager;
import com.alchemi.dodgechallenger.managers.RankManager;

import me.goodandevil.skyblock.api.island.Island;

public class DeRankEvent extends Event{

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final Island island;
	private final IslandManager islandManager;
	private final RankManager rankManager;
	private final RankManager oldRank;
	
	public DeRankEvent(Island island, RankManager oldRank) {
		this.island = island;
		this.islandManager = IslandManager.getByIsland(island);
		this.rankManager = RankManager.getRank(islandManager.getRank());
		this.oldRank = oldRank;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public Island getIsland() {
		return island;
	}
	
	public IslandManager getIslandManager() {
		return islandManager;
	}
	
	public RankManager getRankManager() {
		return rankManager;
	}

	/**
	 * @return the oldRank
	 */
	public RankManager getOldRank() {
		return oldRank;
	}
	
}
