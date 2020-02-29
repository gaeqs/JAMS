package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.language.Language;
import net.jamsimulator.jams.language.event.DefaultLanguageChangeEvent;
import net.jamsimulator.jams.language.event.SelectedLanguageChangeEvent;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This singleton stores all {@link Language}s that projects may use.
 * <p>
 * To register a {@link Language} use {@link #register(Language)}.
 * To unregister a {@link Language} use {@link #unregister(String)}.
 * <p>
 * The selected {@link Language} will be the one to be used by the GUI.
 */
public class LanguageManager extends SimpleEventBroadcast {

	public static final LanguageManager INSTANCE = new LanguageManager();

	private Set<Language> languages;
	private Language defaultLanguage;
	private Language selectedLanguage;


	private LanguageManager() {
		languages = new HashSet<>();
		loadDefaultLanguages();
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
	 * Any attempt to modify this {@link Set} result in an {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link Set};
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
		return languages.add(language);
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
		if (defaultLanguage.getName().equals(name)) return false;
		if (selectedLanguage.getName().equals(name)) selectedLanguage = defaultLanguage;
		return languages.removeIf(target -> target.getName().equals(name));
	}


	private void loadDefaultLanguages() {
		Language english = new Language("English", Jams.class.getResourceAsStream("/language/english.jlang"));
		Language spanish = new Language("Spanish", Jams.class.getResourceAsStream("/language/spanish.jlang"));
		defaultLanguage = english;
		selectedLanguage = english;
		languages.add(english);
		languages.add(spanish);
	}

}
