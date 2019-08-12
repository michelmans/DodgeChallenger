package me.alchemi.dodgechallenger.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.objects.GUI.GUIBase;
import me.alchemi.al.objects.GUI.GUIListener;
import me.alchemi.al.objects.handling.ItemFactory;
import me.alchemi.al.objects.handling.SexyRunnable;
import me.alchemi.dodgechallenger.Config;
import me.alchemi.dodgechallenger.Config.Messages;
import me.alchemi.dodgechallenger.Config.Options;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.managers.DodgeIslandManager;
import me.alchemi.dodgechallenger.managers.RankManager;
import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.Challenge.Type;
import me.alchemi.dodgechallenger.objects.Challenge.lockedResult;
import me.alchemi.dodgechallenger.objects.DodgeIsland;
import me.alchemi.dodgechallenger.objects.DodgyEntity;
import me.alchemi.dodgechallenger.objects.Rank;
import me.alchemi.dodgechallenger.objects.placeholder.Stringer;
import me.goodandevil.skyblock.api.island.IslandManager;

public class ChallengeGui extends GUIBase {
	
	private DodgeIsland island = null;
	
	private static Map<Integer, ItemFactory> staticContents = new HashMap<Integer, ItemFactory>();
	
	public ChallengeGui(Player player) {
		super(Dodge.getInstance(), Messenger.formatString("&2&oChallenges"), 54, player, player);
		new GUIListener(Dodge.getInstance(), this);
		
		island = DodgeIslandManager.getManager().getByPlayer(player);
		
		setContents();		
		setCommands();
		
		openGUI();
		
	}
	
	public ChallengeGui(OfflinePlayer player, CommandSender sender) {
		super(Dodge.getInstance(), Messenger.formatString("&2&oChallenges"), 54, player, (Player) sender);
		new GUIListener(Dodge.getInstance(), this);
		
		if (player.isOnline()) island = DodgeIslandManager.getManager().getByPlayer(player.getPlayer());
		else island = IslandManager.hasIsland(player) ? new DodgeIsland(DodgeIslandManager.getIslandUUID(player)) : null;
		
		setContents();
		
		openGUI();
		
	}
	
	public void buildRank(Rank rank, int i) {
		if (island.getRank().getId() >= rank.getId()) 
			contents.put(i, new ItemFactory(rank.getDisplayMaterial()).setName(Messenger.formatString(rank.getDisplayName())));
		else {
			DodgeIsland.RankRequired rr = island.challengeNeeded(rank);
			
			List<String> lore = new ArrayList<String>();
			
			//CHALLENGE
			if (rr.getChallenges().size() == 1) { //SINGLE CHALLENGE
				
				Stringer string = new Stringer(Messages.CHALLENGE_LOCKED_SINGLE)
						.player(owningPlayer.getName())
						.required_challenge(rr.getChallenges().get(0))
						.rank(rank)
						;
				
				if (owningPlayer.isOnline()) lore.add(string.parse(owningPlayer.getPlayer()).create());
				
				else lore.add(string.parse(owningPlayer).create());
				
			} else if (rr.getChallenges().size() > 1) { //MULTIPLE CHALLENGES
				
				String challenges = "";
				for (Challenge c : rr.getChallenges()) {
					if (c == rr.getChallenges().get(0)) {
						challenges = c.getDisplayName();
					} else if (c == rr.getChallenges().get(rr.getChallenges().size() - 1)) {
						challenges = challenges.concat("&r%f% and " + c.getDisplayName());
					} else {
						challenges = challenges.concat(", " + c.getDisplayName());
					}
				}
				
				Stringer string = new Stringer(Messages.CHALLENGE_LOCKED_MULTIPLE)
						.player(owningPlayer.getName())
						.required_challenges(challenges)
						.rank(rank)
						;
				
				if (owningPlayer.isOnline()) lore.add(string.parse(owningPlayer.getPlayer()).create());
				
				else lore.add(string.parse(owningPlayer).create());
			}
			
			lore.add(new Stringer(Messages.CHALLENGE_LOCKED_AMOUNT)
					.amount(rr.getAmount())
					.create());
			contents.put(i, new ItemFactory(rank.getDisplayMaterial()).setName(Messenger.formatString(rank.getDisplayName())).setLore(lore));
		}
	}
	
	public void buildChallengeCompleted(Challenge challenge, int i, List<String> lore) {
		
		if (challenge.isRepeatable()) {
			double amountModifier = Math.pow(1.5, challenge.amountCompleted(island.getChallenges()));
			
			if (challenge.returnType() != Type.islandLevel) {
				lore.add("");
				lore.add(Messenger.formatString(Messages.CHALLENGE_LORE_REQUIRES.value()));
			}

			if (!challenge.getRequiredItems().isEmpty()) {
				for (Entry<Material, Integer> ent : challenge.getRequiredItems().entrySet()) {
					lore.add(new Stringer(Messages.CHALLENGE_LORE_ITEM)
							.item(new ItemStack(ent.getKey()))
							.amount(Math.round((float)(ent.getValue() * amountModifier)))
							.create());
				}
			}
			
			if (!challenge.getRequiredEntities().isEmpty()) {
				for (Entry<DodgyEntity, Integer> ent : challenge.getRequiredEntities().entrySet()) {
					
					Stringer string = new Stringer(Messages.CHALLENGE_LORE_ITEM).amount(Math.round((float)(ent.getValue() * amountModifier)));
					
					if (!ent.getKey().hasColour()) string.item(ent.getKey().getName());
					else string.item(ent.getKey().getColour().name().toLowerCase() + " " + ent.getKey().getName());
					
					lore.add(string.create());
				}
			}
			
			lore.add("");
			
			if (challenge.returnType() == Challenge.Type.onPlayer) lore.add(Messenger.formatString(Messages.CHALLENGE_LORE_ONPLAYER.value()));
			else if (challenge.returnType() == Challenge.Type.onIsland) lore.add(new Stringer(Messages.CHALLENGE_LORE_ONISLAND)
					.amount(challenge.getRadius())
					.parse(owningPlayer)
					.create());
				
			lore.add(Messenger.formatString(Messages.CHALLENGE_LORE_REWARD_BASE.value()));
			
			for (String rewardLine : challenge.getRepeatRewardText().split("\\|")) {
				lore.add(new Stringer(Messages.CHALLENGE_LORE_REWARD_TEXT)
						.text(new Stringer(rewardLine)
								.currency(Dodge.eco.currencyNameSingular())
								.create())
						.create());
				
			}
			
			if (owningPlayer.isOnline() 
					&& sender != owningPlayer.getPlayer() 
					&& challenge.canComplete(owningPlayer.getPlayer()).getKey()) 
				lore.add(Messenger.formatString(Messages.CHALLENGE_CANCOMPLETE.value()));
			
			else if (owningPlayer.isOnline() 
					&& sender != owningPlayer.getPlayer()) 
				lore.add(Messenger.formatString(Messages.CHALLENGE_CANNOTCOMPLETE.value()));
			
			lore.add(new Stringer(Messages.CHALLENGE_COMPLETED)
					.amount(challenge.amountCompleted(island.getChallenges()))
					.create());
		} else {
			
			lore.add("");
			lore.add(Messenger.formatString(Messages.CHALLENGE_NOTREPEATABLE.value()));
			
		}
		
		contents.put(i, new ItemFactory(challenge.getDisplayItem())
				.setName(Messenger.formatString(challenge.getDisplayName()))
				.setLore(lore)
				.addEnch(Enchantment.BINDING_CURSE, false));
		
	}
	
	public void buildChallenge(Challenge challenge, int i, List<String> lore) {
		
		if (challenge.returnType() != Type.islandLevel) {
			lore.add("");
			lore.add(Messenger.formatString(Messages.CHALLENGE_LORE_REQUIRES.value()));
		}
		
		if (!challenge.getRequiredItems().isEmpty()) {
			for (Entry<Material, Integer> ent : challenge.getRequiredItems().entrySet()) {
				lore.add(new Stringer(Messages.CHALLENGE_LORE_ITEM)
						.item(new ItemStack(ent.getKey()))
						.amount(ent.getValue())
						.create());
			}
		}
		
		if (!challenge.getRequiredEntities().isEmpty()) {
			for (Entry<DodgyEntity, Integer> ent : challenge.getRequiredEntities().entrySet()) {
				
				Stringer string = new Stringer(Messages.CHALLENGE_LORE_ITEM).amount(ent.getValue());
				
				if (!ent.getKey().hasColour()) string.item(ent.getKey().getName());
				else string.item(ent.getKey().getColour().name().toLowerCase() + " " + ent.getKey().getName());
				
				lore.add(string.create());
			}
		}
		
		lore.add("");
		
		if (challenge.returnType() == Challenge.Type.onPlayer) lore.add(Messenger.formatString(Messages.CHALLENGE_LORE_ONPLAYER.value()));
		else if (challenge.returnType() == Challenge.Type.onIsland) lore.add(new Stringer(Messages.CHALLENGE_LORE_ONISLAND)
				.amount(challenge.getRadius())
				.parse(owningPlayer)
				.create());
			
		lore.add(Messenger.formatString(Messages.CHALLENGE_LORE_REWARD_BASE.value()));
		
		for (String rewardLine : challenge.getRewardText().split("\\|")) {
			lore.add(new Stringer(Messages.CHALLENGE_LORE_REWARD_TEXT)
					.text(new Stringer(rewardLine)
							.currency(Dodge.eco.currencyNameSingular())
							.create())
					.create());

		}
		
		if (owningPlayer.isOnline() 
				&& sender != owningPlayer.getPlayer() 
				&& challenge.canComplete(owningPlayer.getPlayer()).getKey()) 
			lore.add(Messenger.formatString(Messages.CHALLENGE_CANCOMPLETE.value()));
		
		else if (owningPlayer.isOnline() 
				&& sender != owningPlayer.getPlayer()) 
			lore.add(Messenger.formatString(Messages.CHALLENGE_CANNOTCOMPLETE.value()));
				
		contents.put(i, new ItemFactory(challenge.getDisplayItem())
				.setName(Messenger.formatString(challenge.getDisplayName()))
				.setLore(lore));
	}
	
	@Override
	public void setContents() {
		
		if (island == null) return;
		
		int i = 0;
		
		for (Rank rank : RankManager.getManager().getRanks()) {
			
			buildRank(rank, i);
			
			i++;
			
			for (Challenge challenge : rank.getChallenges()) {
				
				lockedResult lr = challenge.getLocked(island, owningPlayer);

				if (lr.isLocked) {
					
					if (challenge.hasOffset()) continue;
				
					List<String> lore = new ArrayList<String>();
					
					if (Config.Options.SHOW_LOCKED_CHALLENGE_NAME.asBoolean()) lore.add(Messenger.formatString("&o" + challenge.getDisplayName()));  

					lore.add(lr.lockReason);
					contents.put(i, new ItemFactory(Options.LOCKED_DISPLAY_ITEM.asMaterial())
							.setName(Messenger.formatString(Messages.CHALLENGE_LOCKED_NAME.value()))
							.setLore(lore));					
					
				} else {
					
					if (challenge.hasOffset()) i--;
					
					List<String> lore = new ArrayList<String>();
					
					if (challenge.getDescription() != null) lore.add(Messenger.formatString(challenge.getDescription()));
					
					if (island.getChallenges().contains(challenge)) buildChallengeCompleted(challenge, i, lore);
					
					else if (!island.getChallenges().contains(challenge)) buildChallenge(challenge, i, lore);
				
				}
				
				i++;
			}
		}
		
	}
	
	@Override
	public void setCommands() {
		if (island == null) return;
		
		int i = 0;
		for (Rank rank : RankManager.getManager().getRanks()) {
			i++;
			for (Challenge challenge : rank.getChallenges()) {
				
				lockedResult lr = challenge.getLocked(island, owningPlayer);
				if (lr.isLocked) {
					
					if (challenge.hasOffset()) continue;
					
					commands.put(i, new SexyRunnable() {
						
						@Override
						public void run(Object... args) {
							
							//Player
							((CommandSender) args[0]).sendMessage(Messenger.formatString(lr.lockReason));
							
						}
					});
					arguments.put(i, new Object[] {owningPlayer.getPlayer()});
					
				} else {
					if (challenge.hasOffset()) i--;
						
					commands.put(i, complete);
					arguments.put(i, new Object[] {challenge, owningPlayer.getPlayer()});
					
				}
				i++;
			}
		}
	}

//	public static void setupGui() {
//		int i = 0;
//		for (Rank rank : RankManager.getManager().getRanks()) {
//
//			buildRank(rank, i);
//
//			i++;
//
//			for (Challenge challenge : rank.getChallenges()) {
//
//				if (challenge.hasOffset()) i--;
//
//				List<String> lore = new ArrayList<String>();
//
//				if (challenge.getDescription() != null) lore.add(Messenger.formatString(challenge.getDescription()));
//
//				buildChallenge(challenge, i, lore);
//
//				i++;
//			}
//		}
//	}
	
	public static Map<Integer, ItemFactory> getStaticContents() {
		return staticContents;
	}

	public static void setStaticContents(Map<Integer, ItemFactory> staticContents) {
		ChallengeGui.staticContents = staticContents;
	}

	SexyRunnable complete = new SexyRunnable() {
		
		@Override
		public void run(Object... args) {
			//Challenge, Player 
			
			((Challenge)args[0]).complete((Player) args[1]);
			setContents();
			setCommands();
			for (int slot = 0; slot < guiSize; slot++) {
				if (contents.containsKey(slot)) getGui().setItem(slot, contents.get(slot));
				
			}
			
		}
	};

	@Override
	public void onClose() {
		// TODO Auto-generated method stub
		
	}

	
}
