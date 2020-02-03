package net.jamsimulator.jams.configuration;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the root of a configuration. This instance should be created using a JSON string or
 * a file that contains it.
 */
public class RootConfiguration extends Configuration {

	private File file;

	/**
	 * Creates a root configuration using a file that contains a JSON string.
	 *
	 * @param json
	 * @throws IOException
	 * @throws ParseException
	 */
	public RootConfiguration(File json) throws IOException, ParseException {
		super(null, loadJSON(json), null);
		root = this;
		this.file = json;
	}

	/**
	 * Saves the {@link RootConfiguration} into the file that loaded it, if present.
	 *
	 * @throws IOException
	 */
	public void save() throws IOException {
		save(file);
	}


	private static Map<String, Object> loadJSON(File file) throws IOException, ParseException {
		BufferedReader reader = new BufferedReader(new FileReader(file));

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
		reader.close();
		return map;
	}
}
