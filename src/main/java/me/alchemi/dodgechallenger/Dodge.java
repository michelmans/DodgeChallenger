package me.alchemi.dodgechallenger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice.MaterialChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.configurations.SexyConfiguration;
import me.alchemi.al.objects.Container;
import me.alchemi.al.objects.base.PluginBase;
import me.alchemi.al.objects.handling.ItemFactory;
import me.alchemi.dodgechallenger.Config.Data;
import me.alchemi.dodgechallenger.listeners.commands.CommandChallenge;
import me.alchemi.dodgechallenger.listeners.events.CreatureSpawn;
import me.alchemi.dodgechallenger.listeners.events.PlayerLoginLogout;
import me.alchemi.dodgechallenger.listeners.events.island.IslandCreateDelete;
import me.alchemi.dodgechallenger.listeners.events.island.IslandRename;
import me.alchemi.dodgechallenger.listeners.events.rank.ChallengeComplete;
import me.alchemi.dodgechallenger.listeners.events.rank.Ranks;
import me.alchemi.dodgechallenger.listeners.tabcomplete.ChallengeTabComplete;
import me.alchemi.dodgechallenger.managers.ConfigurationManager;
import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import me.alchemi.dodgechallenger.managers.IDataManager;
import me.alchemi.dodgechallenger.managers.MySQLManager;
import me.alchemi.dodgechallenger.managers.RankManager;
import me.alchemi.dodgechallenger.managers.SQLiteManager;
import me.alchemi.dodgechallenger.objects.StorageSystem;
import me.alchemi.dodgechallenger.objects.placeholder.PapiExpansion;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class Dodge extends PluginBase {

	public static Dodge instance;
	
	public static Economy eco;
	public static Chat chat;
	public static boolean chatEnabled;
	
	public static boolean playerVaults;
	
	public static IDataManager dataManager;
	
	public List<ShapelessRecipe> recipes = new ArrayList<ShapelessRecipe>();

	public SexyConfiguration GIVE_QUEUE;
	
	public Config conf;
	
	static {
		ConfigurationSerialization.registerClass(Container.class);
	}
	
	@Override
	public void onEnable() {
		
		for (String s : getDescription().getDepend()) {
			if (Bukkit.getPluginManager().getPlugin(s) == null 
					|| !Bukkit.getPluginManager().isPluginEnabled(s)) {
				Bukkit.getLogger().log(Level.SEVERE, ChatColor.translateAlternateColorCodes('&', "&4&lDependency %depend% not found, disabling plugin...".replace("%depend%", s)));
				getServer().getPluginManager().disablePlugin(this);
			}
		}
		
		instance = this;
		
		GIVE_QUEUE = SexyConfiguration.loadConfiguration(new File(getDataFolder(), "give_queue.yml"));
		
		RankManager.enable();
		DodgeIslandManager.enable();
		
		setMessenger(new Messenger(this));
		messenger.print("Enabling DodgeChallenger");
		
		try {
			conf = new Config(this);
			messenger.print("Configs enabled.");
			if (!GIVE_QUEUE.getFile().exists()) GIVE_QUEUE.getFile().createNewFile();
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
			Messenger.printStatic("Configs enabling errored, disabling plugin.", "[DodgeChallenger]");
		}
		
		if (StorageSystem.valueOf(Config.Data.STORAGE.asString()) == StorageSystem.YML) {
			messenger.print("Using yml database.");
			dataManager = new ConfigurationManager();
		} else if (StorageSystem.valueOf(Data.STORAGE.asString()) == StorageSystem.SQLITE) {
			messenger.print("Using SQLite database.");
			dataManager = new SQLiteManager();
		} else {
			messenger.print("Using MySQL database.");
			dataManager = new MySQLManager();
		}
		messenger.print("Database Initiliazed.");
		
		if (!setupEconomy()) {
			messenger.print("No Vault Economy dependency found, disabled eco hook.");
		}
		if (!(chatEnabled = setupChat())) {
			messenger.print("No Vault Chat dependency found, using displaynames instead.");
		}
		
		if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PapiExpansion().register();
            messenger.print("Placeholders registered.");
        }
		
		registerEvents();
		
		registerCommands();
		
		messenger.print("Ranks and challenges created and registered.");
		
		playerVaults = getServer().getPluginManager().isPluginEnabled("PlayerVaultsX") 
				|| getServer().getPluginManager().isPluginEnabled("PlayerVaults") 
				|| getServer().getPluginManager().getPlugin("PlayerVaultsX") != null 
				|| getServer().getPluginManager().getPlugin("PlayerVaults") != null;  
		
		registerRecipes();
		
		messenger.print("Initialization complete.");		
	}
	
	@Override
	public void onDisable() {
		
		if (StorageSystem.valueOf(Data.STORAGE.asString()) == StorageSystem.YML) {
			
			Dodge.getInstance().getMessenger().print("Running data queries: " + ((ConfigurationManager)Dodge.dataManager).querySize());
			((ConfigurationManager)Dodge.dataManager).runQuery();
			
		} else if (StorageSystem.valueOf(Data.STORAGE.asString()) == StorageSystem.MYSQL) {
			
			((MySQLManager)Dodge.dataManager).getDatabase().onDisable();
			
		}
		
	}
	
	private void registerCommands() {
		getCommand("challenges").setExecutor(new CommandChallenge());
		
		getCommand("challenges").setTabCompleter(new ChallengeTabComplete());
	}
	
	private void registerEvents() {
		for (Listener listen : Arrays.asList(new IslandCreateDelete(), new Ranks(),
				new CreatureSpawn(), new PlayerLoginLogout(), new ChallengeComplete(), new IslandRename())) {
			Bukkit.getPluginManager().registerEvents(listen, this);
		}
	}
	
	private void registerRecipes() {
		//recipes
		Bukkit.resetRecipes();

		ItemStack item = new ItemFactory(MaterialWrapper.NAME_TAG);

		ShapelessRecipe recipe = new ShapelessRecipe(item.getType().getKey(), item);

		recipe.addIngredient(MaterialWrapper.SLIME_BALL.getMaterial());
		recipe.addIngredient(MaterialWrapper.STRING.getMaterial());
		recipe.addIngredient(MaterialWrapper.PAPER.getMaterial());
		recipe.addIngredient(MaterialWrapper.INK_SAC.getMaterial());

		recipes.add(recipe);
		Bukkit.addRecipe(recipe);
		messenger.print("Nametag recipe added.");

		recipe = new ShapelessRecipe(new NamespacedKey(instance, "sandstone_to_sand"), new ItemStack(MaterialWrapper.SAND.getMaterial(), 4));
		recipe.addIngredient(new MaterialChoice(MaterialWrapper.SANDSTONE.getMaterial(), 
				MaterialWrapper.CHISELED_SANDSTONE.getMaterial(), 
				MaterialWrapper.CUT_SANDSTONE.getMaterial(), 
				MaterialWrapper.SMOOTH_SANDSTONE.getMaterial()));

		recipes.add(recipe);
		Bukkit.addRecipe(recipe);

		recipe = new ShapelessRecipe(new NamespacedKey(instance, "redsandstone_to_redsand"), new ItemStack(MaterialWrapper.RED_SAND.getMaterial(), 4));
		recipe.addIngredient(new MaterialChoice(MaterialWrapper.RED_SANDSTONE.getMaterial(), 
				MaterialWrapper.CHISELED_RED_SANDSTONE.getMaterial(), 
				MaterialWrapper.CUT_RED_SANDSTONE.getMaterial(), 
				MaterialWrapper.SMOOTH_RED_SANDSTONE.getMaterial()));

		recipes.add(recipe);
		Bukkit.addRecipe(recipe);

		messenger.print("Sandstone to sand recipes added.");

		MaterialWrapper[] slabs = new MaterialWrapper[] {MaterialWrapper.ACACIA_SLAB, MaterialWrapper.BIRCH_SLAB,
				MaterialWrapper.BRICK_SLAB, MaterialWrapper.COBBLESTONE_SLAB, MaterialWrapper.DARK_OAK_SLAB,
				MaterialWrapper.DARK_PRISMARINE_SLAB, MaterialWrapper.JUNGLE_SLAB, MaterialWrapper.NETHER_BRICK_SLAB,
				MaterialWrapper.OAK_SLAB, MaterialWrapper.PRISMARINE_BRICK_SLAB, MaterialWrapper.PRISMARINE_SLAB,
				MaterialWrapper.SPRUCE_SLAB};

		ItemFactory[] blocks = new ItemFactory[] {new ItemFactory(MaterialWrapper.ACACIA_PLANKS),
				new ItemFactory(MaterialWrapper.BIRCH_PLANKS),
				new ItemFactory(MaterialWrapper.BRICKS),
				new ItemFactory(MaterialWrapper.COBBLESTONE),
				new ItemFactory(MaterialWrapper.DARK_OAK_PLANKS),
				new ItemFactory(MaterialWrapper.DARK_PRISMARINE),
				new ItemFactory(MaterialWrapper.JUNGLE_PLANKS),
				new ItemFactory(MaterialWrapper.NETHER_BRICKS),
				new ItemFactory(MaterialWrapper.OAK_PLANKS),
				new ItemFactory(MaterialWrapper.PRISMARINE_BRICKS),
				new ItemFactory(MaterialWrapper.PRISMARINE),
				new ItemFactory(MaterialWrapper.SPRUCE_PLANKS)};

		for (int i = 0; i < slabs.length; i++) {

			recipe = new ShapelessRecipe(new NamespacedKey(this, blocks[i].getType().getKey().getKey() + "_from_slab"), blocks[i]);

			recipe.addIngredient(2, slabs[i].getMaterial());

			recipes.add(recipe);
			Bukkit.addRecipe(recipe);
			messenger.print(recipe.getKey().getKey().replaceAll("_", " ").toLowerCase() + " recipe added.");

		}
	}
	
	private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            eco = economyProvider.getProvider();
        }

        return (eco != null);
    }
	
	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		return (chat != null);
	}
	
	public static Dodge getInstance() {
		return instance;
	}
	
	public Messenger getMessenger() {
		return messenger;
	}
	
}
