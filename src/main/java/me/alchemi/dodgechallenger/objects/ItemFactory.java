package me.alchemi.dodgechallenger.objects;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import me.alchemi.al.configurations.Messenger;

public class ItemFactory extends ItemStack{

	public ItemFactory(Material type) {
		this.setType(type);
		this.setAmount(1);
	}
	
	public ItemFactory setName(String name) {
		ItemMeta meta = getItemMeta();
		meta.setDisplayName(Messenger.formatString(name));
		meta.setLocalizedName(Messenger.formatString(name));
		setItemMeta(meta);
		return this;
	}
	
	public ItemFactory setLore(List<String> lore) {
		ItemMeta meta = getItemMeta();
		List<String> lore2 = new ArrayList<String>();
		for (String s : lore) lore2.add(Messenger.formatString(s));
		meta.setLore(lore2);
		setItemMeta(meta);
		return this;
	}
	
	public ItemFactory setNum(int amount) {
		setAmount(amount);
		return this;
	}
	
	public ItemFactory addEnch(Enchantment ench) { return addEnch(ench, true); }
	
	public ItemFactory addEnch(Enchantment ench, boolean enchVisible) {
		ItemMeta meta = getItemMeta();
		if (ench != null) {
			meta.addEnchant(ench, 1, false);
			if (!enchVisible) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		setItemMeta(meta);
		return this;
	}
	
	public ItemFactory addEnch(Enchantment ench, double lvl) { return addEnch(ench, lvl, true); }
	
	public ItemFactory addEnch(Enchantment ench, double lvl, boolean enchVisible) {
		ItemMeta meta = getItemMeta();
		if (ench != null) {
			meta.addEnchant(ench, (int) lvl, false);
			if (!enchVisible) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		setItemMeta(meta);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public ItemFactory parseNBT(String nbt) {
		
		if (nbt.equals("")) return this;
		
		Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
		Map<String, Object> in = new Gson().fromJson(nbt, mapType);
		
		for (Entry<String, Object> ent : in.entrySet()) {
			
			System.out.println(ent.getKey());
			
			switch (ent.getKey()) {
			case "Enchantments":
				for (Map<String, Object> entry : (List<Map<String, Object>>) ent.getValue()) {
					addEnch(Enchantment.getByKey(NamespacedKey.minecraft(((String) entry.get("id")).replace("minecraft:", ""))), (double) entry.get("lvl"));
				}
				continue;
			case "display":
				
				Map<String, Object> entry = (Map<String, Object>) ent.getValue();
				if (entry.containsKey("Name")) setName(Messenger.formatString((String)entry.get("Name")));
				if (entry.containsKey("Lore")) setLore((List<String>)entry.get("Lore"));
				
				continue;
				
			}
			
		}
		return this;
	}
	
}
