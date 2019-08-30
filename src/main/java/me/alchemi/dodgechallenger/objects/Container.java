package me.alchemi.dodgechallenger.objects;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

@SuppressWarnings("unchecked")
public class Container<T extends StringSerializable> implements StringSerializable, Serializable, ConfigurationSerializable {

	private Map<T, Integer> contained_items;
	private Class<T> clazz;
	
	public Container() {
		contained_items = new LinkedHashMap<T, Integer>();
	}
	
	public Container(Collection<T> elements) {
		contained_items = new LinkedHashMap<T, Integer>();
		addAll(elements);
	}
	
	public Container(T... elements) {
		contained_items = new LinkedHashMap<T, Integer>();
		addAll(elements);
	}
	
	public void add(T element) {
		if (clazz == null) clazz = (Class<T>) element.getClass();
		
		if (contained_items.containsKey(element)) {
			int amount = contained_items.get(element);
			contained_items.put(element, amount++);
		} else {
			contained_items.put(element, 1);
		}
	}
	
	public void addAll(Collection<T> elements) {
		for (T element : elements) {
			add(element);
		}
	}
	
	public void addAll(T... elements) {
		for (T element : elements) {
			add(element);
		}
	}
	
	public void set(T element, int amount) {
		contained_items.put(element, amount);
	}
	
	public Entry<T, Integer> get(int index) throws IndexOutOfBoundsException {
		
		int i = 0;
		for (Entry<T, Integer> element : contained_items.entrySet()) {
			if (i == index) return element;
			i++;
		}
		throw new IndexOutOfBoundsException();
		
	}
	
	public int getAmount(T element) {
		if (contained_items.containsKey(element)) return contained_items.get(element);
		return -1;
	}
	
	public boolean contains(T element) {
		return contained_items.containsKey(element);
	}

	public boolean containsAll(Collection<T> elements) {
		for (T e : elements) {
			if (!contains(e)) return false;
		}
		return true;
	}

	public void remove(T element) {
		if (contains(element)) {
			
			if (contained_items.get(element) == 1) {
				contained_items.remove(element);
			} else {
				contained_items.put(element, contained_items.get(element) - 1);
			}
			
		}
	}
	
	public void removeAll(T element) {
		if (contains(element)) {
			contained_items.remove(element);
		}
	}
	
	public void clear() {
		contained_items.clear();
	}
	
	public boolean isEmpty() {
		return contained_items.isEmpty();
	}
	
	public int size() {
		return contained_items.size();
	}
	
	@Override
	public String toString() {
		return "Container[" + String.join(", ", contained_items.entrySet().toString()) + "]";
	}

	@Override
	public String serialize_string() {
		
		if (contained_items.isEmpty()) return Container.class.getName() + "[]";
		
		String content = "";
		for (Entry<T, Integer> ent : contained_items.entrySet()) {
			if (content.isEmpty()) content = "{" + ent.getKey().serialize_string() + ":" + ent.getValue() + "}";
			else content = content.concat(", {" + ent.getKey().serialize_string() + ":" + ent.getValue() + "}");
		}
		
		return this.getClass().getName() + "<" + clazz.getName() + ">[" + content + "]";
	}

	public static Container<? extends StringSerializable> deserialize_string(String serialized) {
		if (!serialized.startsWith(Container.class.getName())) throw new IllegalArgumentException("Serialized string doesn't originate from Container.");
		
		Matcher m = Pattern.compile("(?:<)(.+(?=>))").matcher(serialized);
		Class<?> clazz; 
		if (m.find()) {
			try {
				clazz = Class.forName(m.group().replace("<", ""));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}

		Method serializeMethod = StringSerializable.getDeserializeMethod((Class<? extends StringSerializable>) clazz);
		String[] parts = serialized.replaceFirst("(" + Container.class.getName() + "<" + clazz.getName() + ">\\[\\{)", "").replaceFirst("(\\}]$)", "").split("\\}, \\{");
		
		Container<StringSerializable> container = new Container<StringSerializable>();
		for (String part : parts) {
			String serializePart = part.replaceFirst("(:\\d+$)", "");
			int amount = Integer.parseInt(part.replace(serializePart + ":", ""));
			try {
				container.set((StringSerializable) serializeMethod.invoke(null, serializePart), amount);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		return container;
	}
	
	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> result = new LinkedHashMap<String, Object>();
		
		result.put("type", clazz.getName());
		result.put("contained_items", new HashMap<String, Integer>(){
			{
				for (Entry<T, Integer> entry : contained_items.entrySet()) {
					put(entry.getKey().serialize_string(), entry.getValue());
				}
			}
		});
		
		return result;
	}
	
	public static Container<? extends StringSerializable> deserialize(Map<String, Object> serialized){
		Container<StringSerializable> container;
		try {
			Class<? extends StringSerializable> clazz = (Class<? extends StringSerializable>) Class.forName((String) serialized.get("type"));
			serialized.remove("type");
			
			Method serializeMethod = StringSerializable.getDeserializeMethod(clazz);
			
			container = new Container<StringSerializable>();
			for (Entry<String, Integer> entry : ((Map<String, Integer>) serialized.get("contained_items")).entrySet()) {
				container.set((StringSerializable) serializeMethod.invoke(null, entry.getKey()), entry.getValue());
			}
			
		} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
		
		return container;
		
	}
	
}
