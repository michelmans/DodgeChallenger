package com.alchemi.dodgechallenger.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.al.objects.SexyRunnable;
import com.alchemi.dodgechallenger.Config;
import com.alchemi.dodgechallenger.main;
import com.alchemi.dodgechallenger.managers.IslandManager;
import com.alchemi.dodgechallenger.managers.RankManager;
import com.alchemi.dodgechallenger.objects.Challenge;
import com.alchemi.dodgechallenger.objects.Challenge.Type;
import com.alchemi.dodgechallenger.objects.Challenge.lockedResult;
import com.alchemi.dodgechallenger.objects.DodgyEntity;
import com.alchemi.dodgechallenger.objects.ItemFactory;

public class ChallengeGui extends GuiBase {

	public ChallengeGui(main plugin, String name, int size) {
		super(plugin, Messenger.cc(name), size);
		plugin.guiListener.registerGui(this);
	}
	
	void setContents(Player pl) {
		
		IslandManager im = IslandManager.getByPlayer(pl);
		im.checkRank();
		
		int i = 0;
		for (RankManager rank : RankManager.getRanks()) {
			if (im.getRank() >= rank.rank()) contents.put(i, new ItemFactory(rank.getDisplayMaterial()).setName(Messenger.cc(rank.getDisplayName())));
			else {
				IslandManager.RankRequired rr = im.challengeNeeded(rank);
				
				List<String> lore = new ArrayList<String>();
				
				//CHALLENGE
				if (rr.getChallenges().size() == 1) { //SINGLE CHALLENGE
					
					lore.add(Config.MESSAGES.CHALLENGE_LOCKED_CHALLENGE.value().replace("$player$", pl.getDisplayName())
							.replace("$challenge$", rr.getChallenges().get(0).getDisplayName())
							.replace("$c_challenge$", rank.getDisplayName())
							.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString()));

									
				} else if (rr.getChallenges().size() > 1) { //MULTIPLE CHALLENGES
					
					String cs = "";
					for (Challenge c : rr.getChallenges()) {
						if (c == rr.getChallenges().get(0)) {
							cs = c.getDisplayName();
						} else if (c == rr.getChallenges().get(rr.getChallenges().size() - 1)) {
							cs = cs.concat("&r$f$ and " + c.getDisplayName());
						} else {
							cs = cs.concat(", " + c.getDisplayName());
						}
					}
					
					String challenges = cs;
					
					lore.add(Config.MESSAGES.CHALLENGE_LOCKED_CHALLENGES.value().replace("$player$", pl.getDisplayName())
							.replace("$challenges$", challenges)
							.replace("$c_challenge$", rank.getDisplayName())
							.replace("$f$", Config.OPTIONS.BROADCAST_FORMAT.asString()));
				}
				
				lore.add("You need to complete " + rr.getAmount() + " challenges in the previous rank.");
				contents.put(i, new ItemFactory(rank.getDisplayMaterial()).setName(Messenger.cc(rank.getDisplayName())).setLore(lore));
			}
			i++;
			
			for (Challenge c : rank.getChallenges()) {
				List<String> lore = new ArrayList<String>();
				lockedResult lr = c.getLocked(im, pl);
								
				if (lr.res1) {
					if (c.hasOffset()) continue;
					
					if (Config.OPTIONS.SHOW_LOCKED_CHALLENGE_NAME.asBoolean()) lore.add(Messenger.cc("&o" + c.getDisplayName()));  
					lore.add(Messenger.cc(lr.res2.replaceAll("\\$f\\$", Config.OPTIONS.BROADCAST_FORMAT.asString())));
					contents.put(i, new ItemFactory(Config.OPTIONS.LOCKED_DISPLAY_ITEM.asMaterial())
							.setName(Messenger.cc(Config.MESSAGES.CHALLENGE_LOCKED_LOCKED.value()))
							.setLore(lore));
					
					
				} else {
					if (c.hasOffset()) i--;
					
					if (c.getDescription() != null) lore.add(Messenger.cc(c.getDescription()));
					
					if (im.getChallenges().contains(c)) {
						if (c.isRepeatable()) {
							double amountModifier = Math.pow(1.5, c.amountCompleted(im.getChallenges()));
							
							if (c.returnType() != Type.islandLevel) {
								lore.add("");
								lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_REQUIRES.value()));
							}
							
							for (Entry<Material, Integer> ent : c.getRequiredItems().entrySet()) {
								lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_ITEM.value()).replace("$item$", ent.getKey().getKey().getKey().replaceAll("_", ""))
										.replace("$amount$", String.valueOf(Math.round((ent.getValue() * amountModifier)))));
							}
							
							if (!c.getRequiredEntities().isEmpty()) {
								for (Entry<DodgyEntity, Integer> ent : c.getRequiredEntities().entrySet()) {
									
									String string = Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_ITEM.value());
									
									if (!ent.getKey().hasColour()) string = string.replace("$item$", ent.getKey().getName().replaceAll("_", ""));
									else string = string.replace("$item$", ent.getKey().getColour().name().toLowerCase() + " " + ent.getKey().getName().toLowerCase().replaceAll("_", ""));
									string = string.replace("$amount$", String.valueOf(Math.round((ent.getValue() * amountModifier))));
									
									lore.add(string);
								}
							}
							
							lore.add("");
							if (c.returnType() == Challenge.Type.onPlayer) lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_ONPLAYER.value()));
							else if (c.returnType() == Challenge.Type.onIsland) lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_ONISLAND.value()).replace("$amount$", String.valueOf(c.getRadius())));
							lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_REWARD_BASE.value()));
							for (String s : c.getRepeatRewardText().split("\\|")) {
								lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_REWARD_TEXT.value()).replace("$text$", s.replaceAll("\\$currency\\$", main.eco.currencyNameSingular())));
							}
							
							if (c.canCompleteChallenge(pl, im)) lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_CANCOMPLETE.value()));
							else lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_CANNOTCOMPLETE.value()));
							
							lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_COMPLETED.value().replaceAll("\\$amount\\$", String.valueOf(c.amountCompleted(im.getChallenges())))));
							
						} else {
							
							lore.add("");
							lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_NOTREPEATABLE.value()));
							
						}
						
						contents.put(i, new ItemFactory(c.getDisplayItem())
								.setName(Messenger.cc(c.getDisplayName()))
								.setLore(lore)
								.addEnch(Enchantment.BINDING_CURSE, false));
					} else {
						if (c.returnType() != Type.islandLevel) {
							lore.add("");
							lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_REQUIRES.value()));
						}
						for (Entry<Material, Integer> ent : c.getRequiredItems().entrySet()) {
							lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_ITEM.value()).replace("$item$", ent.getKey().getKey().getKey().replaceAll("_", ""))
									.replace("$amount$", String.valueOf(ent.getValue())));
						}
						
						if (!c.getRequiredEntities().isEmpty()) {
							for (Entry<DodgyEntity, Integer> ent : c.getRequiredEntities().entrySet()) {
								
								String string = Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_ITEM.value());
								
								if (!ent.getKey().hasColour()) string = string.replace("$item$", ent.getKey().getName().replaceAll("_", ""));
								else string = string.replace("$item$", ent.getKey().getColour().name().toLowerCase() + " " + ent.getKey().getName().toLowerCase().replaceAll("_", ""));
								string = string.replace("$amount$", String.valueOf(ent.getValue()));
								
								lore.add(string);
							}
						}
						
						lore.add("");
						if (c.returnType() == Challenge.Type.onPlayer) lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_ONPLAYER.value()));
						else if (c.returnType() == Challenge.Type.onIsland) lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_ONISLAND.value()).replace("$amount$", String.valueOf(c.getRadius())));
						
						lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_REWARD_BASE.value()));
						for (String s : c.getRewardText().split("\\|")) {
							lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_LORE_REWARD_TEXT.value()).replace("$text$", s.replaceAll("\\$currency\\$", main.eco.currencyNameSingular())));
						}
						if (c.canCompleteChallenge(pl, im)) {
							lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_CANCOMPLETE.value()));
						} else {
							lore.add(Messenger.cc(Config.MESSAGES.CHALLENGE_CANNOTCOMPLETE.value()));
						}
						
						contents.put(i, new ItemFactory(c.getDisplayItem())
								.setName(Messenger.cc(c.getDisplayName()))
								.setLore(lore));
					}
				
				}
				
				i++;
			}
		}
		
	}
	
	void setCommands(Player pl) {
		IslandManager im = IslandManager.getByPlayer(pl);
		int i = 0;
		for (RankManager rank : RankManager.getRanks()) {
			i++;
			for (Challenge c : rank.getChallenges()) {
				
				lockedResult lr = c.getLocked(im, pl);
				if (lr.res1) {
					
					if (c.hasOffset()) continue;
					
					commands.put(contents.get(i), new SexyRunnable() {
						
						@Override
						public void run(Object... args) {
							
							//Player
							((CommandSender) args[0]).sendMessage(Messenger.cc(lr.res2));
							
						}
					});
					arguments.put(contents.get(i), new Object[] {pl});
					
				} else {
					if (c.hasOffset()) i--;
						
					commands.put(contents.get(i), complete);
					arguments.put(contents.get(i), new Object[] {c, pl});
					
				}
				
				i++;
			}
		}
	}
	
	@Override
	public void openGUI(Player pl) {
		setContents(pl);
		setCommands(pl);
		super.openGUI(pl);
	}
	
	@Override
	public void openGUI(CommandSender sender, Player player) {
		setContents(player);
		super.openGUI(sender, player);
	}

	@Override
	void setContents() {}

	@Override
	void setCommands() {}

	SexyRunnable complete = new SexyRunnable() {
		
		@Override
		public void run(Object... args) {
			//Challenge, Player
			
			((Challenge)args[0]).complete((Player) args[1]);
			setContents((Player) args[1]);
			setCommands((Player) args[1]);
			for (int slot = 0; slot < guiSize; slot++) {
				if (contents.containsKey(slot)) getGui().setItem(slot, contents.get(slot));
				
			}
			
		}
	};

}
