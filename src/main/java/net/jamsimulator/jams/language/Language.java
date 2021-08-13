/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.language;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.language.exception.LanguageFailedLoadException;
import net.jamsimulator.jams.manager.Labeled;
import net.jamsimulator.jams.utils.Validate;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a language. A language contains can be interpreted as a map that links language nodes
 * to their message in the represented language.
 * <p>
 * A language may be linked to a file. This file will contain all the language data when this language is saved.
 * <p>
 * A language file has the following syntax:
 * <p>
 * - The first line represents the name of the language. (Example: English)
 * <p>
 * - A message is defined as LANGUAGE_NODE=Message. (Example: TEST_MESSAGE=This message is a test!)
 * <p>
 * - You can define complex messages using the operator .= . These messages can be defined in multiple lines. The final line must be "\END".
 * You can use several styling options in these messages using the xml tag syntax.
 * The valid optiona are: b, code, u, i, sub.
 */
public class Language implements Labeled {

    public static final String MESSAGE_SEPARATOR = "=";
    public static final char LITERAL_CHARACTER = '.';

    private final String name;
    private final File file;
    private final Map<String, String> messages;

    /**
     * Loads the language present in the given file.
     *
     * @param file the file to load.
     * @throws LanguageFailedLoadException whether an exception occurs while loading the language.
     */
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

    /**
     * Loads the language present in the given stream.
     *
     * @param inputStream the stream.
     * @throws LanguageFailedLoadException whether an exception occurs while loading the language.
     */
    public Language(InputStream inputStream) throws LanguageFailedLoadException {
        Validate.notNull(inputStream, "Input stream cannot be null!");
        this.file = null;
        this.messages = new HashMap<>();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            name = reader.readLine();
            loadMessages(reader);
        } catch (IOException e) {
            throw new LanguageFailedLoadException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the file containing this language if present.
     *
     * @return the file if present.
     */
    public Optional<File> getFile() {
        return Optional.ofNullable(file);
    }

    /**
     * Returns the message linked to the given node if present.
     *
     * @param node the node.
     * @return the message if present.
     */
    public Optional<String> getMessage(String node) {
        return Optional.ofNullable(messages.get(node));
    }

    /**
     * Returns the message linked to the given node or an empty string if not found.
     *
     * @param node the node.
     * @return the message or an empty string.
     */
    public String getOrEmpty(String node) {
        return messages.getOrDefault(node, "");
    }

    /**
     * Returns the message linked to the given node or the message linked to the default language.
     * If the default language doesn't contain the language node neither, an empty string is returned.
     *
     * @param node the node.
     * @return the message or an empty string.
     */
    public String getOrDefault(String node) {
        String string = messages.get(node);
        if (string != null) return string;
        return Jams.getLanguageManager().getDefault().getOrEmpty(node);
    }

    /**
     * Merges the contents of the given language with the nodes of this language. The result is stored in this
     * language.
     * <p>
     * This method allows loading messages for a language from different files (and, in extension, from different plugins).
     *
     * @param language the language to merge.
     */
    public void addNotPresentValues(Language language) {
        language.messages.forEach(messages::putIfAbsent);
    }

    /**
     * Saves the language into the file representing this language.
     *
     * @return whether the operation was sucessful.
     * @see #getFile()
     */
    public boolean save() {
        return save(file);
    }

    /**
     * Saves the language into the given file.
     *
     * @return whether the operation was sucessful.
     */
    public boolean save(File file) {
        if (file == null) return false;
        try {

            OutputStream stream = new FileOutputStream(file);

            Writer writer = new BufferedWriter(new OutputStreamWriter(stream, StandardCharsets.UTF_8));
            writer.write(name + "\n");

            for (Map.Entry<String, String> entry : messages.entrySet()) {
                //If contains \n the text is literal.
                if (entry.getValue().contains("\n")) {
                    writer.write(entry.getKey() + LITERAL_CHARACTER + MESSAGE_SEPARATOR + entry.getValue());
                    writer.write("\n\\END\n");
                } else {
                    writer.write(entry.getKey() + MESSAGE_SEPARATOR + entry.getValue() + '\n');
                }
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
        StringBuilder message;
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty() || line.startsWith("//")) continue;
            index = line.indexOf(MESSAGE_SEPARATOR);
            if (index == -1) {
                System.err.println("Error while loading Language " + name + ": bad line format: " + line);
                continue;
            }

            node = line.substring(0, index);
            message = new StringBuilder(line.substring(index + 1));

            if (node.isEmpty()) continue;

            if (node.charAt(node.length() - 1) == LITERAL_CHARACTER) {
                node = node.substring(0, node.length() - 1);
                if (node.isEmpty()) continue;

                while ((line = reader.readLine()) != null && !line.equals("\\END")) {
                    message.append('\n').append(line);
                }
            }

            messages.put(node, message.toString());
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
