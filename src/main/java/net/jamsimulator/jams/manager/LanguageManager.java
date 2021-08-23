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

package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.exception.LanguageFailedLoadException;
import net.jamsimulator.jams.utils.FolderUtils;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This singleton stores all {@link Language}s that JAMS may use.
 * <p>
 * To register a {@link Language} use {@link #add(Labeled)}.
 * To unregister a {@link Language} use {@link #remove(Object)}.
 * <p>
 * The selected {@link Language} will be the one to be used by the GUI.
 */
public final class LanguageManager extends SelectableManager<Language> {

    public static final String FOLDER_NAME = "languages";
    public static final String DEFAULT_LANGUAGE_NODE = "language.default";
    public static final String SELECTED_LANGUAGE_NODE = "language.selected";

    public static final LanguageManager INSTANCE = new LanguageManager();

    private Map<String, String> bundledLanguages;
    private File folder;

    private LanguageManager() {
        Jams.getMainConfiguration().registerListeners(this, true);
    }

    /**
     * Loads the languages present in the given streams.
     * If the language is already present, the language is merged. If not, the language is added to this manager.
     *
     * @param streams      the streams.
     * @param closeStreams whether the streams should be closed after the language is closed.
     */
    public void loadLanguages(Collection<InputStream> streams, boolean closeStreams) {
        Validate.hasNoNulls(streams, "Streams cannot have any null value!");
        for (InputStream in : streams) {
            try {
                var language = new Language(in);
                var other = get(language.getName());

                if (other.isEmpty()) {
                    add(language);
                } else {
                    other.get().addNotPresentValues(language);
                    other.get().save();
                }
            } catch (LanguageFailedLoadException ex) {
                ex.printStackTrace();
            } finally {

                if (closeStreams) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    @Override
    protected void loadDefaultElements() {
        loadLanguagesFolder();
        loadStoredLanguages();
        refreshBundledLanguages();
    }

    @Override
    protected Language loadDefaultElement() {
        var configDefault = Jams.getMainConfiguration().getString(DEFAULT_LANGUAGE_NODE).orElse("English");

        var english = get("English");
        var defaultOptional = get(configDefault);
        if (defaultOptional.isEmpty()) {
            System.err.println("Default language " + configDefault + " not found. Using English instead.");
            if (english.isEmpty()) {
                System.err.println("English language not found! Using the first found language instead.");
                return stream().findFirst().orElseThrow(NullPointerException::new);
            } else return english.get();
        } else return defaultOptional.get();
    }

    @Override
    protected Language loadSelectedElement() {
        var configSelected = Jams.getMainConfiguration().getString(SELECTED_LANGUAGE_NODE).orElse("English");

        var english = get("English");
        var selectedOptional = get(configSelected);
        if (selectedOptional.isEmpty()) {
            System.err.println("Selected language " + configSelected + " not found. Using English instead.");
            if (english.isEmpty()) {
                System.err.println("English language not found! Using the first found language instead.");
                return stream().findFirst().orElseThrow(NullPointerException::new);
            } else return english.get();
        } else return selectedOptional.get();
    }

    private void loadLanguagesFolder() {
        folder = new File(Jams.getMainFolder(), FOLDER_NAME);
        FolderUtils.checkFolder(folder);

        bundledLanguages = new HashMap<>();
        bundledLanguages.put("English", "/language/english.jlang");
        bundledLanguages.put("Spanish", "/language/spanish.jlang");

        bundledLanguages.forEach((fileName, resourcePath) -> {
            File file = new File(folder, fileName.toLowerCase() + ".jlang");
            if (!file.exists()) {
                if (!FolderUtils.moveFromResources(Jams.class, resourcePath, file))
                    throw new NullPointerException(fileName + " language not found!");
            }
        });
    }

    private void loadStoredLanguages() {
        var files = folder.listFiles();
        if (files == null) throw new NullPointerException("There's no languages!");

        for (File file : files) {
            if (!file.getName().toLowerCase().endsWith(".jlang")) continue;

            try {
                add(new Language(file));
            } catch (LanguageFailedLoadException ex) {
                System.err.println("Failed to load language " + file.getName() + ": ");
                ex.printStackTrace();
            }
        }

        if (isEmpty()) throw new NullPointerException("There's no languages!");
    }

    private void refreshBundledLanguages() {
        bundledLanguages.forEach((key, value) -> {
            var language = get(key).orElse(null);
            if (language == null) return;

            try {
                var in = Jams.class.getResourceAsStream(value);
                if (in != null) {
                    Language bundled = new Language(in);
                    in.close();
                    language.addNotPresentValues(bundled);
                    language.save();
                }
            } catch (LanguageFailedLoadException | IOException e) {
                e.printStackTrace();
            }

        });
    }

    @Listener
    private void onNodeChange(ConfigurationNodeChangeEvent.After event) {
        if (event.getNode().equals(SELECTED_LANGUAGE_NODE)) {
            get(event.getNewValue().orElse("").toString()).ifPresent(this::setSelected);
        } else if (event.getNode().equals(DEFAULT_LANGUAGE_NODE)) {
            get(event.getNewValue().orElse("").toString()).ifPresent(this::setDefault);
        }
    }

}
