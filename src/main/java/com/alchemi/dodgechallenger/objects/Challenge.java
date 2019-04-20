package com.alchemi.dodgechallenger.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.dodgechallenger.Config;
import com.alchemi.dodgechallenger.events.ChallengeCompleteEvent;
import com.alchemi.dodgechallenger.managers.IslandManager;
import com.alchemi.dodgechallenger.managers.RankManager;

import me.goodandevil.skyblock.api.SkyBlockAPI;
import me.goodandevil.skyblock.api.island.Island;

public class Challenge {

	private static HashMap<String, Challenge> challenges = new HashMap<String, Challenge>();
	private static List<Challenge> levelUpChallenges = new ArrayList<Challenge>();
	
	private final RankManager rank;
	
	private final String name;
	private final String displayName;
	private final String description;
	private final Type type;
	private final int radius;
	private final Map<Material, Integer> requiredItems = new HashMap<Material, Integer>();
	private final Map<DodgyEntity, Integer> requiredEntities = new HashMap<DodgyEntity, Integer>();
	private final Integer requiredLevel;
	private final boolean repeatable;
	private final Material displayItem;
	private final String rewardText;
	private final String repeatRewardText;
	private final boolean hasOffset;
	
	private final List<String> requiredChallenges;
	
	private final ConfigurationSection section;
	
	public Challenge(String name, ConfigurationSection sec, RankManager rank) {
		
		this.rank = rank;
		
		this.section = sec;
		this.name = name;
		this.displayName = sec.getString("name");
		this.description = sec.getString("description");
		this.type = Type.valueOf(sec.getString("type"));
		this.radius = sec.getInt("radius", 10);
		this.repeatable = sec.contains("repeatReward");
		
		//items
		if (this.type != Type.islandLevel) {
			for (String s : sec.getStringList("requiredItems")) {
				Matcher m = Pattern.compile(".*(?=\\:)").matcher(s);
				if (m.find() && Material.getMaterial(m.group()) != null) {
					Material mat = Material.getMaterial(m.group());
					m = Pattern.compile("[^:]*$").matcher(s);
					int amount = 1;
					if (m.find()) {
						amount = Integer.valueOf(m.group());
					}
					
					requiredItems.put(mat, amount);
				}
			}
			this.requiredLevel = null;
			
			if (sec.contains("requiredEntities")) {
				
				for (String entity : sec.getStringList("requiredEntities")) {
					
					Matcher patternWords = Pattern.compile("([A-z_\\s]+)").matcher(entity);
					Matcher patternNumbers = Pattern.compile("(\\d+)").matcher(entity);
					Matcher patternNBT = Pattern.compile("([{].*)").matcher(entity);
					
					String nbt = patternNBT.find() ? patternNBT.group() : "";
					
					DodgyEntity toAdd = null;
					if (patternWords.find()) {
						toAdd = new DodgyEntity(patternWords.group(), nbt);
					}
					if (toAdd != null && patternNumbers.find()) {
						this.requiredEntities.put(toAdd, Integer.valueOf(patternNumbers.group()));
					}
					
				}
				
			}
			
		} else {
			this.requiredLevel = sec.getInt("requiredLevel");
		}
		
		//displayitem
		if (Material.getMaterial(sec.getString("displayItem", "BLUE_STAINED_GLASS_PANE")) == null) this.displayItem = Material.BLUE_STAINED_GLASS_PANE;
		else this.displayItem = Material.getMaterial(sec.getString("displayItem", "BLUE_STAINED_GLASS_PANE"));
		
		this.requiredChallenges = sec.getStringList("requiredChallenges");
		this.rewardText = sec.getString("reward.text");
		this.repeatRewardText = sec.getString("repeatReward.text");
		
		this.hasOffset = (sec.getInt("offset") == -1);
		
		if (this.displayName == null || this.type == null || (this.requiredItems.isEmpty() && this.requiredLevel == null)) {
			return;
		}
		
		challenges.put(name, this);
		if (this.type == Type.islandLevel) {
			levelUpChallenges.add(this);
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public String getDisplayName() {
		return displayName;
	}
	
	public Material getDisplayItem() {
		return displayItem;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getRadius() {
		return radius;
	}
	
	public List<String> getRequiredChallenges() {
		return requiredChallenges;
	}
	
	public Map<Material, Integer> getRequiredItems() {
		return requiredItems;
	}
	
	public Map<DodgyEntity, Integer> getRequiredEntities() {
		return requiredEntities;
	}
	
	public Integer getRequiredLevel() {
		return requiredLevel;
	}
	
	public String getType() {
		return type.name;
	}
	
	public Type returnType() {
		return type;
	}
	
	public static Challenge getChallengeFromID(String id) {
		if (challenges != null && !challenges.isEmpty() && challenges.containsKey(id) && challenges.get(id) != null) {
			
			return challenges.get(id);
			
		}
		
		return null;
	}

	public static Set<String> getChallenges() {	
		return challenges != null && !challenges.isEmpty() ? challenges.keySet() : null;
	}
	
	public static int getSize() {
		return challenges != null && !challenges.isEmpty() ? challenges.size() : 0;
	}

	public ConfigurationSection getSection() {
		return section;
		
	}
	
	public final class getAllResult{
		public final boolean res1;
		public final List<ItemStack> res2;
		public final HashMap<Material, Integer> res3;
		
		public getAllResult(boolean res1, List<ItemStack> res2, HashMap<Material, Integer> res3) {
			this.res1 = res1;
			this.res2 = res2;
			this.res3 = res3;
		}
	}
	
	private getAllResult getAll(PlayerInventory inv, List<Challenge> cs) {
		HashMap<Material, Integer> items = new HashMap<Material, Integer>(requiredItems);
		List<ItemStack> toTake = new ArrayList<ItemStack>();
		 
		
		for (Entry<Material, Integer> ent : requiredItems.entrySet()) {
			if (inv.contains(ent.getKey(), (int) Math.round(ent.getValue()  * Math.pow(1.5, amountCompleted(cs))))) {
				items.remove(ent.getKey());
				toTake.add(new ItemStack(ent.getKey(), (int) Math.round(ent.getValue()  * Math.pow(1.5, amountCompleted(cs)))));
			} else {
				int amountPresent = 0;
				for (ItemStack entry : inv.all(ent.getKey()).values()) {
					amountPresent += entry.getAmount();
				}
				items.put(ent.getKey(), (int) Math.round(ent.getValue()  * Math.pow(1.5, amountCompleted(cs))) - amountPresent);
			}
		}
		if (items.isEmpty()) {
			return new getAllResult(true, toTake, items);
		}
		return new getAllResult(false, toTake, items);
	}
	
	public final class getBlockResult{
		public final boolean res1;
		public final HashMap<Material, Integer> res2;
		
		public getBlockResult(boolean res1, HashMap<Material, Integer> res2) {

			this.res1 = res1;
			this.res2 = res2;
		}
	}
	
	private getBlockResult getBlocks(Location loc, List<Challenge> cs) {  
		HashMap<Material, Integer> blocks = new HashMap<Material, Integer>(requiredItems);
		HashMap<Material, Integer> blocksPresent = new HashMap<Material, Integer>();
		
		for (Entry<Material, Integer> item : blocks.entrySet()) {
			blocks.put(item.getKey(), (int)Math.round(item.getValue() * Math.pow(1.5, amountCompleted(cs))));
		}
		
		for (int x = -radius; x < radius; x++) {
			for (int y = -radius; y < radius; y++) {
				for (int z = -radius; z < radius; z++) {
					Material mat = loc.clone().add(x, y, z).getBlock().getType();
					
					if (blocks.containsKey(mat)) {
						blocksPresent.put(mat, blocksPresent.containsKey(mat) ? blocksPresent.get(mat) + 1 : 1);
					}
				}
			}
		}
		
		for (Entry<Material, Integer> pres : blocksPresent.entrySet()) {
			if ((pres.getKey().getKey().getKey().toLowerCase().contains("door") 
					|| pres.getKey().getKey().getKey().toLowerCase().contains("bed")) && pres.getValue()%2 == 0) {
				pres.setValue(pres.getValue()/2);
			}
			
			if (blocks.get(pres.getKey()) - pres.getValue() > 0) blocks.put(pres.getKey(), blocks.get(pres.getKey()) - pres.getValue());
			else blocks.remove(pres.getKey());
		}
		
		if (!blocks.isEmpty()) return new getBlockResult(false, blocks);
		return new getBlockResult(true, blocks);
	}
	
	public final class getEntityResult{
		public final boolean res1;
		public final HashMap<DodgyEntity, Integer> res2;
		
		public getEntityResult(boolean res1, HashMap<DodgyEntity, Integer> res2) {

			this.res1 = res1;
			this.res2 = res2;
		}
	}
	
	private getEntityResult getEntities(Player player, List<Challenge> cs) {  
		HashMap<DodgyEntity, Integer> entities = new HashMap<DodgyEntity, Integer>(requiredEntities);
		
		for (Entry<DodgyEntity, Integer> item : entities.entrySet()) {
			entities.put(item.getKey(), (int)Math.round(item.getValue() * Math.pow(1.5, amountCompleted(cs))));
		}
		
		for (Entity ent : player.getNearbyEntities(radius, radius, radius)) {
			for (DodgyEntity entry : entities.keySet()) {
				
				if (entry.getType().equals(ent.getType())) {
					if (entities.containsKey(entry)) {
						entities.put(entry, entities.get(entry) - 1);
						if (entities.get(entry) == 0) entities.remove(entry);
					}
				}
				
			}
		}
		
		if (!entities.isEmpty()) return new getEntityResult(false, entities);
		return new getEntityResult(true, entities);
	}
	
	public boolean canCompleteChallenge(Player player, IslandManager im) {
		if (im.getChallenges().contains(this) && !this.isRepeatable()) return false;
		
		if (type == Type.onPlayer) {
			return getAll(player.getInventory(), im.getChallenges()).res1;
		} else if (type == Type.onIsland) {
			
			return requiredEntities.isEmpty() ? getBlocks(player.getLocation(), im.getChallenges()).res1 : getBlocks(player.getLocation(), im.getChallenges()).res1 && getEntities(player, im.getChallenges()).res1;
			
		} else {
			return im.getIsland().getLevel().getLevel() >= requiredLevel;
		}
	}
	
	public String whyNotComplete(Player player, IslandManager im) {
		if (im.getChallenges().contains(this) && !this.isRepeatable())
			return Config.MESSAGES.CHALLENGE_NOTREPEATABLE.value();
		
		if (type == Type.islandLevel) {
			return Config.MESSAGES.CHALLENGE_MISSING_LEVEL.value()
					.replace("$player$", player.getDisplayName())
					.replace("$challenge$", displayName)
					.replace("$level$", String.valueOf(requiredLevel))
					.replace("$island_level$", String.valueOf(SkyBlockAPI.getIslandManager().getIsland(player).getLevel()))
					.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString());
		} else if (type == Type.onIsland) {
			
			String reason = Config.MESSAGES.CHALLENGE_MISSING_BASE.value()
					.replace("$player$", player.getDisplayName())
					.replace("$challenge$", displayName)
					.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString());
			
			for (Entry<Material, Integer> ent : getBlocks(player.getLocation(), im.getChallenges()).res2.entrySet()) {
				reason = reason.concat(Config.MESSAGES.CHALLENGE_MISSING_ITEM.value()
						.replace("$player$", player.getDisplayName())
						.replace("$challenge$", displayName)
						.replace("$amount$", String.valueOf(ent.getValue()))
						.replace("$item$", ent.getKey().getKey().getKey().replaceAll("_", " "))
						.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString()));
			}
			
			if (!requiredEntities.isEmpty()) {
				for (Entry<DodgyEntity, Integer> ent : getEntities(player, im.getChallenges()).res2.entrySet()) {
					reason = reason.concat(Config.MESSAGES.CHALLENGE_MISSING_ITEM.value()
							.replace("$player$", player.getDisplayName())
							.replace("$challenge$", displayName)
							.replace("$amount$", String.valueOf(ent.getValue()))
							.replace("$item$", ent.getKey().getName())
							.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString()));
				}
			}
			
			return reason;
			
		} else if (type == Type.onPlayer) {
			String reason = Config.MESSAGES.CHALLENGE_MISSING_BASE.value()
					.replace("$player$", player.getDisplayName())
					.replace("$challenge$", displayName)
					.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString());
			
			for (Entry<Material, Integer> ent : getAll(player.getInventory(), im.getChallenges()).res3.entrySet()) {
				reason = reason.concat(Config.MESSAGES.CHALLENGE_MISSING_ITEM.value().replace("$player$", player.getDisplayName())
					.replace("$challenge$", displayName)
					.replace("$amount$", String.valueOf(ent.getValue()))
					.replace("$item$", ent.getKey().getKey().getKey().replaceAll("_", " "))
					.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString()));
			}
			
			return reason;
		} else {
			return "&7&oBecause you can bite my shiny metal ass!";
		}
	}
	
	public final class lockedResult {
		public final boolean res1;
		public final String res2;
		public final boolean res3; //Locked Because of 
		
		public lockedResult(boolean res1, String res2, boolean res3) {
			this.res1 = res1;
			this.res2 = res2;
			this.res3 = res3;
		}
	}
	
	public lockedResult getLocked(IslandManager island, OfflinePlayer oPlayer) {
		
		//RANK
		if (island.getRank() < this.rank.rank()) {
			String reason = (Config.OPTIONS.BROADCAST_FORMAT.asString() + Config.MESSAGES.CHALLENGE_LOCKED_RANK.value())
					.replace("$player$", oPlayer.getName())
					.replace("$rank$", rank.getDisplayName())
					.replace("$c_challenge$", displayName)
					.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString());
			
			return new lockedResult(true, reason, false);
		}
		
		//CHALLENGE
		List<Challenge> rC = new ArrayList<Challenge>();
		for (String c : requiredChallenges) {
			
			if (!island.getChallenges().contains(Challenge.getChallengeFromID(c))
					&& Challenge.getChallengeFromID(c) != null) rC.add(Challenge.getChallengeFromID(c));
			
		}
		
		if (rC.size() == 1) { //SINGLE CHALLENGE
			String reason = Config.MESSAGES.CHALLENGE_LOCKED_CHALLENGE.value()
					.replace("$player$", oPlayer.getName())
					.replace("$challenge$", rC.get(0).getDisplayName())
					.replace("$c_challenge$", displayName)
					.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString());
			
			return new lockedResult(true, reason, true);
		
		} else if (rC.size() > 1) { //MULTIPLE CHALLENGES
			
			String cs = "";
			for (Challenge c : rC) {
				if (c == rC.get(0)) {
					cs = c.getDisplayName();
				} else if (c == rC.get(rC.size() - 1)) {
					cs = cs.concat("&r$f$ and " + c.getDisplayName());
				} else {
					cs = cs.concat(", " + c.getDisplayName());
				}
			}
			
			String challenges = cs;
			
			String reason = Config.MESSAGES.CHALLENGE_LOCKED_CHALLENGES.value()
					.replace("$player$", oPlayer.getName())
					.replace("$challenges$", challenges)
					.replace("$c_challenge$", displayName)
					.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString());
			return new lockedResult(true, reason, true);
		}
	
		return new lockedResult(false, null, false);
	}
	
	public void complete(Player player) {
		
		IslandManager im = me.goodandevil.skyblock.api.island.IslandManager.hasIsland(player) ? IslandManager.getByPlayer(player) : null;
		
		if (type == Type.onPlayer) {
			getAllResult result = getAll(player.getInventory(), im.getChallenges());
			if (result.res1) {
				im.checkRank();
				
				if (!Config.OPTIONS.COMPLETE_SOUND.asString().equals("null")) player.playSound(player.getLocation(), Config.OPTIONS.COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
				player.getInventory().removeItem(result.res2.toArray(new ItemStack[result.res2.size()]));
				Bukkit.getPluginManager().callEvent(new ChallengeCompleteEvent(this, player, SkyBlockAPI.getIslandManager().getIsland(player)));
			} else {
				if (!Config.OPTIONS.NO_COMPLETE_SOUND.asString().equals("null")) player.playSound(player.getLocation(), Config.OPTIONS.NO_COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
				player.sendMessage(Messenger.cc(whyNotComplete(player, im)));
			}
		} else if (type == Type.onIsland && SkyBlockAPI.getIslandManager().getIslandPlayerAt(player).equals(SkyBlockAPI.getIslandManager().getIsland(player))) {
			if (getBlocks(player.getLocation(), im.getChallenges()).res1) {
				im.checkRank();
				
				if (!Config.OPTIONS.COMPLETE_SOUND.asString().equals("null")) player.playSound(player.getLocation(), Config.OPTIONS.COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
				Bukkit.getPluginManager().callEvent(new ChallengeCompleteEvent(this, player, SkyBlockAPI.getIslandManager().getIsland(player)));
			} else {
				if (!Config.OPTIONS.NO_COMPLETE_SOUND.asString().equals("null")) player.playSound(player.getLocation(), Config.OPTIONS.NO_COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
				player.sendMessage(Messenger.cc(whyNotComplete(player, im)));
			}
		} else {
			if (canCompleteChallenge(player, im)) {
				im.checkRank();
				
				if (!Config.OPTIONS.COMPLETE_SOUND.asString().equals("null")) player.playSound(player.getLocation(), Config.OPTIONS.COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
				Bukkit.getPluginManager().callEvent(new ChallengeCompleteEvent(this,player, SkyBlockAPI.getIslandManager().getIsland(player)));
			} else {
				if (!Config.OPTIONS.NO_COMPLETE_SOUND.asString().equals("null")) player.playSound(player.getLocation(), Config.OPTIONS.NO_COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
				player.sendMessage(Messenger.cc(whyNotComplete(player, im)));
			}
		}
	}
	
	public void forceComplete(OfflinePlayer player) { 
		if (!Config.OPTIONS.COMPLETE_SOUND.asString().equals("null") && player.isOnline()) player.getPlayer().playSound(player.getPlayer().getLocation(), Config.OPTIONS.COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
		Bukkit.getPluginManager().callEvent(new ChallengeCompleteEvent(this, player, SkyBlockAPI.getIslandManager().getIsland(player)));
	}
	
	public void complete(Island island) {
		Bukkit.getPluginManager().callEvent(new ChallengeCompleteEvent(this, Bukkit.getPlayer(island.getOwnerUUID()), island));
	}
	
	public int amountCompleted(List<Challenge> cs) {
		if (cs.isEmpty()) return 0;
		int amount = 0;
		for (Challenge c : cs) {
			if (this.equals(c)) amount++;
		}
		return amount;
	}

	/**
	 * @return the repeatable
	 */
	public boolean isRepeatable() {
		return repeatable;
	}

	/**
	 * @return the rewardText
	 */
	public String getRewardText() {
		return rewardText;
	}

	/**
	 * @return the repeatRewardText
	 */
	public String getRepeatRewardText() {
		return repeatRewardText;
	}
	
	/**
	 * @return the hasOffset
	 */
	public boolean hasOffset() {
		return hasOffset;
	}

	/**
	 * @return the levelUpChallenges
	 */
	public static List<Challenge> getLevelUpChallenges() {
		return levelUpChallenges;
	}

	public static void purge() {
		challenges.clear();
		levelUpChallenges.clear();
		
		IslandManager.purge();
	}
	
	public enum Type{
		onPlayer("onPlayer"), onIsland("onIsland"), islandLevel("islandLevel");
		
		public final String name;
		private Type(String name) {
			this.name = name;
		}
	}

}
