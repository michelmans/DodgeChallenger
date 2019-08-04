package me.alchemi.dodgechallenger.meta;

import me.alchemi.al.objects.meta.BaseMeta;
import me.alchemi.dodgechallenger.main;
import me.alchemi.dodgechallenger.managers.IslandManager;

public class IslandMeta extends BaseMeta{

	public IslandMeta(IslandManager im) {
		super(main.getInstance(), im);
	}

}
