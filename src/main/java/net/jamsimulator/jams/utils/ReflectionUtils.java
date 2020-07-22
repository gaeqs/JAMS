package net.jamsimulator.jams.utils;

import java.lang.reflect.Field;

public class ReflectionUtils {

	public static <E extends Enum> E[] getEnumValues(Class<E> enumClass)
			throws NoSuchFieldException, IllegalAccessException {
		Field f = enumClass.getDeclaredField("$VALUES");
		f.setAccessible(true);
		Object o = f.get(null);
		return (E[]) o;
	}

}
