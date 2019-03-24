package com.alchemi.dodgechallenger.meta;

import org.bukkit.plugin.Plugin;

import com.alchemi.al.objects.meta.BaseMeta;

public class PrefixMeta extends BaseMeta {

	public static final String NAME = "defaultPrefix";
	
	public PrefixMeta(Plugin owningPlugin, String prefix) {
		super(owningPlugin, prefix);
	}
	
	@Override
	public void invalidate() {}
	
	@Override
	public String name() {
		
		return NAME;
	}
}
