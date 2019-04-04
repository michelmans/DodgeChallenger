package com.alchemi.dodgechallenger.listeners;

import org.bukkit.entity.Player;

import com.alchemi.al.Library;
import com.alchemi.dodgechallenger.main;
import com.alchemi.dodgechallenger.listeners.events.IslandEvents;
import com.alchemi.dodgechallenger.managers.IslandManager;
import com.alchemi.dodgechallenger.meta.PrefixMeta;

public class PrefixListener implements Runnable{

	public final Player player;
	
	public PrefixListener(Player player) {
		
		this.player = player;
		
	}
	
	@Override
	public void run() {
		
		String prefixDef = Library.getMeta(player, PrefixMeta.class).asString();
		String prefixCur = main.chatEnabled ? main.chat.getPlayerPrefix(player) : player.getDisplayName();
		
		if (!(main.rankTags.get(IslandManager.getByPlayer(player).getRank()) + prefixDef).equals(prefixCur)) {
			
			player.setMetadata(PrefixMeta.class.getSimpleName(), new PrefixMeta(prefixCur));
			IslandEvents.setRankPrefix(player, IslandManager.getByPlayer(player).getRank());
			
		}
		
	}
}
