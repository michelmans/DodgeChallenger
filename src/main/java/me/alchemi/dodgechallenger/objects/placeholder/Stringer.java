package me.alchemi.dodgechallenger.objects.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.alchemi.al.Library;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.objects.base.ConfigBase.IMessage;
import me.alchemi.al.objects.placeholder.IStringer;
import me.alchemi.dodgechallenger.Config.Messages;
import me.alchemi.dodgechallenger.Config.Options;
import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.Rank;

public class Stringer implements IStringer {

	private String string;
	
	public Stringer(String initialString) {
		string = initialString;
	}
	
	public Stringer(Messages message) {
		string = message.value();
	}
	
	@Override
	public Stringer command(String command) {
		string = string.replace("%command%", command);
		return this;
	}

	@Override
	public Stringer player(Player player) {
		string = string.replace("%player%", player.getName());
		return this;
	}
	
	@Override
	public Stringer player(String player) {
		string = string.replace("%player%", player);
		return this;
	}
	
	public Stringer challenge(Challenge challenge) {
		string = string.replace("%challenge%", challenge.getDisplayName());
		return this;
	}
	
	public Stringer required_challenges(String required_challenges) {
		string = string.replace("%required_challenges%", required_challenges);
		return this;
	}
	
	public Stringer required_challenge(Challenge required_challenge) {
		string = string.replace("%required_challenge%", required_challenge.getDisplayName());
		return this;
	}
	
	public Stringer rank(Rank rank) {
		string = string.replace("%rank%", rank.getDisplayName());
		return this;
	}
	
	public Stringer required_rank(Rank required_rank) {
		string = string.replace("%required_rank%", required_rank.getDisplayName());
		return this;
	}
	
	@Override
	public Stringer amount(int amount) {
		string = string.replace("%amount%", String.valueOf(amount));
		return this;
	}

	public Stringer item(String item) {
		string = string.replace("%item%", item);
		return this;
	}
	
	public Stringer item(ItemStack item) {
		string = string.replace("%item%", item.hasItemMeta() ? item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().getKey().getKey().replace("_", " ").toLowerCase() : item.getType().getKey().getKey().replace("_", " ").toLowerCase());
		return this;
	}
	
	public Stringer text(String text) {
		string = string.replace("%text%", text);
		return this;
	}
	
	public Stringer level(int level) {
		string = string.replace("%level%", String.valueOf(level));
		return this;
	}
	
	public Stringer island_level(long island_level) {
		string = string.replace("%island_level%", String.valueOf(island_level));
		return this;
	}
	
	public Stringer owner(OfflinePlayer owner) {
		string = string.replace("%owner%", owner.isOnline() ? owner.getName() : owner.getPlayer().getDisplayName());
		return this;
	}
	
	public Stringer f(String f) {
		string = string.replace("%f%", f);
		return this;
	}
	
	public Stringer currency(String currency) {
		string = string.replace("%currency%", currency);
		return this;
	}
	
	public Stringer reason(String reason) {
		string = string.replace("%reason%", reason);
		return this;
	}
	
	public Stringer message(String message) {
		string = string.replace("%message%", message);
		return this;
	}

	@Override
	public Stringer parse(Player player) {		
		string = Library.getParser().parse(player, string);
		return this;		
	}
	
	@Override
	public Stringer parse(OfflinePlayer player) {		
		string = Library.getParser().parse(player, string);
		return this;		
	}
	
	@Override
	public Stringer parse(CommandSender sender) {		
		string = Library.getParser().parse(sender, string);
		return this;		
	}

	@Override
	public String create() {
		return Messenger.formatString(string.replace("%f%", Options.BROADCAST_FORMAT.asString()));
	}

	@Override
	public IStringer message(IMessage message) {
		string = message.value();
		return this;
	}
	
}
