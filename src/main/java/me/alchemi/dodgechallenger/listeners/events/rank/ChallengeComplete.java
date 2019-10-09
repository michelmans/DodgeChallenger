package me.alchemi.dodgechallenger.listeners.events.rank;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.alchemi.dodgechallenger.Config;
import me.alchemi.dodgechallenger.Config.Messages;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.events.ChallengeCompleteEvent;
import me.alchemi.dodgechallenger.objects.DodgeIsland;
import me.alchemi.dodgechallenger.objects.placeholder.Stringer;

public class ChallengeComplete implements Listener {

	@EventHandler
	public void onChallengeComplete(ChallengeCompleteEvent e) {
		e.getReward().give(e.getPlayer());
		
		DodgeIsland island = e.getIsland();
		island.addChallenge(e.getChallenge());
		
		System.out.println(e.getFabledIslandUUID());
		System.out.println(e.getFabledIsland().getIslandUUID());
		System.out.println(e.getIsland().getIsland());
		
		Dodge.dataManager.setChallenges(island.getIsland(), island.getChallenges());
		
		if (e.getPlayer().isOnline()) {
			if (e.getToTake() != null) e.getOnlinePlayer().getInventory().removeItem(e.getToTake());
			Dodge.getInstance().getMessenger().sendMessage(new Stringer(Messages.CHALLENGE_MESSAGE)
					.player(e.getOnlinePlayer())
					.challenge(e.getChallenge()), e.getOnlinePlayer());
		}
		
		if (!e.getRepeat() && e.getPlayer().isOnline()) {
			if (Config.Options.BROADCAST_COMPLETION.asBoolean()) 
				
				Dodge.getInstance().getMessenger().broadcast(new Stringer(Messages.CHALLENGE_BROADCASTCOMPLETED)
						.player(e.getPlayer().getPlayer())
						.challenge(e.getChallenge())
						.rank(e.getRank())
						.parse(e.getPlayer().getPlayer()));		
		}
		
		
		island.checkRank();
	}
	
	
	
}
