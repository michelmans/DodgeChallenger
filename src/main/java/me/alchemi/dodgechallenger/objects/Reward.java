package me.alchemi.dodgechallenger.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.alchemi.al.Library;
import me.alchemi.al.objects.handling.ItemFactory;
import me.alchemi.dodgechallenger.Dodge;
import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.island.Island;
import me.goodandevil.skyblock.api.island.IslandRole;

public class Reward {

	private List<ItemStack> items = new ArrayList<ItemStack>();
	private int money = 0;
	private int xp = 0;
	private List<String> commands = new ArrayList<String>();
	
	public Reward(Challenge challenge, boolean repeat) {
		ConfigurationSection sec = challenge.getSection().getConfigurationSection("reward");
		
		if (repeat) sec = challenge.getSection().getConfigurationSection("repeatReward"); 
		
		if (sec == null) return;
		
		for (String path : sec.getKeys(false)) {
			if (path.equals("text")) continue;
			else if (path.equals("items")) {
				for (String item : sec.getStringList(path)) {
					
					if (item.matches("\\{p=.+")) {
						
						Matcher m = Pattern.compile("(\\{p=.*\\})").matcher(item);
						if (m.find()) {
							
							m = Pattern.compile("(\\d\\.\\d*)").matcher(m.group());
							if (m.find()) {
								int percentage = Math.round(Float.valueOf(m.group()) * 100);
								item = item.replaceFirst("(\\{.+\\})", "");
								
								m = Pattern.compile("\\w+").matcher(item);
								if (m.find() && Material.getMaterial(m.group()) != null) { 
									Material mat = Material.getMaterial(m.group());
									
									m = Pattern.compile("(\\d)+").matcher(item);
									int amount = 1;
									if (m.find()) amount = Integer.valueOf(m.group());
									
									ItemFactory itemF = new ItemFactory(mat);
									
									Matcher nbt = Pattern.compile("\\{.*").matcher(item);
									if (nbt.find()) {
										itemF.parseNBT(nbt.group());
									}
									
									Random rand = new Random();
									for (int i = 0; i < percentage; i++) {
										if (rand.nextInt(100) == percentage) {
											items.add(itemF.setNum(amount));
											break;
										}
									}
								}
							}
						}
					} else {
						
						Matcher m = Pattern.compile("\\w+").matcher(item);
						if (m.find() && Material.getMaterial(m.group()) != null) { 
							Material mat = Material.getMaterial(m.group());
							ItemFactory itemF = new ItemFactory(mat);
							m = Pattern.compile("(\\d)+").matcher(item);
							int amount = 1;
							if (m.find()) amount = Integer.valueOf(m.group());
							itemF.setNum(amount);
							Matcher nbt = Pattern.compile("\\{.*").matcher(item);
							
							if (nbt.find()) {
								itemF.parseNBT(nbt.group());
							}
							
							items.add(itemF);
							
						}
					}
				}
			} else if (path.equals("currency")) {
				this.money = sec.getInt(path);
			} else if (path.equals("xp")) {
				this.xp = sec.getInt(path);
			} else if (path.equals("commands")) {
				commands.addAll(sec.getStringList(path));
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void give(OfflinePlayer player) {
		
		if (player.isOnline()) {
		
			if (items.size() > 0) {
				for (ItemStack item : items) {
					Library.giveItemStack(item, player.getPlayer());
				}
			}
			if (money > 0 && Dodge.eco != null) {
				Dodge.eco.depositPlayer(player, money);
			}
			if (xp > 0) {
				player.getPlayer().giveExp(xp);
			}
		} else {
			
			if (money > 0 && Dodge.eco != null) {
				Dodge.eco.depositPlayer(player, money);
			}
			
			if (Dodge.getInstance().GIVE_QUEUE.contains(player.getName())) {
				
				ConfigurationSection sec = Dodge.getInstance().GIVE_QUEUE.getConfigurationSection(player.getName());
				
				if (xp > 0) {
					int xp2 = sec.getInt("xp", 0);
					sec.set("xp", xp2 + xp);
				}
				
				if (items.size() > 0) {
					List<ItemStack> items2 = (List<ItemStack>) sec.getList("items", new ArrayList<ItemStack>());
					items2.addAll(items);
					sec.set("items", items2);
				}
				Dodge.getInstance().GIVE_QUEUE.createSection(player.getName(), sec.getValues(true));
			} else {
				
				ConfigurationSection sec = Dodge.getInstance().GIVE_QUEUE.createSection(player.getName());
				
				if (xp > 0) {
					sec.set("xp", xp);
				}
				
				if (items.size() > 0) {
					sec.set("items", items);
				}
				
				Dodge.getInstance().GIVE_QUEUE.createSection(player.getName(), sec.getValues(true));
				
			}
			
			try {
				Dodge.getInstance().GIVE_QUEUE.save();
			} catch (IOException e) {}
			
		}
		
		if (commands.size() > 0) {
			for (String cmd : commands) {
				if (cmd.contains("\\{player\\}")) cmd = cmd.replaceAll("\\{player\\}", player.getName());
				if (cmd.contains("\\{island\\}")) {
					Island is = SkyBlockAPI.getIslandManager().getIsland(player);
					for (UUID uuid : is.getPlayersWithRole(IslandRole.MEMBER)) {
						Player p = Bukkit.getPlayer(uuid);
						if (!p.isOnline()) continue;
						String command = cmd.replaceAll("\\{island\\}", p.getName());
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
					}
					for (UUID uuid : is.getPlayersWithRole(IslandRole.OPERATOR)) {
						Player p = Bukkit.getPlayer(uuid);
						if (!p.isOnline()) continue;
						String command = cmd.replaceAll("\\{island\\}", p.getName());
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
					}
					for (UUID uuid : is.getPlayersWithRole(IslandRole.OWNER)) {
						Player p = Bukkit.getPlayer(uuid);
						if (!p.isOnline()) continue;
						String command = cmd.replaceAll("\\{island\\}", p.getName());
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
					}
				}
			}
		}
	}
	
	public List<String> getCommands() {
		return commands;
	}
	
	public List<ItemStack> getItems() {
		return items;
	}
	
	public int getMoney() {
		return money;
	}
	
	public int getXp() {
		return xp;
	}
}
