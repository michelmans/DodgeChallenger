package com.alchemi.dodgechallenger.meta;

import org.bukkit.plugin.Plugin;

import com.alchemi.al.objects.meta.BaseMeta;

public class GUIPageMeta extends BaseMeta {

	public static final String NAME = "guiPage";
	
	public GUIPageMeta(Plugin owningPlugin, int guiPage) {
		super(owningPlugin, guiPage);
	}

	@Override
	public void invalidate() {}

	@Override
	public String name() {
		return NAME;
	}
}
