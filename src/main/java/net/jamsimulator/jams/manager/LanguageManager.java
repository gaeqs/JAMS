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

package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.MainNodes;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.LanguageRegisterEvent;
import net.jamsimulator.jams.language.event.LanguageUnregisterEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;
import net.jamsimulator.jams.language.exception.LanguageFailedLoadException;
import net.jamsimulator.jams.utils.FolderUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This singleton stores all {@link Language}s that JAMS may use.
 * <p>
 * To register a {@link Language} use {@link #add(Labeled)}.
 * To unregister a {@link Language} use {@link #remove(Object)}.
 * <p>
 * The selected {@link Language} will be the one to be used by the GUI.
 */
public class LanguageManager extends SelectableManager<Language> {


	public static final String FOLDER_NAME = "language";
	public static final String DEFAULT_LANGUAGE_NODE = "language.default";
	public static final String SELECTED_LANGUAGE_NODE = "language.selected";

	public static final LanguageManager INSTANCE = new LanguageManager();

	private Map<String, String> bundledLanguages;
	private File folder;


	private LanguageManager() {
		super(LanguageRegisterEvent.Before::new, LanguageRegisterEvent.After::new,
				LanguageUnregisterEvent.Before::new, LanguageUnregisterEvent.After::new,
				DefaultLanguageChangeEvent.Before::new, DefaultLanguageChangeEvent.After::new,
				SelectedLanguageChangeEvent.Before::new, SelectedLanguageChangeEvent.After::new);

		Jams.getMainConfiguration().registerListeners(this, true);
	}

	@Override
	protected void loadDefaultElements() {
		loadLanguagesFolder();
		loadStoredLanguages();
		refreshBundledLanguages();
	}

	@Override
	protected Language loadDefaultElement() {
		String configDefault = Jams.getMainConfiguration()
				.getString(MainNodes.CONFIG_LANGUAGE_DEFAULT).orElse("English");

		Optional<Language> english = get("English");
		Optional<Language> defaultOptional = get(configDefault);
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
		String configSelected = Jams.getMainConfiguration()
				.getString(MainNodes.CONFIG_LANGUAGE_SELECTED).orElse("English");

		Optional<Language> english = get("English");
		Optional<Language> selectedOptional = get(configSelected);
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
		File[] files = folder.listFiles();
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
			Language language = get(key).orElse(null);
			if (language == null) return;

			try {
				Language bundled = new Language(Jams.class.getResourceAsStream(value));
				language.addNotPresentValues(bundled);
				language.save();
			} catch (LanguageFailedLoadException e) {
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
