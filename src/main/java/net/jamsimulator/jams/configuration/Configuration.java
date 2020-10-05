/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.configuration;

import net.jamsimulator.jams.gui.util.converter.ValueConverters;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.gui.util.converter.ValueConverter;
import net.jamsimulator.jams.utils.CollectionUtils;
import net.jamsimulator.jams.utils.Validate;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Represents a configuration node. This class is use to manage configuration data easily.
 * <p>
 * A configuration node may be a {@link RootConfiguration} or a
 * node of a {@link RootConfiguration}.
 */
public class Configuration {

	public static final Set<Class<?>> NATIVE_CLASSES = Set.of(
			Byte.class,
			Short.class,
			Integer.class,
			Long.class,
			Float.class,
			Double.class,
			Character.class,
			Boolean.class,
			String.class,
			List.class,
			Map.class);

	public static boolean isObjectNativelySupported(Object o) {
		return NATIVE_CLASSES.stream().anyMatch(target -> target.isInstance(o));
	}

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
		Validate.notNull(map, "Map cannot be null!");
		Validate.isTrue(root != null || this instanceof RootConfiguration, "Root cannot be found!");
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
	 * wanted value is not present, the method will immediately return {@code Optional.empty()}.
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
	 * Returns the value that matches the given key, if present, and converts it with the
	 * {@link ValueConverter}
	 * that matches the given name.
	 * <p>
	 * You can get values stored in the child nodes of this configuration using the separator ".".
	 * For example, if you want to get the value "data" inside the child "node", you must use the
	 * key "node.data".
	 * <p>
	 * This method won't modify the structure of the configuration. If the node that should contain the
	 * wanted value is not present, the method will immediately return {@code Optional.empty()}.
	 * <p>
	 * This method will never return {@link Map} instances, but {@link Configuration} objects.
	 *
	 * @param key the key.
	 * @param <T> the value type.
	 * @return the value, if present and matches the type.
	 */
	public <T> Optional<T> getAndConvert(String key, String converter) {
		var c = ValueConverters.getByName(converter);
		try {
			if (c.isEmpty()) {
				Optional<Object> optional = get(key);
				if (optional.isEmpty()) return Optional.empty();
				return Optional.of((T) optional.get());
			} else {
				return (Optional<T>) c.get().load(this, key);
			}
		} catch (ClassCastException ex) {
			return Optional.empty();
		}
	}

	/**
	 * Returns the value that matches the given key, if present, and converts it with the
	 * {@link ValueConverter}
	 * that matches the given type.
	 * <p>
	 * You can get values stored in the child nodes of this configuration using the separator ".".
	 * For example, if you want to get the value "data" inside the child "node", you must use the
	 * key "node.data".
	 * <p>
	 * This method won't modify the structure of the configuration. If the node that should contain the
	 * wanted value is not present, the method will immediately return {@code Optional.empty()}.
	 * <p>
	 * This method will never return {@link Map} instances, but {@link Configuration} objects.
	 *
	 * @param key the key.
	 * @param <T> the value type.
	 * @return the value, if present and matches the type.
	 */
	public <T> Optional<T> getAndConvert(String key, Class<?> type) {
		var c = ValueConverters.getByType(type);
		try {
			if (c.isEmpty()) {
				Optional<Object> optional = get(key);
				if (optional.isEmpty()) return Optional.empty();
				return Optional.of((T) optional.get());
			} else {
				return (Optional<T>) c.get().load(this, key);
			}
		} catch (ClassCastException ex) {
			return Optional.empty();
		}
	}

	/**
	 * Returns the number that matches the given key, if present.
	 * <p>
	 * You can get values stored in the child nodes of this configuration using the separator ".".
	 * For example, if you want to get the value "data" inside the child "node", you must use the
	 * key "node.data".
	 * <p>
	 * This method won't modify the structure of the configuration. If the node that should contain the
	 * wanted value is not present, the method will immediately return {@code Optional.empty()}.
	 *
	 * @param key the key.
	 * @return the number, if present and matches the type.
	 */
	public Optional<Number> getNumber(String key) {
		return get(key);
	}

	/**
	 * Returns the value that matches the given key or the value given if not present.
	 * <p>
	 * You can get values stored in the child nodes of this configuration using the separator ".".
	 * For example, if you want to get the value "data" inside the child "node", you must use the
	 * key "node.data".
	 * <p>
	 * This method won't modify the structure of the configuration. If the node that should contain the
	 * wanted value is not present, the method will immediately return {@code Optional.empty()}.
	 * <p>
	 * This method will never return {@link Map} instances, but {@link Configuration} objects.
	 *
	 * @param key    the key.
	 * @param orElse the value returned if no element was found.
	 * @param <T>    the value type.
	 * @return the value if present and matches the type. Else, returns the given element.
	 */
	public <T> T getOrElse(String key, T orElse) {
		Optional<T> optional = get(key);
		return optional.orElse(orElse);
	}

	/**
	 * Returns the configuration that matches the given key, if present.
	 * <p>
	 * If no configuration is present, one new configuration will be created, replacing
	 * any previous value.
	 * <p>
	 * You can get configurations stored in the child nodes of this configuration using the separator ".".
	 * For example, if you want to get the configuration "data" inside the child "node", you must use the
	 * key "node.data".
	 *
	 * @param key the key.
	 * @return the configuration.
	 */
	public Configuration getOrCreateConfiguration(String key) {
		Optional<Configuration> config = get(key);
		if (config.isPresent()) return config.get();
		set(key, new HashMap<>());
		config = get(key);
		return config.get();
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
	 * wanted value is not present, the method will immediately return {@code Optional.empty()}.
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
	 * Returns the value that matches the given key, if present.
	 * If the value is not a {@link Enum<T>}, returns {@code Optional.empty()}.
	 * <p>
	 * You can get values stored in the child nodes of this configuration using the separator ".".
	 * For example, if you want to get the value "data" inside the child "node", you must use the
	 * key "node.data".
	 * <p>
	 * This method won't modify the structure of the configuration. If the node that should contain the
	 * wanted value is not present, the method will immediately return {@code Optional.empty()}.
	 *
	 * @param key the key.
	 * @return the value as a {@link String}, if present.
	 * @throws ClassCastException whether the value doesn't match the given value type.
	 */
	public <T extends Enum<T>> Optional<T> getEnum(Class<T> clazz, String key) {
		Optional<String> optional = getString(key);
		if (!optional.isPresent()) return Optional.empty();
		try {
			return Optional.of(Enum.valueOf(clazz, optional.get()));
		} catch (IllegalArgumentException ex) {
			return Optional.empty();
		}
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
		if (key.isEmpty() || key.startsWith(".") || key.endsWith("."))
			throw new IllegalArgumentException("Bad key format: " + key + ".");

		if (value instanceof Map) value = CollectionUtils.deepCopy(((Map<String, Object>) value));
		else if (value instanceof Configuration) value = CollectionUtils.deepCopy(((Configuration) value).map);

		String[] array = key.split("\\.");
		Map<String, Object> current = map;
		Object obj;

		for (int i = 0; i < array.length - 1; i++) {
			obj = current.get(array[i]);
			if (!(obj instanceof Map)) {
				if (value == null) return;
				obj = new HashMap<>();
				current.put(array[i], obj);
			}
			current = (Map<String, Object>) obj;
		}

		Object old = current.get(array[array.length - 1]);

		String absoluteKey = name == null || name.isEmpty() ? key : name + "." + key;
		ConfigurationNodeChangeEvent.Before before = root.callEvent(new ConfigurationNodeChangeEvent.Before(this, absoluteKey, old, value));
		if (before.isCancelled()) return;
		Object nValue = before.getNewValue().orElse(null);

		if (value != nValue) {
			if (nValue instanceof Map) value = CollectionUtils.deepCopy(((Map<String, Object>) nValue));
			else if (nValue instanceof Configuration) value = CollectionUtils.deepCopy(((Configuration) nValue).map);
			else value = nValue;
		}

		current.put(array[array.length - 1], value);
		root.callEvent(new ConfigurationNodeChangeEvent.After(this, absoluteKey, old, value));
	}

	/**
	 * Sets the converted given value into the given key.
	 * The value is converted by the
	 * {@link ValueConverter}
	 * that matches the given type.
	 * <p>
	 * If the converted is not found or it's not valid this method returns false.
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
	public boolean convertAndSet(String key, Object value, Class<?> converter) {
		if (isObjectNativelySupported(value)) {
			set(key, value);
			return true;
		}

		var c = ValueConverters.getByType(converter);
		if (c.isEmpty() || !c.get().conversionClass().isInstance(value)) return false;
		c.get().save(this, key, value);
		return true;
	}

	/**
	 * Removes the value that matches the given key from the configuration.
	 * <p>
	 * If the value is a {@link Configuration}, this will also remove all its children.
	 *
	 * @param key the key.
	 */
	public void remove(String key) {
		set(key, null);
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
	 * <p>
	 * Any further modification on this configuration won't cause any effect on the {@link RootConfiguration}.
	 */
	public void remove() {
		if (root == this) throw new IllegalStateException("You cannot remove the root of a configuration!");
		root.remove(name);
	}

	/**
	 * Removes all children from this configuration.
	 * Any further modification on any child configuration won't cause any effect on this configuration.
	 */
	public void clear() {
		map.clear();
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
