package me.alchemi.dodgechallenger.events;

import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.DodgeIsland;
import me.alchemi.dodgechallenger.objects.Rank;
import me.alchemi.dodgechallenger.objects.Reward;
import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.island.Island;

public class ChallengeCompleteEvent extends Event{

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final boolean repeat;
	private final Challenge challenge;
	private final OfflinePlayer player;
	private final UUID fabledIsland;
	private final Reward reward;
	private final DodgeIsland island;
	private final Rank rank;
	private final ItemStack[] toTake;
	
	public ChallengeCompleteEvent(Challenge challenge, OfflinePlayer player, UUID island, ItemStack[] toTake) {
		this.challenge = challenge;
		this.player = player;
		this.fabledIsland = island;
		this.island = DodgeIslandManager.getManager().get(island);
		System.out.println(this.island);
		System.out.println(island);
		this.rank = this.island.getRank();
		this.repeat = this.island.getChallenges().contains(challenge);
		this.reward = new Reward(challenge, repeat);
		this.toTake = toTake;
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
	
	public UUID getFabledIslandUUID() {
		return fabledIsland;
	}
	
	public Island getFabledIsland() {
		return SkyBlockAPI.getIslandManager().getIslandByUUID(fabledIsland);
	}
	
	public DodgeIsland getIsland() {
		return island;
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
	
	public Rank getRank() {
		return rank;
	}

	public boolean getRepeat() {
		return repeat;
	}

	public ItemStack[] getToTake() {
		return toTake;
	}
}
