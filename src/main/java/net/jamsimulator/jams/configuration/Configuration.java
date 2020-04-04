package net.jamsimulator.jams.configuration;

import net.jamsimulator.jams.utils.CollectionUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a configuration node. This class is use to manage configuration data easily.
 * <p>
 * A configuration node may be a {@link RootConfiguration} or a
 * node of a {@link RootConfiguration}.
 */
public class Configuration {

	protected String name;
	protected Map<String, Object> map;
	protected RootConfiguration root;


	/**
	 * Creates a configuration using an absolute name, a data map and a root.
	 *
	 * @param name the name.
	 * @param map  the map.
	 * @param root the root configuration.
	 */
	public Configuration(String name, Map<String, Object> map, RootConfiguration root) {
		this.name = name;
		this.map = map;
		this.root = root;
	}

	/**
	 * Returns the absolute name of this configuration.
	 * <p>
	 * This is the name that should be used to access this configuration from
	 * the {@link RootConfiguration}.
	 *
	 * @return the absolute name.
	 */
	public String getAbsoluteName() {
		return name;
	}

	/**
	 * Returns the relative name of this configuration. This is equivalent to the last
	 * node of the absolute name.
	 * <p>
	 * For example, if the absolute name of a configuration is "a.b.c" the relative name will be "c".
	 *
	 * @return the relative node.
	 */
	public String getRelativeName() {
		int index = name.lastIndexOf(".");
		if (index == -1 || index == name.length() - 1) return name;
		return name.substring(index + 1);
	}

	/**
	 * Returns the {@link RootConfiguration} of the configuration.
	 *
	 * @return the {@link RootConfiguration}.
	 */
	public RootConfiguration getRoot() {
		return root;
	}

	/**
	 * Returns the value that matches the given key, if present.
	 * <p>
	 * You can get values stored in the child nodes of this configuration using the separator ".".
	 * For example, if you want to get the value "data" inside the child "node", you must use the
	 * key "node.data".
	 * <p>
	 * This method won't modify the structure of the configuration. If the node that should contain the
	 * wanted value is not present, the method will immediately return {@column Optional.empty()}.
	 * <p>
	 * This method will never return {@link Map} instances, but {@link Configuration} objects.
	 *
	 * @param key the key.
	 * @param <T> the value type.
	 * @return the value, if present and matches the type.
	 */
	public <T> Optional<T> get(String key) {
		//Checks the key.
		if (key.isEmpty() || key.startsWith(".") || key.endsWith("."))
			throw new IllegalArgumentException("Bad key format: " + key + ".");
		String[] array = key.split("\\.");

		try {
			//If the array length is 1 return the value from the map.
			if (array.length == 1) return Optional.ofNullable((T) parseMap(key, map.get(key)));

			//Iterates the child nodes.
			Object obj = map.get(array[0]);
			if (!(obj instanceof Map)) return Optional.empty();
			Map<String, Object> child = (Map<String, Object>) obj;
			for (int i = 1; i < array.length - 1; i++) {
				obj = child.get(array[i]);
				if (!(obj instanceof Map)) return Optional.empty();
				child = (Map<String, Object>) obj;
			}

			//Returns the value.
			return Optional.ofNullable((T) parseMap(key, child.get(array[array.length - 1])));
		} catch (ClassCastException ex) {
			return Optional.empty();
		}
	}

	/**
	 * Returns the value that matches the given key, if present.
	 * If the value is not a {@link String}, returns it's {@link Object#toString()} representation.
	 * <p>
	 * You can get values stored in the child nodes of this configuration using the separator ".".
	 * For example, if you want to get the value "data" inside the child "node", you must use the
	 * key "node.data".
	 * <p>
	 * This method won't modify the structure of the configuration. If the node that should contain the
	 * wanted value is not present, the method will immediately return {@column Optional.empty()}.
	 * <p>
	 * This method will never return {@link Map} instances, but {@link Configuration} objects.
	 *
	 * @param key the key.
	 * @return the value as a {@link String}, if present.
	 * @throws ClassCastException whether the value doesn't match the given value type.
	 */
	public Optional<String> getString(String key) {
		Optional<Object> optional = get(key);
		return optional.map(Object::toString);
	}

	/**
	 * Returns all the children of this configuration. Maps are wrapped inside a configuration.
	 * <p>
	 * The given {@link Map} is a unmodifiable {@link Map} and it cannot be edited.
	 * Any modification results in a {@link UnsupportedOperationException}.
	 * <p>
	 * Whether the boolean "deep" is true, the map will contain all it's children values, and not
	 * the child configs. The name of these children values will be split using the separator ".".
	 *
	 * @param deep whether the map should cointain deep values.
	 * @return the map
	 */
	public Map<String, Object> getAll(boolean deep) {
		Map<String, Object> map = new HashMap<>();
		if (!deep) {
			this.map.forEach((key, value) -> map.put(key, parseMap(key, value)));
		} else {
			this.map.forEach((key, value) -> {
				value = parseMap(key, value);
				if (value instanceof Configuration) {
					Configuration cConfig = (Configuration) value;
					String relName = cConfig.getRelativeName();
					cConfig.getAll(true).forEach((cKey, cValue) ->
							map.put(relName + "." + cKey, cValue));
				} else map.put(key, value);
			});
		}
		return Collections.unmodifiableMap(map);
	}

	/**
	 * Sets the given value into the given key.
	 * <p>
	 * You can store values into the child nodes of this configuration using the separator ".".
	 * For example, if you want to store the value "data" inside the child "node", you must use the
	 * key "node.data".
	 * <p>
	 * This method modifies the structure of the configuration: it will make new {@link Map}s
	 * or override previous values that are not {@link Map}s to store the given object.
	 * <p>
	 * This method will never store {@link Configuration} or {@link Map} instances, but a deep copy of the {@link Map}s.
	 *
	 * @param key   the key.
	 * @param value the value.
	 */
	public void set(String key, Object value) {
		if (value == null) {
			remove(key);
			return;
		}
		if (key.isEmpty() || key.startsWith(".") || key.endsWith("."))
			throw new IllegalArgumentException("Bad key format: " + key + ".");
		String[] array = key.split("\\.");
		if (array.length == 1) {

			if (value instanceof Map) value = CollectionUtils.deepCopy(((Map<String, Object>) value));
			if (value instanceof Configuration) value = CollectionUtils.deepCopy(((Configuration) value).map);

			map.put(key, value);
			return;
		}

		Object obj = map.get(array[0]);
		if (!(obj instanceof Map)) {
			obj = new HashMap<String, Object>();
			map.put(array[0], obj);
		}
		Map<String, Object> child = (Map<String, Object>) obj;
		for (int i = 1; i < array.length - 1; i++) {
			obj = child.get(array[i]);
			if (!(obj instanceof Map)) {
				obj = new HashMap<String, Object>();
				child.put(array[i], obj);
			}
			child = (Map<String, Object>) obj;
		}
		child.put(array[array.length - 1], value instanceof Configuration ?
				CollectionUtils.deepCopy(((Configuration) value).map) : value);
	}

	/**
	 * Removes the value that matches the given key from the configuration.
	 * <p>
	 * If the value is a {@link Configuration}, this will also remove all its children.
	 *
	 * @param key the key.
	 */
	public void remove(String key) {
		if (key.isEmpty() || key.startsWith(".") || key.endsWith("."))
			throw new IllegalArgumentException("Bad key format: " + key + ".");
		String[] array = key.split("\\.");
		if (array.length == 1) {
			map.remove(key);
			return;
		}

		Object obj = map.get(array[0]);
		if (!(obj instanceof Map)) return;
		Map<String, Object> child = (Map<String, Object>) obj;
		for (int i = 1; i < array.length - 1; i++) {
			obj = child.get(array[i]);
			if (!(obj instanceof Map)) return;
			child = (Map<String, Object>) obj;
		}
		child.remove(array[array.length - 1]);
	}

	/**
	 * Adds all nodes that are present in the given {@link Configuration} but
	 * not in this instance.
	 * <p>
	 * This method won't override any present node, unless the old value is not a {@link Configuration}
	 * but the new one is.
	 *
	 * @param configuration the configuration.
	 */
	public void addNotPresentValues(Configuration configuration) {
		configuration.getAll(false).forEach((key, value) -> {
			if (value instanceof Configuration) {
				if (!map.containsKey(key)) {
					set(key, value);
				} else {
					Object tValue = get(key).orElse(null);
					if (tValue instanceof Configuration) {
						((Configuration) tValue).addNotPresentValues((Configuration) value);
					} else set(key, value);
				}
			} else {
				if (!map.containsKey(key)) {
					map.put(key, value);
				}
			}
		});
	}

	/**
	 * Removes this {@link Configuration} from the {@link RootConfiguration}.
	 * This will throw an {@link IllegalStateException} if this node is the root node.
	 */
	public void remove() {
		if (root == this) throw new IllegalStateException("You cannot remove the root of a configuration!");
		root.remove(name);
	}

	/**
	 * Saves this {@link Configuration} as a JSON string into the given file.
	 *
	 * @param useFormat whether the output text should be formatted.
	 * @param file      the file.
	 * @throws IOException writer IOException.
	 */
	public void save(File file, boolean useFormat) throws IOException {
		FileWriter writer = new FileWriter(file);
		JSONObject object = new JSONObject(map);

		if (useFormat)
			writer.write(object.toString(1));
		else writer.write(object.toString());
		writer.close();
	}


	private Object parseMap(String key, Object o) {
		if (o instanceof Map) {
			String name = key;
			if (this.name != null && !this.name.isEmpty())
				name = this.name + "." + name;
			return new Configuration(name, (Map<String, Object>) o, root);
		}
		return o;
	}

	@Override
	public String toString() {
		return "Configuration{" +
				"name='" + name + '\'' +
				", map=" + map +
				'}';
	}
}
