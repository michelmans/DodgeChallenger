package me.alchemi.dodgechallenger.objects;

import java.util.AbstractMap;
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

import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.objects.Container;
import me.alchemi.al.objects.StringSerializable;
import me.alchemi.dodgechallenger.Config.Messages;
import me.alchemi.dodgechallenger.Config.Options;
import me.alchemi.dodgechallenger.events.ChallengeCompleteEvent;
import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import me.alchemi.dodgechallenger.managers.RankManager;
import me.alchemi.dodgechallenger.objects.placeholder.Stringer;
import com.songoda.skyblock.api.SkyBlockAPI;
import com.songoda.skyblock.api.island.Island;
import com.songoda.skyblock.api.island.IslandManager;

public class Challenge implements StringSerializable {

	private static HashMap<String, Challenge> challenges = new HashMap<String, Challenge>();
	private static List<Challenge> levelUpChallenges = new ArrayList<Challenge>();
	
	private final Rank rank;
	
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
	
	public Challenge(String name, ConfigurationSection sec, Rank rank) {
		
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
		
		//display item
		if (MaterialWrapper.getWrapper(sec.getString("displayItem", "BLUE_STAINED_GLASS_PANE")) == null) this.displayItem = Material.BLUE_STAINED_GLASS_PANE;
		else this.displayItem = MaterialWrapper.getWrapper(sec.getString("displayItem", "BLUE_STAINED_GLASS_PANE"));
		
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
		return "Challenge{" + name + "}";
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
	
	public final class ItemsResult{
		public final boolean allItemsPresent;
		public final ItemStack[] itemsToRemove;
		public final HashMap<Material, Integer> missingItemsAmount;
		
		public ItemsResult(boolean res1, List<ItemStack> res2, HashMap<Material, Integer> res3) {
			this.allItemsPresent = res1;
			this.itemsToRemove = res2.toArray(new ItemStack[res2.size()]);
			this.missingItemsAmount = res3;
		}
	}
	
	private ItemsResult getItemsInventory(PlayerInventory inv, Container<Challenge> cs) {
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
			return new ItemsResult(true, toTake, items);
		}
		return new ItemsResult(false, toTake, items);
	}
	
	public final class BlockResult{
		public final boolean allBlocksPresent;
		public final HashMap<Material, Integer> missingBlocks;
		
		public BlockResult(boolean res1, HashMap<Material, Integer> res2) {

			this.allBlocksPresent = res1;
			this.missingBlocks = res2;
		}
	}
	
	private BlockResult getBlocks(Location loc, Container<Challenge> challenges) {  
		HashMap<Material, Integer> blocks = new HashMap<Material, Integer>(requiredItems);
		HashMap<Material, Integer> blocksPresent = new HashMap<Material, Integer>();
		
		for (Entry<Material, Integer> item : blocks.entrySet()) {
			blocks.put(item.getKey(), (int)Math.round(item.getValue() * Math.pow(1.5, amountCompleted(challenges))));
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
		
		if (!blocks.isEmpty()) return new BlockResult(false, blocks);
		return new BlockResult(true, blocks);
	}
	
	public final class EntityResult{
		public final boolean allEntitiesPresent;
		public final HashMap<DodgyEntity, Integer> missingEntities;
		
		public EntityResult(boolean res1, HashMap<DodgyEntity, Integer> res2) {

			this.allEntitiesPresent = res1;
			this.missingEntities = res2;
		}
	}
	
	private EntityResult getEntities(Player player, Container<Challenge> cs) {  
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
		
		if (!entities.isEmpty()) return new EntityResult(false, entities);
		return new EntityResult(true, entities);
	}
	
	public Entry<Boolean, String> canComplete(Player player){
		
		Entry<Boolean, String> falseEntry = new AbstractMap.SimpleEntry<Boolean, String>(false, "");
		Entry<Boolean, String> trueEntry = new AbstractMap.SimpleEntry<Boolean, String>(true, "");
		
		
		DodgeIsland island = DodgeIslandManager.getManager().getByPlayer(player);
		
		if (island.getChallenges().contains(this) && !this.isRepeatable()) {
			falseEntry.setValue(Messenger.formatString(Messages.CHALLENGE_NOTREPEATABLE.value()));
			return falseEntry;
		}
		
		String reason = new Stringer(Messages.CHALLENGE_MISSING_BASE)
				.player(player)
				.challenge(this)
				.create();
		
		switch(type) {
		case islandLevel:
			
			Island fabledIsland = SkyBlockAPI.getIslandManager().getIslandByUUID(island.getIsland());
			
			if (fabledIsland.getLevel().getLevel() >= requiredLevel
					|| fabledIsland.getLevel().getLevel() >= requiredLevel) {
				return trueEntry;
				
			} else {
				falseEntry.setValue(new Stringer(Messages.CHALLENGE_MISSING_LEVEL)
						.player(player)
						.challenge(this)
						.level(requiredLevel)
						.island_level(fabledIsland.getLevel().getLevel())
						.parse(player)
						.create());
				return falseEntry;
			}
			
		case onIsland:
			
			BlockResult br = getBlocks(player.getLocation(), island.getChallenges());
			
			EntityResult er = getEntities(player, island.getChallenges());
			
			if ((requiredEntities.isEmpty() || er.allEntitiesPresent)
					&& (requiredItems.isEmpty() || br.allBlocksPresent)) {
				return trueEntry;
			}
			
			for (Entry<Material, Integer> ent : br.missingBlocks.entrySet()) {
				reason = reason.concat(new Stringer(Messages.CHALLENGE_MISSING_ITEM)
						.player(player)
						.challenge(this)
						.amount(ent.getValue())
						.item(new ItemStack(ent.getKey()))
						
						.create());
			}
			
			for (Entry<DodgyEntity, Integer> ent : er.missingEntities.entrySet()) {
				reason = reason.concat(new Stringer(Messages.CHALLENGE_MISSING_ITEM)
						.player(player)
						.challenge(this)
						.amount(ent.getValue())
						.item(ent.getKey().getName())
						
						.create());
			}
			falseEntry.setValue(reason);
			return falseEntry;
			
		case onPlayer:
			
			ItemsResult ir = getItemsInventory(player.getInventory(), island.getChallenges());
			
			if (ir.allItemsPresent) {
				return trueEntry;
			}
			
			for (Entry<Material, Integer> ent : ir.missingItemsAmount.entrySet()) {
				reason = reason.concat(new Stringer(Messages.CHALLENGE_MISSING_ITEM)
						.player(player)
						.challenge(this)
						.amount(ent.getValue())
						.item(new ItemStack(ent.getKey()))
						
						.create());
			}
			falseEntry.setValue(reason);
			return falseEntry;
			
		default:
			
			return new AbstractMap.SimpleEntry<Boolean, String>(false, "&7&oBecause you can bite my shiny metal ass!");
		
		}
		
	}
	
	public final class lockedResult {
		public final boolean isLocked;
		public final String lockReason;
		public final boolean res3; //Locked Because of 
		
		public lockedResult(boolean res1, String res2, boolean res3) {
			this.isLocked = res1;
			this.lockReason = res2;
			this.res3 = res3;
		}
	}
	
	public lockedResult getLocked(DodgeIsland island, OfflinePlayer oPlayer) {
		
		//RANK
		if (island.getRank().getId() < this.rank.getId()) {
			return new lockedResult(true, Options.BROADCAST_FORMAT.asString() + new Stringer(Messages.CHALLENGE_LOCKED_RANK)
					.player(oPlayer.getName())
					.rank(rank)
					.required_rank(RankManager.getManager().getPreviousRank(rank))
					.challenge(this)
					.create(), false);
		}
		
		//CHALLENGE
		List<Challenge> requiredChallengesList = new ArrayList<Challenge>();
		for (String challenge : requiredChallenges) {
			
			if (!island.getChallenges().contains(Challenge.getChallengeFromID(challenge))
					&& Challenge.getChallengeFromID(challenge) != null) requiredChallengesList.add(Challenge.getChallengeFromID(challenge));
			
		}
		
		if (requiredChallengesList.size() == 1) { //SINGLE CHALLENGE
			return new lockedResult(true, new Stringer(Messages.CHALLENGE_LOCKED_SINGLE)
					.player(oPlayer.getName())
					.challenge(this)
					.required_challenge(requiredChallengesList.get(0))
					.create(), true);
		
		} else if (requiredChallengesList.size() > 1) { //MULTIPLE CHALLENGES
			
			String challenges = "";
			for (Challenge challenge : requiredChallengesList) {
				if (challenge == requiredChallengesList.get(0)) {
					challenges = challenge.getDisplayName();
				} else if (challenge == requiredChallengesList.get(requiredChallengesList.size() - 1)) {
					challenges = challenges.concat("&r%f% and " + challenge.getDisplayName());
				} else {
					challenges = challenges.concat(", " + challenge.getDisplayName());
				}
			}
			
			Stringer string = new Stringer(Messages.CHALLENGE_LOCKED_MULTIPLE)
					.player(oPlayer.getName())
					.challenge(this)
					.required_challenges(challenges);
			
			if (oPlayer.isOnline()) string.parse(oPlayer.getPlayer());
			else string.parse(oPlayer);
			
			return new lockedResult(true, string.create(), true);
		}
	
		return new lockedResult(false, null, false);
	}
	
	public void complete(Player player) {
		
		DodgeIsland island = IslandManager.hasIsland(player) ? DodgeIslandManager.getManager().getByPlayer(player) : null;
		
		Entry<Boolean, String> canComplete = canComplete(player);
		
		if (type == Type.onPlayer) {
			
			ItemStack[] result = getItemsInventory(player.getInventory(), island.getChallenges()).itemsToRemove;

			if (canComplete.getKey()) {
				
				island.checkRank();
				
				if (!Options.COMPLETE_SOUND.asString().equals("null"))
					player.playSound(player.getLocation(), Options.COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
				Bukkit.getPluginManager().callEvent(
						new ChallengeCompleteEvent(this, player, result));
				
			} else {
				
				if (!Options.NO_COMPLETE_SOUND.asString().equals("null"))
					player.playSound(player.getLocation(), Options.NO_COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
				player.sendMessage(canComplete.getValue());
				
			}
		} else if (type == Type.onIsland 
				&& SkyBlockAPI.getIslandManager().getIslandPlayerAt(player).getOwnerUUID().equals(SkyBlockAPI.getIslandManager().getIsland(player).getOwnerUUID())) {
			
			if (canComplete.getKey()) {
				
				if (!Options.COMPLETE_SOUND.asString().equals("null"))
					player.playSound(player.getLocation(), Options.COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
				Bukkit.getPluginManager().callEvent(
						new ChallengeCompleteEvent(this, player, null));
				
			} else {
				
				if (!Options.NO_COMPLETE_SOUND.asString().equals("null"))
					player.playSound(player.getLocation(), Options.NO_COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
				player.sendMessage(canComplete.getValue());
				
			}
			
		} else if (type == Type.islandLevel) {
			if (canComplete.getKey()) {
				
				if (!Options.COMPLETE_SOUND.asString().equals("null"))
					player.playSound(player.getLocation(), Options.COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
				Bukkit.getPluginManager().callEvent(
						new ChallengeCompleteEvent(this,player, null));

			} else {
				
				if (!Options.NO_COMPLETE_SOUND.asString().equals("null"))
					player.playSound(player.getLocation(), Options.NO_COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
				player.sendMessage(canComplete.getValue());
				
			}
		}
	}
	
	public void forceComplete(OfflinePlayer player) { 
		if (!Options.COMPLETE_SOUND.asString().equals("null")
				&& player.isOnline()) 
			player.getPlayer().playSound(player.getPlayer().getLocation(), Options.COMPLETE_SOUND.asSound(), 1.0F, 1.0F);
		
		Bukkit.getPluginManager().callEvent(
				new ChallengeCompleteEvent(this, player, null));
	}
	
	public void complete(Island island) {
		Bukkit.getPluginManager().callEvent(
				new ChallengeCompleteEvent(this, Bukkit.getPlayer(island.getOwnerUUID()), null));
	}
	
	public int amountCompleted(Container<Challenge> challenges) {
		return challenges.contains(this) ? challenges.getAmount(this) : 0;
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
		
		DodgeIslandManager.getManager().purge();
	}
	
	public enum Type{
		onPlayer("onPlayer"), onIsland("onIsland"), islandLevel("islandLevel");
		
		public final String name;
		private Type(String name) {
			this.name = name;
		}
	}
	
	@Override
	public String serialize_string() {
		return Challenge.class.getName() + "{" + this.name + "}";
	}
	
	public static Challenge deserialize_string(String deserialized) {
		return getChallengeFromID(deserialized.replace(Challenge.class.getName() + "{", "").replace("}", ""));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Challenge) {
			Challenge c = (Challenge) obj;
			return c.name.equals(name);
		}
		return false;
	}

}
