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

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the root of a configuration. This instance should be created using a JSON string or
 * a file that contains it.
 */
public class RootConfiguration extends Configuration implements EventBroadcast {

	private File file;

	private final SimpleEventBroadcast broadcast;

	/**
	 * Creates an empty root configuration.
	 */
	public RootConfiguration() {
		super(null, new HashMap<>(), null);
		root = this;
		file = null;
		broadcast = new SimpleEventBroadcast();
	}

	/**
	 * Creates a root configuration using a file that contains a JSON string.
	 *
	 * @param json the json file to parse.
	 * @throws IOException
	 */
	public RootConfiguration(File json) throws IOException {
		super(null, loadJSON(json), null);
		root = this;
		file = json;
		broadcast = new SimpleEventBroadcast();
	}

	/**
	 * Creates a root configuration using a {@link Reader} that contains a JSON string.
	 *
	 * @param reader the reader.
	 * @throws IOException
	 */
	public RootConfiguration(Reader reader) throws IOException {
		super(null, loadJSON(reader), null);
		root = this;
		file = null;
		broadcast = new SimpleEventBroadcast();
	}

	/**
	 * Sets the default save file.
	 *
	 * @param file the default save file.
	 */
	public void setFile(File file) {
		this.file = file;
	}

	/**
	 * Saves the {@link RootConfiguration} into the file that loaded it, if present.
	 *
	 * @param useFormat whether the output text should be formatted.
	 * @throws IOException writer IOException.
	 */
	public void save(boolean useFormat) throws IOException {
		if (file != null)
			save(file, useFormat);
	}


	private static Map<String, Object> loadJSON(File file) throws IOException {
		if(!file.isFile()) return new HashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Map<String, Object> map = loadJSON(reader);
		reader.close();
		return map;
	}

	private static Map<String, Object> loadJSON(Reader r) throws IOException {
		BufferedReader reader = new BufferedReader(r);
		//Loads the string first. This allows us to check if the file is empty.
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		String line;
		while ((line = reader.readLine()) != null) {
			if (!first) {
				builder.append('\n');
			} else first = false;
			builder.append(line);
		}
		String string = builder.toString();
		//If empty, return a new HashMap.
		if (string.isEmpty()) return new HashMap<>();

		return new JSONObject(string).toMap();
	}

	//region broadcast methods

	@Override
	public boolean registerListener(Object instance, Method method, boolean useWeakReferences) {
		return broadcast.registerListener(instance, method, useWeakReferences);
	}

	@Override
	public int registerListeners(Object instance, boolean useWeakReferences) {
		return broadcast.registerListeners(instance, useWeakReferences);
	}

	@Override
	public boolean unregisterListener(Object instance, Method method) {
		return broadcast.unregisterListener(instance, method);
	}

	@Override
	public int unregisterListeners(Object instance) {
		return broadcast.unregisterListeners(instance);
	}

	@Override
	public <T extends Event> T callEvent(T event) {
		return broadcast.callEvent(event, this);
	}

	//endregion
}
