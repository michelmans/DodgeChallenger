package com.alchemi.dodgechallenger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.dodgechallenger.gui.GuiListener;
import com.alchemi.dodgechallenger.listeners.commands.CommandChallenge;
import com.alchemi.dodgechallenger.listeners.commands.admin.CommandAdmin;
import com.alchemi.dodgechallenger.listeners.events.IslandEvents;
import com.alchemi.dodgechallenger.listeners.tabcomplete.AdminTabComplete;
import com.alchemi.dodgechallenger.managers.DataManager;
import com.alchemi.dodgechallenger.managers.DatabaseManager;
import com.alchemi.dodgechallenger.managers.RankManager;
import com.alchemi.dodgechallenger.objects.placeholder.PapiExpansion;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class main extends JavaPlugin {

	public static main instance;
	
	public static Messenger messenger;
	
	public static File CONFIG_FILE;
	public static File MESSAGES_FILE;
	public static File CHALLENGES_FILE;
	
	public static Economy eco;
	public static Chat chat;
	public static boolean chatEnabled;
	
	public static DatabaseManager dbm;
	
	public static final int CONFIG_FILE_VERSION = 4;
	public static final int MESSAGES_FILE_VERSION = 8;
	public static final int CHALLENGES_FILE_VERSION = 6;
	
	public static Map<Integer, String> rankTags = new HashMap<Integer, String>();
	
	public List<ShapelessRecipe> recipes = new ArrayList<ShapelessRecipe>();
	
	public GuiListener guiListener;
	
	@Override
	public void onEnable() {
		
		instance = this;
		
		CONFIG_FILE = new File(getDataFolder(), "config.yml");
		MESSAGES_FILE = new File(getDataFolder(), "messages.yml");
		CHALLENGES_FILE = new File(getDataFolder(), "challenges.yml");
		
		messenger = new Messenger(this);
		messenger.print("Enabling DodgeChallenger");
		
		try {
			Config.enable();
			messenger.print("Configs enabled.");
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
			Messenger.printStatic("Configs enabling errored, disabling plugin.", "[DodgeChallenger]");
		}
		
		if (Config.DATABASE.ENABLED.asBoolean()) {
			dbm = new DatabaseManager();
			messenger.print("Using MySQL database.");
		}
		else {
			dbm = new DataManager();
			messenger.print("Using yml database.");
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
            messenger.print("Placeholder registered.");
        }
		
		guiListener = new GuiListener(this);
		
		getServer().getPluginManager().registerEvents(new IslandEvents(), this);
		getServer().getPluginManager().registerEvents(guiListener, this);
		
		getCommand("challenges").setExecutor(new CommandChallenge());
		getCommand("chadmin").setExecutor(new CommandAdmin());
		getCommand("chadmin").setTabCompleter(new AdminTabComplete());
		
		for (RankManager rank : RankManager.getRanks()) {
			rankTags.put(rank.rank(), Config.MESSAGES.RANK_TAG.value()
					.replace("$rank$", rank.getDisplayName())
					.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString()));
		}
		messenger.print("Ranks and challenges created and registered.");
		
		
		//recipes
		
		Bukkit.resetRecipes();
		
		ItemStack item = new ItemStack(Material.NAME_TAG);
		
		ShapelessRecipe recipe = new ShapelessRecipe(item.getType().getKey(), item);
		
		recipe.addIngredient(Material.SLIME_BALL);
		recipe.addIngredient(Material.STRING);
		recipe.addIngredient(Material.PAPER);
		recipe.addIngredient(Material.INK_SAC);
		
		recipes.add(recipe);
		Bukkit.addRecipe(recipe);
		messenger.print("Nametag recipe added.");
		
		Material[] slabs = new Material[] {Material.ACACIA_SLAB, Material.BIRCH_SLAB,
				Material.BRICK_SLAB, Material.COBBLESTONE_SLAB, Material.DARK_OAK_SLAB,
				Material.DARK_PRISMARINE_SLAB, Material.JUNGLE_SLAB, Material.NETHER_BRICK_SLAB,
				Material.OAK_SLAB, Material.PRISMARINE_BRICK_SLAB, Material.PRISMARINE_SLAB,
				Material.SPRUCE_SLAB, Material.STONE_SLAB};
		
		ItemStack[] blocks = new ItemStack[] {new ItemStack(Material.ACACIA_PLANKS), new ItemStack(Material.BIRCH_PLANKS),
				new ItemStack(Material.BRICKS), new ItemStack(Material.COBBLESTONE), new ItemStack(Material.DARK_OAK_PLANKS),
				new ItemStack(Material.DARK_PRISMARINE), new ItemStack(Material.JUNGLE_PLANKS), new ItemStack(Material.NETHER_BRICKS),
				new ItemStack(Material.OAK_PLANKS), new ItemStack(Material.PRISMARINE_BRICKS), new ItemStack(Material.PRISMARINE),
				new ItemStack(Material.SPRUCE_PLANKS), new ItemStack(Material.SMOOTH_STONE)};
		
		for (int i = 0; i < slabs.length; i++) {
			
			recipe = new ShapelessRecipe(new NamespacedKey(this, blocks[i].getType().getKey().getKey() + "_from_slab"), blocks[i]);
			
			recipe.addIngredient(2, slabs[i]);
			
			recipes.add(recipe);
			Bukkit.addRecipe(recipe);
			messenger.print(recipe.getKey().getKey().replaceAll("_", " ").toLowerCase() + " recipe added.");
			
		}
		
		messenger.print("Initialization complete.");
		
		
	}
	
	@Override
	public void onDisable() {
		for (com.alchemi.dodgechallenger.managers.IslandManager im : com.alchemi.dodgechallenger.managers.IslandManager.getIslands().values()) {
			im.save();
		}
		
		main.messenger.print("Running data queries: " + main.dbm.querySize());
		dbm.runQuery();
	}
	
	private boolean setupEconomy()
    {
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
	
}
