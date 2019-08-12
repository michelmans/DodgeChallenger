package me.alchemi.dodgechallenger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.alchemi.al.configurations.SexyConfiguration;
import me.alchemi.al.objects.base.ConfigBase;
import me.alchemi.al.objects.base.PluginBase;
import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import me.alchemi.dodgechallenger.managers.RankManager;
import me.alchemi.dodgechallenger.objects.DodgeIsland;
import me.alchemi.dodgechallenger.objects.Rank;
import me.goodandevil.skyblock.api.island.IslandManager;

public class Config extends ConfigBase {
	
	public Config(PluginBase plugin) throws FileNotFoundException, IOException, InvalidConfigurationException {
		super(plugin);
	}
	
	public static enum ConfigEnum implements IConfigEnum{
		
		CONFIG(new File(Dodge.getInstance().getDataFolder(), "config.yml"), 6),
		MESSAGES(new File(Dodge.getInstance().getDataFolder(), "messages.yml"), 10),
		CHALLENGES(new File(Dodge.getInstance().getDataFolder(), "challenges.yml"), 7);
		
		final File file;
		final int version;
		SexyConfiguration config;
		
		private ConfigEnum(File file, int version) {
			this.file = file;
			this.version = version;
			this.config = SexyConfiguration.loadConfiguration(file);
		}
		
		@Override
		public SexyConfiguration getConfig() {
			return config;
		}

		@Override
		public File getFile() {
			return file;
		}

		@Override
		public int getVersion() {
			return version;
		}
		
	}

	public static enum Messages implements IMessage{
		COMMANDS_NOPERMISSION("DodgeChallenger.Commands.NoPermission"),
		COMMANDS_WRONGFORMAT("DodgeChallenger.Commands.WrongFormat"),
		COMMANDS_UNKNOWN("DodgeChallenger.Commands.Unknown"),
		COMMANDS_NOCHALLENGE("DodgeChallenger.Commands.NoChallenge"),
		COMMANDS_NOISLAND("DodgeChallenger.Commands.NoIsland"),
		CHALLENGE_BROADCASTCOMPLETED("DodgeChallenger.Challenge.BroadcastCompleted"),
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
		CHALLENGE_LOCKED_SINGLE("DodgeChallenger.Challenge.Locked.Single"),
		CHALLENGE_LOCKED_MULTIPLE("DodgeChallenger.Challenge.Locked.Multiple"),
		CHALLENGE_LOCKED_AMOUNT("DodgeChallenger.Challenge.Locked.Amount"),
		CHALLENGE_LOCKED_NAME("DodgeChallenger.Challenge.Locked.Name"),
		CHALLENGE_LOCKED_LORE("DodgeChallenger.Challenge.Locked.Lore"),
		CHALLENGE_MISSING_LEVEL("DodgeChallenger.Challenge.Missing.Level"),
		CHALLENGE_MISSING_BASE("DodgeChallenger.Challenge.Missing.Base"),
		CHALLENGE_MISSING_ITEM("DodgeChallenger.Challenge.Missing.Item"),
		RANK_BROADCASTRANKUP("DodgeChallenger.Rank.BroadcastRankup"),
		RANK_MESSAGE("DodgeChallenger.Rank.Message"),
		RANK_TAG("DodgeChallenger.Rank.Tag"),
		GUINAME("DodgeChallenger.GUIName"),
		GUI_NEXTPAGE("DodgeChallenger.GUI.NextPage"),
		GUI_PREVPAGE("DodgeChallenger.GUI.PrevPage"),
		ADMIN_COMPLETE_CHALLENGE("DodgeChallenger.Admin.Complete.Challenge"),
		ADMIN_COMPLETE_RANK("DodgeChallenger.Admin.Complete.Rank"),
		ADMIN_RESET("DodgeChallenger.Admin.Reset"),
		ADMIN_RESETALL("DodgeChallenger.Admin.ResetAll");
		
		String value;
		String key;
		
		private Messages(String key) {
			this.key = key;
		}
		
		public void get() { 
			value = getConfig().getString(key);
			
		}
		
		public String value() {
			return value;
		}

		@Override
		public String key() {
			return key;
		}

		@Override
		public SexyConfiguration getConfig() {
			return ConfigEnum.MESSAGES.getConfig();
		}
	}
	
	public static enum Options implements IConfig {
		
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
		SPAWN_WITHERSKELETON("Spawn.WitherSkeleton"),
		SPAWN_BLAZE("Spawn.Blaze"),		
		SPAWN_GUARDIAN("Spawn.Guardian"),
		NEXT_PAGE_MATERIAL("nextPageMaterial"),
		PREV_PAGE_MATERIAL("prevPageMaterial");
		
		
		private Object value;
		public final String key;
		
		Options(String key){
			this.key = key;
			get();
		}
		
		
		
		@Override
		public void get() {
			value = getConfig().get(key);
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



		@Override
		public String key() {
			return key;
		}



		@Override
		public SexyConfiguration getConfig() {
			return ConfigEnum.CONFIG.getConfig();
		}



		@Override
		public double asDouble() {
			// TODO Auto-generated method stub
			return 0;
		}



		@Override
		public List<Float> asFloatList() {
			// TODO Auto-generated method stub
			return null;
		}



		@Override
		public List<Integer> asIntList() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static enum DataBase implements IConfig {
		
		ENABLED("MySQL.enabled"),
		HOST("MySQL.host"),
		PORT("MySQL.port"),
		DATABASE("MySQL.database"),
		USERNAME("MySQL.username"),
		PASSWORD("MySQL.password");

		String key;
		Object value;
		
		private DataBase(String key) {
			this.key = key;
			get();
		}
		
		@Override
		public Object value() {
			return value;
		}

		@Override
		public void get() {
			
			this.value = getConfig().get(key);
			
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

		@Override
		public String key() {
			return key;
		}

		@Override
		public SexyConfiguration getConfig() {
			return ConfigEnum.CONFIG.getConfig();
		}

		@Override
		public double asDouble() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public List<Float> asFloatList() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Integer> asIntList() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	@Override
	public void reload() {
		super.reload();
		
		RankManager.getManager().purge();
		DodgeIslandManager.getManager().purge();
		
		ConfigurationSection section = ConfigEnum.CHALLENGES.getConfig().getConfigurationSection("ranks");
		for (String path : section.getValues(false).keySet()) {
			new Rank(section.getConfigurationSection(path));
		}
		
		DataBase.ENABLED.set(false);
		
		for (File islandFile : new File(Dodge.getInstance().getDataFolder(), "islands").listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.matches("(\\d+-\\d+-\\d+\\.yml)");
			}
		})) {
			
			try {
				Dodge.getInstance().getMessenger().print("Attempting to rename " + islandFile.getName());
				OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(YamlConfiguration.loadConfiguration(islandFile).getString("owner")));
				UUID id = DodgeIslandManager.getIslandUUID(player);
				islandFile.renameTo(new File(islandFile.getParentFile(), id.toString() + ".yml"));
			} catch(IllegalAccessError e) {
				e.printStackTrace();
			}
			
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (IslandManager.hasIsland(player) && DodgeIslandManager.getManager().getByPlayer(player) == null) {
				
				new DodgeIsland(DodgeIslandManager.getIslandUUID(player));
				
			}
		}
	}
	
	@Override
	protected IConfigEnum[] getConfigs() {
		return ConfigEnum.values();
	}

	@Override
	protected Set<IConfig> getEnums() {
		return new HashSet<ConfigBase.IConfig>() {
			{
				addAll(Arrays.asList(DataBase.values()));
				addAll(Arrays.asList(Options.values()));
			}
		};
	}

	@Override
	protected Set<IMessage> getMessages() {
		return Stream.of(Messages.values()).collect(Collectors.toSet());
	}
	
}
