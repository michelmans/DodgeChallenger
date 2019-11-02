package me.alchemi.dodgechallenger.listeners.events.rank;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.songoda.skyblock.api.SkyBlockAPI;

import me.alchemi.dodgechallenger.Config;
import me.alchemi.dodgechallenger.Config.Messages;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.events.DeRankEvent;
import me.alchemi.dodgechallenger.events.RankupEvent;
import me.alchemi.dodgechallenger.objects.placeholder.Stringer;

public class Ranks implements Listener {

	@EventHandler
	public static void onRankup(RankupEvent e) {
		if (Config.Options.BROADCAST_RANKUP.asBoolean()) 
			
			Dodge.getInstance().getMessenger().broadcast(new Stringer(Messages.RANK_BROADCASTRANKUP)
					.rank(e.getRank())
					.owner(Bukkit.getOfflinePlayer(e.getFabledIsland().getOwnerUUID()))
					.parse(Bukkit.getOfflinePlayer(e.getFabledIsland().getOwnerUUID())));
		
		Dodge.dataManager.setRank(e.getFabledIslandUUID(), e.getRank().getId());
		e.getIsland().setRank(e.getRank());
		SkyBlockAPI.getImplementation().getLeaderboardManager().resetLeaderboard();
	}
	
	@EventHandler
	public static void onDeRank(DeRankEvent e) {
		Dodge.dataManager.setRank(e.getFabledIslandUUID(), e.getRank().getId());
	}
	
}
