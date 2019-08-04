package me.alchemi.dodgechallenger.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.alchemi.dodgechallenger.managers.IslandManager;
import me.alchemi.dodgechallenger.managers.RankManager;
import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.Reward;
import me.goodandevil.skyblock.api.island.Island;

public class ChallengeCompleteEvent extends Event{

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final boolean repeat;
	private final Challenge challenge;
	private final OfflinePlayer player;
	private final Island island;
	private final Reward reward;
	private final IslandManager islandManager;
	private final RankManager rankManager;
	
	public ChallengeCompleteEvent(Challenge challenge, OfflinePlayer player, Island island) {
		this.challenge = challenge;
		this.player = player;
		this.island = island;
		this.islandManager = player.isOnline() ? IslandManager.getByPlayer(player.getPlayer()) : IslandManager.getByIsland(island);
		this.rankManager = islandManager.getRankManager();
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
	
	public OfflinePlayer getPlayer() {
		return player;
	}
	
	public boolean isPlayerOnline() {
		return player.isOnline();
	}
	
	public Player getOnlinePlayer() {
		return isPlayerOnline() ? player.getPlayer() : null;
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
