package me.alchemi.dodgechallenger.events;

import java.util.UUID;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import me.alchemi.dodgechallenger.objects.DodgeIsland;
import me.alchemi.dodgechallenger.objects.Rank;
import com.songoda.skyblock.api.SkyBlockAPI;
import com.songoda.skyblock.api.island.Island;

public class DeRankEvent extends Event{

	private static final HandlerList HANDLERS = new HandlerList();
	
	private final UUID fabledIsland;
	private final DodgeIsland island;
	private final Rank rank;
	private final Rank oldRank;
	
	public DeRankEvent(UUID island, Rank oldRank) {
		this.fabledIsland = island;
		this.island = DodgeIslandManager.getManager().get(island);
		this.rank = this.island.getRank();
		this.oldRank = oldRank;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
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
	
	public Rank getRank() {
		return rank;
	}

	/**
	 * @return the oldRank
	 */
	public Rank getOldRank() {
		return oldRank;
	}
	
}
