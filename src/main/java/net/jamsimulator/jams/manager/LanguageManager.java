package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.configuration.MainNodes;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.LanguageRegisterEvent;
import net.jamsimulator.jams.language.event.LanguageUnregisterEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;
import net.jamsimulator.jams.language.exception.LanguageFailedLoadException;
import net.jamsimulator.jams.utils.FolderUtils;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.*;

/**
 * This singleton stores all {@link Language}s that JAMS may use.
 * <p>
 * To register a {@link Language} use {@link #register(Language)}.
 * To unregister a {@link Language} use {@link #unregister(String)}.
 * <p>
 * The selected {@link Language} will be the one to be used by the GUI.
 */
public class LanguageManager extends SimpleEventBroadcast {


	private static final String FOLDER_NAME = "language";

	public static final LanguageManager INSTANCE = new LanguageManager();

	private final Map<String, String> bundledLanguages;

	private final Set<Language> languages;
	private Language defaultLanguage;
	private Language selectedLanguage;

	private File folder;


	private LanguageManager() {
		languages = new HashSet<>();

		bundledLanguages = new HashMap<>();
		bundledLanguages.put("English", "/language/english.jlang");
		bundledLanguages.put("Spanish", "/language/spanish.jlang");

		loadLanguagesFolder();
		loadStoredLanguages();
		refreshBundledLanguages();
		assignSelectedAndDefaultLanguage();
	}

	/**
	 * Returns the default {@link Language} of this manager.
	 * This will be by default the English language.
	 * <p>
	 * The default {@link Language} will be used when no node is found
	 * in the selected language.
	 *
	 * @return the default {@link Language}.
	 */
	public Language getDefault() {
		return defaultLanguage;
	}

	/**
	 * Attempts to set the registered {@link Language} that matches the given name
	 * as the default {@link Language}. This will fail if there's no {@link Language}
	 * that matches the name.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean setDefault(String name) {
		Optional<Language> optional = get(name);
		if (!optional.isPresent()) return false;

		Language old = defaultLanguage;
		DefaultLanguageChangeEvent.Before before = callEvent(new DefaultLanguageChangeEvent.Before(old, optional.get()));
		if (before.isCancelled()) return false;
		defaultLanguage = optional.get();

		callEvent(new DefaultLanguageChangeEvent.After(old, defaultLanguage));
		return true;
	}

	/**
	 * Returns the current {@link Language}. This is the {@link Language} that
	 * will be used to show messages to the user.
	 * <p>
	 * If the node to search is not found in the selected {@link Language}, the default
	 * {@link Language} will be used instead.
	 *
	 * @return the current {@link Language}.
	 * @see #getDefault()
	 */
	public Language getSelected() {
		return selectedLanguage;
	}

	/**
	 * Attempts to set the registered {@link Language} that matches the given name
	 * as the selected {@link Language}. This will fail if there's no {@link Language}
	 * that matches the name.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean setSelected(String name) {
		Optional<Language> optional = get(name);
		if (!optional.isPresent()) return false;

		Language old = selectedLanguage;
		SelectedLanguageChangeEvent.Before before = callEvent(new SelectedLanguageChangeEvent.Before(old, optional.get()));
		if (before.isCancelled()) return false;
		selectedLanguage = optional.get();

		callEvent(new SelectedLanguageChangeEvent.After(old, selectedLanguage));
		return true;
	}

	/**
	 * Returns the {@link Language} that matches the given name, if present.
	 *
	 * @param name the given name.
	 * @return the {@link Language}, if present.
	 */
	public Optional<Language> get(String name) {
		return languages.stream().filter(target -> target.getName().equalsIgnoreCase(name)).findFirst();
	}

	/**
	 * Returns a unmodifiable {@link Set} with all {@link Language}s
	 * registered in this manager.
	 * <p>
	 * Any attempt to modify this {@link Set} results in an {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link Set}.
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<Language> getAll() {
		return Collections.unmodifiableSet(languages);
	}

	/**
	 * Attempts to register the given {@link Language} into the manager.
	 * This will fail if a {@link Language} with the same name already exists within this manager.
	 *
	 * @param language the language to register.
	 * @return whether the language was registered.
	 */
	public boolean register(Language language) {
		Validate.notNull(language, "Language cannot be null!");

		LanguageRegisterEvent.Before before = callEvent(new LanguageRegisterEvent.Before(language));
		if (before.isCancelled()) return false;
		boolean result = languages.add(language);
		if (result) callEvent(new LanguageRegisterEvent.After(language));
		return result;
	}

	/**
	 * Attempts to unregisters the {@link Language} that matches the given name.
	 * This will fail if the {@link Language} to unregister is the default one.
	 * <p>
	 * If the {@link Language} to unregister is the selected {@link Language}
	 * the new selected {@link Language} will be the default one.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean unregister(String name) {
		Validate.notNull(name, "Name cannot be null!");
		Language language = get(name).orElse(null);
		if (language == null) return false;

		LanguageUnregisterEvent.Before before = callEvent(new LanguageUnregisterEvent.Before(language));
		if (before.isCancelled()) return false;

		if (defaultLanguage.getName().equals(name)) return false;
		if (selectedLanguage.getName().equals(name)) selectedLanguage = defaultLanguage;
		boolean result = languages.remove(language);
		if (result) callEvent(new LanguageUnregisterEvent.After(language));
		return result;
	}

	private void loadLanguagesFolder() {
		folder = new File(Jams.getMainFolder(), FOLDER_NAME);
		FolderUtils.checkFolder(folder);

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
				languages.add(new Language(file));
			} catch (LanguageFailedLoadException ex) {
				System.err.println("Failed to load language " + file.getName() + ": ");
				ex.printStackTrace();
			}
		}

		if (languages.isEmpty()) throw new NullPointerException("There's no languages!");
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

	private void assignSelectedAndDefaultLanguage() {
		Configuration config = Jams.getMainConfiguration();
		String configDefault = config.getString(MainNodes.CONFIG_LANGUAGE_DEFAULT).orElse("English");
		String configSelected = config.getString(MainNodes.CONFIG_LANGUAGE_SELECTED).orElse("English");

		Optional<Language> english = get("English");
		Optional<Language> defaultOptional = get(configDefault);
		if (!defaultOptional.isPresent()) {
			System.err.println("Default language " + configDefault + " not found. Using English instead.");
			if (!english.isPresent()) {
				System.err.println("English language not found! Using the first found language instead.");
				defaultLanguage = languages.stream().findFirst().get();
			} else defaultLanguage = english.get();
		} else defaultLanguage = defaultOptional.get();

		Optional<Language> selectedOptional = get(configSelected);
		if (!selectedOptional.isPresent()) {
			System.err.println("Selected language " + configSelected + " not found. Using English instead.");
			if (!english.isPresent()) {
				System.err.println("English language not found! Using the first found language instead.");
				selectedLanguage = languages.stream().findFirst().get();
			} else selectedLanguage = english.get();
		} else selectedLanguage = selectedOptional.get();

	}

}
