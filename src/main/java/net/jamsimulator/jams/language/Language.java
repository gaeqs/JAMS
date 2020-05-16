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

package net.jamsimulator.jams.language;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.language.exception.LanguageFailedLoadException;
import net.jamsimulator.jams.utils.Validate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class Language {

	public static final String MESSAGE_SEPARATOR = "=";

	private final String name;
	private final File file;
	private final Map<String, String> messages;

	public Language(File file) throws LanguageFailedLoadException {
		Validate.notNull(file, "File cannot be null!");
		this.file = file;
		this.messages = new HashMap<>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			name = reader.readLine();
			loadMessages(reader);
			reader.close();
		} catch (IOException e) {
			throw new LanguageFailedLoadException(e);
		}
	}

	public Language(InputStream inputStream) throws LanguageFailedLoadException {
		Validate.notNull(inputStream, "Input stream cannot be null!");
		this.file = null;
		this.messages = new HashMap<>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
			name = reader.readLine();
			loadMessages(reader);
			reader.close();
		} catch (IOException e) {
			throw new LanguageFailedLoadException(e);
		}
	}

	public String getName() {
		return name;
	}

	public Optional<File> getFile() {
		return Optional.ofNullable(file);
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
		return Jams.getLanguageManager().getDefault().getOrEmpty(node);
	}

	public void addNotPresentValues(Language language) {
		language.messages.forEach(messages::putIfAbsent);
	}

	public boolean save() {
		return save(file);
	}

	public boolean save(File file) {
		if (file == null) return false;
		try {

			OutputStream stream = new FileOutputStream(file);

			Writer writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
			writer.write(name + "\n");

			for (Map.Entry<String, String> entry : messages.entrySet()) {
				writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
			}

			writer.close();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void loadMessages(BufferedReader reader) throws IOException {
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
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Language language = (Language) o;
		return name.equals(language.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
