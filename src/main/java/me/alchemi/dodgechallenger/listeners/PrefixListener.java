package me.alchemi.dodgechallenger.listeners;

import org.bukkit.entity.Player;

import me.alchemi.al.objects.meta.PersistentMeta;
import me.alchemi.dodgechallenger.main;
import me.alchemi.dodgechallenger.events.PrefixStuff;
import me.alchemi.dodgechallenger.managers.IslandManager;
import me.alchemi.dodgechallenger.meta.PrefixMeta;

public class PrefixListener implements Runnable{

	public final Player player;
	
	public PrefixListener(Player player) {
		
		this.player = player;
		
	}
	
	@Override
	public void run() {
		
		String prefixDef = PersistentMeta.getMeta(player, PrefixMeta.class).asString();
		String prefixCur = main.chatEnabled ? main.chat.getPlayerPrefix(player) : player.getDisplayName();
		
		if (!(IslandManager.getByPlayer(player).getRankManager().getPrefix() + prefixDef).equals(prefixCur)) {
			
			player.setMetadata(PrefixMeta.class.getSimpleName(), new PrefixMeta(prefixCur));
			PrefixStuff.setRankPrefix(player, IslandManager.getByPlayer(player).getRankManager().getPrefix());
			
		}
		
	}
}
