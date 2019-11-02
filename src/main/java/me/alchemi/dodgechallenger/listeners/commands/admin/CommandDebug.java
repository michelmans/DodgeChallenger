package me.alchemi.dodgechallenger.listeners.commands.admin;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.songoda.skyblock.api.SkyBlockAPI;
import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.api.island.IslandManager;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.dodgechallenger.Config.Messages;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import me.alchemi.dodgechallenger.meta.IslandMeta;
import me.alchemi.dodgechallenger.objects.DodgeIsland;

public class CommandDebug {

	public static boolean perform(CommandSender sender, OfflinePlayer player) {
		
		if (player == null) {
			
			sender.sendMessage(Messenger.formatString(Messages.COMMANDS_WRONGFORMAT.value() + CommandAdmin.debugUsage));
			return true;
			
		}
		
		if (IslandManager.hasIsland(player)) {
		
			DodgeIsland dIsland;
			
			try {
				dIsland = DodgeIslandManager.getManager().getByPlayer(player);
				Dodge.dataManager.saveIsland(dIsland);
			} catch(IllegalAccessError e) {
				Island island = SkyBlockAPI.getIslandManager().getIsland(player);
				dIsland = Dodge.dataManager.newIsland(island.getIslandUUID());
			}
			
			if (player.isOnline()) player.getPlayer().setMetadata(IslandMeta.class.getName(), new IslandMeta(dIsland));
			return true;
			
		}
		
		return true;
	
	}

}
