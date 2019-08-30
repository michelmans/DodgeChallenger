package me.alchemi.dodgechallenger.objects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface StringSerializable {

	String serialize_string();
	
	static Method getDeserializeMethod(Class<? extends StringSerializable> clazz) {
		
		try {
			return clazz.getMethod("deserialize_string", String.class);
		} catch (NoSuchMethodException | SecurityException e) {
			try {
				throw new NoSuchMethodException("Subclass of StringSerializable should implement the public static method: deserialize.");
			} catch (NoSuchMethodException e1) {
				e1.printStackTrace();
			}
		}
		return null;
		 
	}
	
	@SuppressWarnings("unchecked")
	static Object deserialize(String deserialize) {
		Matcher m = Pattern.compile("(^[\\w.]+)").matcher(deserialize);
		
		Class<?> clazz;
		if (m.find()) {
			try {
				clazz = Class.forName(m.group());
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		} else return null;
		
		Method deserializeMethod = getDeserializeMethod((Class<? extends StringSerializable>) clazz);
		if (deserializeMethod == null) return null;
		
		try {
			return deserializeMethod.invoke(null, deserialize);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
