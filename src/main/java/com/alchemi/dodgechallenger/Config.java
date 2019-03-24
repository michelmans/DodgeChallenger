package com.alchemi.dodgechallenger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.alchemi.al.configurations.SexyConfiguration;
import com.alchemi.dodgechallenger.listeners.events.IslandEvents;
import com.alchemi.dodgechallenger.managers.RankManager;

import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.island.IslandManager;

public class Config {

	public static SexyConfiguration config;
	public static SexyConfiguration messages;
	public static SexyConfiguration challenges;

	private interface ConfigInterface {
		
		Object value();
		
		void get();
		
		boolean asBoolean();
		
		String asString();
		
		Sound asSound();
		
		List<String> asStringList();
		
		int asInt();
		
		ItemStack asItemStack();
		
		Material asMaterial();
	}
	
	public static enum MESSAGES{
		
		COMMANDS_NO_PERMISSION("DodgeChallenger.Commands.NoPermission"),
		COMMANDS_WRONG_FORMAT("DodgeChallenger.Commands.WrongFormat"),
		COMMANDS_UNKNOWN("DodgeChallenger.Commands.Unknown"),
		COMMANDS_NO_CHALLENGE("DodgeChallenger.Commands.NoChallenge"),
		COMMANDS_NO_ISLAND("DodgeChallenger.Commands.NoIsland"),
		COMMANDS_COMPLETE("DodgeChallenger.Commands.Complete"),
		COMMANDS_RANK("DodgeChallenger.Commands.Rank"),
		COMMANDS_RESET("DodgeChallenger.Commands.Reset"),
		COMMANDS_RESETALL("DodgeChallenger.Commands.ResetAll"),
		CHALLENGE_BROADCAST_COMPLETED("DodgeChallenger.Challenge.BroadcastCompleted"),
		CHALLENGE_MESSAGE("DodgeChallenger.Challenge.Message"),
		CHALLENGE_LORE_REQUIRES("DodgeChallenger.Challenge.Lore.Requires"),
		CHALLENGE_LORE_ITEM("DodgeChallenger.Challenge.Lore.Item"),
		CHALLENGE_LORE_ONPLAYER("DodgeChallenger.Challenge.Lore.onPlayer"),
		CHALLENGE_LORE_ONISLAND("DodgeChallenger.Challenge.Lore.onIsland"),
		CHALLENGE_LORE_REWARD_BASE("DodgeChallenger.Challenge.Lore.Reward.Base"),
		CHALLENGE_LORE_REWARD_TEXT("DodgeChallenger.Challenge.Lore.Reward.Text"),
		CHALLENGE_CANCOMPLETE("DodgeChallenger.Challenge.CanComplete"),
		CHALLENGE_CANNOTCOMPLETE("DodgeChallenger.Challenge.CannotComplete"),
		CHALLENGE_COMPLETED("DodgeChallenger.Challenge.Completed"),
		CHALLENGE_NOTREPEATABLE("DodgeChallenger.Challenge.NotRepeatable"),
		CHALLENGE_LOCKED_RANK("DodgeChallenger.Challenge.Locked.Rank"),
		CHALLENGE_LOCKED_CHALLENGE("DodgeChallenger.Challenge.Locked.Challenge"),
		CHALLENGE_LOCKED_CHALLENGES("DodgeChallenger.Challenge.Locked.Challenges"),
		CHALLENGE_LOCKED_LOCKED("DodgeChallenger.Challenge.Locked.Locked"),
		CHALLENGE_MISSING_LEVEL("DodgeChallenger.Challenge.Missing.Level"),
		CHALLENGE_MISSING_BASE("DodgeChallenger.Challenge.Missing.Base"),
		CHALLENGE_MISSING_ITEM("DodgeChallenger.Challenge.Missing.Item"),
		RANK_BROADCAST_RANKUP("DodgeChallenger.Rank.BroadcastRankup"),
		RANK_TAG("DodgeChallenger.Rank.Tag"),
		GUI_GUINAME("DodgeChallenger.GUI.GUIName"),
		GUI_NEXTPAGE("DodgeChallenger.GUI.NextPage"),
		GUI_PREVPAGE("DodgeChallenger.GUI.PrevPage");
		
		String value;
		String key;
		
		private MESSAGES(String key) {
			this.key = key;
		}
		
		public void get() { 
			value = messages.getString(key);
			
		}
		
		public String value() {
			return value;
		}
	}
	
	public static enum OPTIONS implements ConfigInterface {
		
		BROADCAST_COMPLETION("broadcastCompletion"),
		BROADCAST_RANKUP("broadcastRankup"),
		BROADCAST_FORMAT("broadcastFormat"),
		REQUIRE_PREVIOUS_RANK("requirePreviousRank"),
		RANKLEEWAY("rankLeeway"),
		RANK_IGNORE_STACKED("rankIgnoreStacked"),
		DEFAULT_RESET_IN_HOURS("defaultResetInHours"),
		RESET_CHALLENGES("resetChallenges"),
		LOCKED_DISPLAY_ITEM("lockedDisplayItem"),
		SHOW_LOCKED_CHALLENGE_NAME("showLockedChallengeName"),
		NO_COMPLETE_SOUND("noCompleteSound"),
		COMPLETE_SOUND("completeSound"),
		CLEAR_PLAYER_INVENTORY("clearPlayerInventory"),
		SHOW_RANK("showRank"),
		NEXT_PAGE_MATERIAL("nextPageMaterial"),
		PREV_PAGE_MATERIAL("prevPageMaterial");
		
		
		private Object value;
		public final String key;
		
		OPTIONS(String key){
			this.key = key;
			get();
		}
		
		
		
		@Override
		public void get() {
			value = config.get(key);
		}

		@Override
		public Object value() {
			return value;
		}

		@Override
		public boolean asBoolean() {
			return Boolean.parseBoolean(asString());
		}

		@Override
		public String asString() {
			return String.valueOf(value);
		}

		@Override
		public Sound asSound() {
			
			return Sound.valueOf(asString());
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<String> asStringList() {
			try {
				return (List<String>) value;
			} catch (ClassCastException e) { return null; }
		}

		@Override
		public int asInt() {
			return Integer.valueOf(asString());
		}

		@Override
		public ItemStack asItemStack() {
			try {
				return (ItemStack) value;
			} catch (ClassCastException e) { return null; }
		}

		@Override
		public Material asMaterial() {
			return Material.valueOf(asString());
		}
	}
	
	public static enum DATABASE implements ConfigInterface {
		
		ENABLED("MySQL.enabled"),
		HOST("MySQL.host"),
		PORT("MySQL.port"),
		DATABASE("MySQL.database"),
		USERNAME("MySQL.username"),
		PASSWORD("MySQL.password");

		String key;
		Object value;
		
		private DATABASE(String key) {
			this.key = key;
			get();
		}
		
		@Override
		public Object value() {
			return value;
		}

		@Override
		public void get() {
			
			this.value = config.get(key);
			
		}
		
		public void set(Object val) {
			this.value = val;
		}

		@Override
		public boolean asBoolean() {
			return Boolean.parseBoolean(asString());
		}

		@Override
		public String asString() {
			return String.valueOf(value);
		}

		@Override
		public Sound asSound() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<String> asStringList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int asInt() {
			
			return Integer.parseInt(asString());
		}

		@Override
		public ItemStack asItemStack() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Material asMaterial() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public static void enable() throws FileNotFoundException, IOException, InvalidConfigurationException {
		config = SexyConfiguration.loadConfiguration(main.CONFIG_FILE);
		messages = SexyConfiguration.loadConfiguration(main.MESSAGES_FILE);
		challenges = SexyConfiguration.loadConfiguration(main.CHALLENGES_FILE);
		
		
		for (SexyConfiguration file : new SexyConfiguration[] {messages, config, challenges}) {
			
			int version;
			if (file.equals(config)) {
				version = main.CONFIG_FILE_VERSION;
			} else if (file.equals(messages)) {
				version = main.MESSAGES_FILE_VERSION;
			} else if (file.equals(challenges)) {
				version = main.CHALLENGES_FILE_VERSION;
			} else version = 0;
			
			if(!file.getFile().exists()) {
				main.instance.saveResource(file.getFile().getName(), false);
			}
			config.setComment("broadcastFormat", "# The formatting of the broadcast text.");
			
			if(!file.isSet("File-Version-Do-Not-Edit") 
					|| !file.get("File-Version-Do-Not-Edit").equals(version)) {
				main.messenger.print("Your $file$ is outdated! Updating...".replace("$file$", file.getFile().getName()));
				file.load(new InputStreamReader(main.instance.getResource(file.getFile().getName())));
				file.update(SexyConfiguration.loadConfiguration(new InputStreamReader(main.instance.getResource(file.getFile().getName()))));
				file.set("File-Version-Do-Not-Edit", version);
				file.save();
				main.messenger.print("File successfully updated!");
			}
		}
		
		
		for (OPTIONS value : OPTIONS.values()) {
			value.get();
		}
		
		for (MESSAGES value : MESSAGES.values()) {
			value.get();
		}
		
		for (DATABASE value : DATABASE.values()) {
			value.get();
		}
		
		ConfigurationSection section = challenges.getConfigurationSection("ranks");
		for (String path : section.getValues(false).keySet()) {
			new RankManager(section.getConfigurationSection(path));
		}
		
	}
	
	public static void reload() {
		config = SexyConfiguration.loadConfiguration(config.getFile());
		messages = SexyConfiguration.loadConfiguration(messages.getFile());
		challenges = SexyConfiguration.loadConfiguration(challenges.getFile());
		
		for (OPTIONS value : OPTIONS.values()) {
			value.get();
		}
		
		for (MESSAGES value : MESSAGES.values()) {
			value.get();
		}
		
		for (DATABASE value : DATABASE.values()) {
			value.get();
		}
		
		RankManager.purge();
		
		ConfigurationSection section = challenges.getConfigurationSection("ranks");
		for (String path : section.getValues(false).keySet()) {
			new RankManager(section.getConfigurationSection(path));
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (IslandManager.hasIsland(player)) {
				int rank = main.dbm.getRank(SkyBlockAPI.getIslandManager().getIsland(player));
				
				if (Config.OPTIONS.SHOW_RANK.asBoolean()) {
					IslandEvents.setRankPrefix(player, rank);
				}
				
				if (com.alchemi.dodgechallenger.managers.IslandManager.getByIsland(SkyBlockAPI.getIslandManager().getIsland(player)) == null) {
					new com.alchemi.dodgechallenger.managers.IslandManager(SkyBlockAPI.getIslandManager().getIsland(player));
				}
				
			}
		}
	}
	
	public static void save() {
		for (OPTIONS value : OPTIONS.values()) {
			config.set(value.key, value.value);
		}
		
		for (MESSAGES value : MESSAGES.values()) {
			messages.set(value.key, value.value);
		}
		
		for (DATABASE value : DATABASE.values()) {
			config.set(value.key, value.value);
		}
		
		try {
			config.save();
			messages.save();
		} catch (IOException e) {e.printStackTrace();}
	}
	
}
