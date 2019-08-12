package me.alchemi.dodgechallenger.meta;

import me.alchemi.al.objects.meta.BaseMeta;
import me.alchemi.dodgechallenger.Dodge;
import me.alchemi.dodgechallenger.objects.DodgeIsland;

public class IslandMeta extends BaseMeta{

	public IslandMeta(DodgeIsland im) {
		super(Dodge.getInstance(), im.getIsland().toString());
	}

}
