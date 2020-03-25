package net.jamsimulator.jams.language;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.language.exception.LanguageFailedLoadException;
import net.jamsimulator.jams.utils.Validate;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Language {

	public static final String MESSAGE_SEPARATOR = "=";

	private String name;
	private Map<String, String> messages;

	public Language(String name, File file) throws LanguageFailedLoadException {
		Validate.notNull(name, "Name cannot be null!");
		Validate.notNull(file, "File cannot be null!");
		this.name = name;
		this.messages = new HashMap<>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			loadMessages(reader);
			reader.close();
		} catch (Exception ex) {
			throw new LanguageFailedLoadException(ex);
		}
	}

	public Language(String name, InputStream inputStream) throws LanguageFailedLoadException {
		Validate.notNull(name, "Name cannot be null!");
		Validate.notNull(inputStream, "Input stream cannot be null!");
		this.name = name;
		this.messages = new HashMap<>();

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		loadMessages(reader);
		try {
			reader.close();
		} catch (IOException e) {
			throw new LanguageFailedLoadException(e);
		}
	}

	public String getName() {
		return name;
	}

	public Optional<String> getMessage(String node) {
		return Optional.ofNullable(messages.get(node));
	}

	public String getOrEmpty(String node) {
		return messages.getOrDefault(node, "");
	}

	public String getOrDefault(String node) {
		String string = messages.get(node);
		if (string != null) return string;
		return Jams.getLanguageManager().getSelected().getOrEmpty(node);
	}


	private void loadMessages(BufferedReader reader) {
		try {
			String line;
			int index;
			String node;
			String message;
			while ((line = reader.readLine()) != null) {
				if (line.isEmpty()) continue;
				index = line.indexOf(MESSAGE_SEPARATOR);
				if (index == -1 || index == line.length() - 1) {
					System.err.println("Error while loading Language " + name + ": bad line format: " + line);
					continue;
				}

				node = line.substring(0, index);
				message = line.substring(index + 1);

				messages.put(node, message);
			}
		} catch (IOException e) {
			System.err.println("Error while loading Language " + name + ":");
			e.printStackTrace();
		}
	}
}
