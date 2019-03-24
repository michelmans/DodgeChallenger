package com.alchemi.dodgechallenger.gui;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.alchemi.al.Library;
import com.alchemi.al.objects.SexyRunnable;
import com.alchemi.dodgechallenger.Config;
import com.alchemi.dodgechallenger.main;
import com.alchemi.dodgechallenger.meta.GUIPageMeta;
import com.alchemi.dodgechallenger.objects.ItemFactory;

public abstract class GuiBase {
	
	protected String guiName = "";
	protected int guiSize = 0;
	protected static main plugin;
	private Inventory gui;
	
	protected HashMap<Integer, ItemStack> contents = new HashMap<Integer, ItemStack>();
	protected HashMap<ItemStack, SexyRunnable> commands = new HashMap<ItemStack, SexyRunnable>();
	protected HashMap<ItemStack, Object[]> arguments = new HashMap<ItemStack, Object[]>();
	
	private ItemStack nextPage = new ItemFactory(Config.OPTIONS.NEXT_PAGE_MATERIAL.asMaterial()).setName(Config.MESSAGES.GUI_NEXTPAGE.value());
	private ItemStack prevPage = new ItemFactory(Config.OPTIONS.PREV_PAGE_MATERIAL.asMaterial()).setName(Config.MESSAGES.GUI_PREVPAGE.value());
	
	public GuiBase(main plugin, String name, int size) {
		GuiBase.plugin = plugin;
		guiName = name;
		guiSize = size;
		gui = Bukkit.createInventory(null, guiSize, guiName);
	}
	
	public void openGUI(Player pl) {
		
		int page = 0;
		if (!Library.hasMeta(pl, GUIPageMeta.class)) pl.setMetadata(GUIPageMeta.NAME, new GUIPageMeta(main.instance, page));
		else page = Library.getMeta(pl, GUIPageMeta.class).asInt();
		
		TreeMap<Integer, ItemStack> mapped = new TreeMap<>(contents);
		
		for (Entry<Integer, ItemStack> ent : mapped.entrySet()) {
			if (mapped.lastKey() > guiSize - 1) {
				int pageMax = mapped.lastKey()/(guiSize-9) > Integer.valueOf(Float.valueOf(mapped.lastKey()/(guiSize-9)).toString().replaceAll("\\..*", "")) ? Integer.valueOf(Float.valueOf(mapped.lastKey()/(guiSize-9)).toString().replaceAll("\\..*", "")) + 1 : Integer.valueOf(Float.valueOf(mapped.lastKey()/(guiSize-9)).toString().replaceAll("\\..*", "")); 
				
				if (ent.getKey() <= guiSize - 9 && page == 0) gui.setItem(ent.getKey(), ent.getValue());
				else if (page >= 1) {
					
					int newPlace = ent.getKey() - page * (guiSize - 9);
					if (newPlace >= 0 && newPlace < guiSize - 9) {
						gui.setItem(newPlace, ent.getValue());
					}
				}
				
				if (page != 0) gui.setItem(guiSize - 9, prevPage);
				if (page != pageMax) gui.setItem(guiSize - 1, nextPage);
				
			} else {
				gui.setItem(ent.getKey(), ent.getValue());
			}
		}
		
		pl.openInventory(gui);
	}
	
	public void openGUI(CommandSender sender, Player player) {
		
		if (!(sender instanceof Player)) return;
		
		int page = 0;
		if (!Library.hasMeta((Player) sender, GUIPageMeta.class)) ((Player) sender).setMetadata(GUIPageMeta.NAME, new GUIPageMeta(main.instance, page));
		else page = Library.getMeta((Player) sender, GUIPageMeta.class).asInt();
		
		TreeMap<Integer, ItemStack> mapped = new TreeMap<>(contents);
		
		for (Entry<Integer, ItemStack> ent : mapped.entrySet()) {
			if (mapped.lastKey() > guiSize - 1) {
				int pageMax = mapped.lastKey()/(guiSize-9) > Integer.valueOf(Float.valueOf(mapped.lastKey()/(guiSize-9)).toString().replaceAll("\\..*", "")) ? Integer.valueOf(Float.valueOf(mapped.lastKey()/(guiSize-9)).toString().replaceAll("\\..*", "")) + 1 : Integer.valueOf(Float.valueOf(mapped.lastKey()/(guiSize-9)).toString().replaceAll("\\..*", "")); 
				
				if (ent.getKey() < guiSize - 9 && page == 0) gui.setItem(ent.getKey(), ent.getValue());
				else if (page >= 1) {
					
					int newPlace = ent.getKey() - page * (guiSize - 9);
					if (newPlace >= 0 && newPlace < guiSize - 9) {
						gui.setItem(newPlace, ent.getValue());
					}
				}
				
				if (page != 0) gui.setItem(guiSize - 9, prevPage);
				if (page != pageMax) gui.setItem(guiSize - 1, nextPage);
				
			} else {
				gui.setItem(ent.getKey(), ent.getValue());
			}
		}
		
		((HumanEntity) sender).openInventory(gui);
		
	}
	
	public void onClicked(int slot, Player pl) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
		
		if (nextPage.isSimilar(gui.getItem(slot))) {
			pageNext.run(pl);
			return;
		} else if (prevPage.isSimilar(gui.getItem(slot))) {
			pagePrev.run(pl);
			return;
		}
		
		if (!contents.containsKey(slot) || !commands.containsKey(contents.get(slot))) pl.playSound(pl.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
		
		else {
			
			ItemStack item = contents.get(slot);
			SexyRunnable m = commands.get(item);
			if (arguments.containsKey(item)) {
				Object[] args = arguments.get(item);
				
				int i = 0;
				for (Object arg : arguments.get(item)) {
					if (arg.equals("<player>")) {
						args[i] = pl;
					}
					i++;
				}
				
				m.run(args);
			}
			else m.run();
			
		}
	}
	
	public void onClicked(ItemStack item, Player pl) {
		if (!contents.containsValue(item) && !commands.containsKey(item)) pl.playSound(pl.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);
		
		else {
		
			SexyRunnable m = commands.get(item);
			if (arguments.containsKey(item)) {
				Object[] args = arguments.get(item);
				
				int i = 0;
				for (Object arg : arguments.get(item)) {
					if (arg.equals("<player>")) {
						args[i] = pl;
					}
					i++;
				}
				
				m.run(args);
			}
			else m.run();
			
		}
	}
	
	abstract void setContents();
	abstract void setCommands();
	
	public String getGuiName() {
		return guiName;
	}
	
	public int getGuiSize() {
		return guiSize;
	}
	
	public Inventory getGui() {
		return gui;
	}
	
	SexyRunnable pageNext = new SexyRunnable() { 
		
		@Override
		public void run(Object... args) {
			// player
			gui.clear();
			int page = Library.getMeta((Player) args[0], GUIPageMeta.class).asInt();
			page ++;
			((Player) args[0]).setMetadata(GUIPageMeta.NAME, new GUIPageMeta(main.instance, page));
			openGUI((Player) args[0]);
		}
	};
	 
	SexyRunnable pagePrev = new SexyRunnable() {
		
		@Override
		public void run(Object... args) {
			// player
			gui.clear();
			int page = Library.getMeta((Player) args[0], GUIPageMeta.class).asInt();
			page --;
			((Player) args[0]).setMetadata(GUIPageMeta.NAME, new GUIPageMeta(main.instance, page));
			openGUI((Player) args[0]);
		}
	};
}
