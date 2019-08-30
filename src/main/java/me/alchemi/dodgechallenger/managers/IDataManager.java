package me.alchemi.dodgechallenger.managers;

import java.util.UUID;

import me.alchemi.al.objects.Callback;
import me.alchemi.dodgechallenger.objects.Challenge;
import me.alchemi.dodgechallenger.objects.Container;
import me.alchemi.dodgechallenger.objects.DodgeIsland;

public interface IDataManager {

	public void newIsland(UUID island);
	
	public void removeIsland(UUID island);
	
	public void completeChallenge(UUID island, Challenge challenge);
	
	public void setChallenges(UUID island, Container<Challenge> challenges);
	
	public void setRank(UUID island, int newRank);
	
	public void saveIsland(DodgeIsland island);
	
	public int getRank(UUID island);
	
	public void getRankAsync(UUID island, Callback<Integer> callback);
	
	public Container<Challenge> getCompletedChallenges(UUID island);

	public void getCompletedChallengesAsync(UUID island, Callback<Container<Challenge>> callback);
	
}
