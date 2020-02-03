package net.jamsimulator.jams.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CollectionUtils {

	public static <T, K> Map<T, K> deepCopy(Map<T, K> original) {
		Map<T, K> map = new HashMap<>();
		original.forEach((key, value) -> {
			if (value instanceof List) {
				map.put(key, (K) deepCopy((List) value));
			} else if (value instanceof Map) {
				map.put(key, (K) deepCopy((Map) value));
			} else map.put(key, value);
		});
		return map;
	}

	public static <T> List<T> deepCopy(List<T> original) {
		List<T> list = new LinkedList<T>();
		original.forEach(value -> {
			if (value instanceof List) {
				list.add((T) deepCopy((List) value));
			} else if (value instanceof Map) {
				list.add((T) deepCopy((Map) value));
			} else list.add(value);
		});
		return list;
	}
}
