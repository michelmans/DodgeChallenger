package com.alchemi.dodgechallenger.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.alchemi.dodgechallenger.managers.IslandManager;
import com.alchemi.dodgechallenger.managers.RankManager;
import com.alchemi.dodgechallenger.objects.Challenge;
import com.alchemi.dodgechallenger.objects.Reward;

import me.goodandevil.skyblock.api.island.Island;

public class ChallengeCompleteEvent extends Event{

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final boolean repeat;
	private final Challenge challenge;
	private final Player player;
	private final Island island;
	private final Reward reward;
	private final IslandManager islandManager;
	private final RankManager rankManager;
	
	public ChallengeCompleteEvent(Challenge challenge, Player player, Island island) {
		this.challenge = challenge;
		this.player = player;
		this.island = island;
		this.islandManager = IslandManager.getByIsland(island);
		this.rankManager = RankManager.getRank(islandManager.getRank());
		this.repeat = islandManager.getChallenges().contains(challenge);
		this.reward = new Reward(challenge, repeat);
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	public Challenge getChallenge() {
		return challenge;
	}
	
	public Island getIsland() {
		return island;
	}
	
	public IslandManager getIslandManager() {
		return islandManager;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Reward getReward() {
		return reward;
	}
	
	public RankManager getRankManager() {
		return rankManager;
	}

	public boolean getRepeat() {
		return repeat;
	}
}
