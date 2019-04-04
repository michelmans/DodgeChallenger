package com.alchemi.dodgechallenger.meta;

import com.alchemi.al.objects.meta.BaseMeta;
import com.alchemi.dodgechallenger.main;
import com.alchemi.dodgechallenger.managers.IslandManager;

public class IslandMeta extends BaseMeta{

	public IslandMeta(IslandManager im) {
		super(main.instance, im);
	}

}
