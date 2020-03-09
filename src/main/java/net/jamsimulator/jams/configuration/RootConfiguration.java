package net.jamsimulator.jams.configuration;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the root of a configuration. This instance should be created using a JSON string or
 * a file that contains it.
 */
public class RootConfiguration extends Configuration {

	private File file;

	/**
	 * Creates an empty root configuration.
	 */
	public RootConfiguration() {
		super(null, new HashMap<>(), null);
		root = this;
		file = null;
	}

	/**
	 * Creates a root configuration using a file that contains a JSON string.
	 *
	 * @param json the json file to parse.
	 * @throws IOException
	 * @throws ParseException
	 */
	public RootConfiguration(File json) throws IOException, ParseException {
		super(null, loadJSON(json), null);
		root = this;
		file = json;
	}

	/**
	 * Creates a root configuration using a {@link Reader} that contains a JSON string.
	 *
	 * @param reader the reader.
	 * @throws IOException
	 * @throws ParseException
	 */
	public RootConfiguration(Reader reader) throws IOException, ParseException {
		super(null, loadJSON(reader), null);
		root = this;
		file = null;
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
	 * @throws IOException
	 */
	public void save() throws IOException {
		if (file != null)
			save(file);
	}


	private static Map<String, Object> loadJSON(File file) throws IOException, ParseException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Map<String, Object> map = loadJSON(reader);
		reader.close();
		return map;
	}

	private static Map<String, Object> loadJSON(Reader r) throws IOException, ParseException {
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

		Map<String, Object> map = (Map<String, Object>) new JSONParser().parse(string);
		return map;
	}
}
