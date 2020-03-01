package net.jamsimulator.jams.configuration;

import net.jamsimulator.jams.utils.CollectionUtils;
import org.json.simple.JSONObject;

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
	 * @return the key, if present.
	 * @throws ClassCastException whether the value doesn't match the given value type.
	 */
	public <T> Optional<T> get(String key) {
		//Checks the key.
		if (key.isEmpty() || key.startsWith(".") || key.endsWith("."))
			throw new IllegalArgumentException("Bad key format: " + key + ".");
		String[] array = key.split("\\.");

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
	}

	/**
	 * Returns all the children of this configuration. Maps are wrapped inside a configuration.
	 * <p>
	 * The given {@link Map} is a unmodifiable {@link Map} and it cannot be edited.
	 * Any modification results in a {@link UnsupportedOperationException}.
	 *
	 * @return the map
	 */
	public Map<String, Object> getAll() {
		Map<String, Object> map = new HashMap<>();
		this.map.forEach((key, value) -> map.put(key, parseMap(key, value)));
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
	 * This method will never store {@link Configuration} instances, but a deep copy their inner {@link Map}s.
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
			map.put(key, value instanceof Configuration ?
					CollectionUtils.deepCopy(((Configuration) value).map) : value);
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
	 * @param file the file.
	 * @throws IOException
	 */
	public void save(File file) throws IOException {
		FileWriter writer = new FileWriter(file);
		JSONObject.writeJSONString(map, writer);
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
}
