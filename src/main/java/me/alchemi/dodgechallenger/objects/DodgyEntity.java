package me.alchemi.dodgechallenger.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.DyeColor;
import org.bukkit.entity.EntityType;
import org.bukkit.material.Colorable;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.MetadataValueAdapter;

import me.alchemi.dodgechallenger.main;

public class DodgyEntity {

	String nbt;
	EntityType type;
	Map<String, MetadataValue> nbtMap = new HashMap<String, MetadataValue>();
	DyeColor colour = null;
	
	public DodgyEntity(String type, String nbt) {
		this.type = getEntityByName(type);
		this.nbt = nbt;
		parseNBT();
	}

	public EntityType getEntityByName(String name) {
        for (EntityType type : EntityType.values()) {
            if(type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
	
	@SuppressWarnings("deprecation")
	public void parseNBT() {
		
		if (nbt.equals("")) return;
		
		Pattern patternNumbers = Pattern.compile("(\\d+)");
		Pattern patternWords = Pattern.compile("([A-z_\\s]+)");
		Matcher patternNBTPart = Pattern.compile("(\\{.*\\})").matcher(nbt);
		
		while (patternNBTPart.find()) {
			String part = patternNBTPart.group();
			Matcher w = patternWords.matcher(part);
			Matcher n = patternNumbers.matcher(part);
			
			if (w.find()) {
				if (type.getEntityClass().isAssignableFrom(Colorable.class) && w.group().equals("Color") && n.find()) {
					colour = DyeColor.getByDyeData(Byte.parseByte(n.group()));
					continue;
				}
				
				if (n.find()) {
					nbtMap.put(w.group(), new MetadataValueAdapter(main.getInstance()) {
						
						@Override
						public Object value() {
							return n.group();
						}
						
						@Override
						public void invalidate() {}
					});
					continue;
				} else if (w.find()) {
					nbtMap.put(w.group(0), new MetadataValueAdapter(main.getInstance()) {
						
						@Override
						public Object value() {
							return w.group();
						}
						
						@Override
						public void invalidate() {}
					});
					continue;
				}
				
			}
			
		}
	}

	public String getName() {
		return type.name();
	}
	
	

	/**
	 * @return the nbt
	 */
	public String getNbt() {
		return nbt;
	}

	/**
	 * @return the type
	 */
	public EntityType getType() {
		return type;
	}

	/**
	 * @return the nbtMap
	 */
	public Map<String, MetadataValue> getNbtMap() {
		return nbtMap;
	}

	public boolean hasColour() {
		return colour != null;
	}
	
	public DyeColor getColour() {
		return colour;
	}
	
}
