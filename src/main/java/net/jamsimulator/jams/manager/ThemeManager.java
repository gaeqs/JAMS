package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.configuration.MainNodes;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.gui.theme.Theme;
import net.jamsimulator.jams.gui.theme.event.SelectedThemeChangeEvent;
import net.jamsimulator.jams.gui.theme.event.ThemeRegisterEvent;
import net.jamsimulator.jams.gui.theme.event.ThemeUnregisterEvent;
import net.jamsimulator.jams.gui.theme.exception.ThemeFailedLoadException;
import net.jamsimulator.jams.utils.FolderUtils;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * This singleton stores all {@link Theme}s that projects may use.
 * <p>
 * To register a {@link Theme} use {@link #register(Theme)}.
 * To unregister a {@link Theme} use {@link #unregister(String)}.
 * <p>
 * The selected {@link Theme} will be the one to be used by the GUI.
 */
public class ThemeManager extends SimpleEventBroadcast {

	private static final String FOLDER_NAME = "theme";

	public static final ThemeManager INSTANCE = new ThemeManager();

	private final Set<String> bundledThemes;

	private final Set<Theme> themes;
	private Theme selectedTheme;

	private File folder;


	private ThemeManager() {
		themes = new HashSet<>();

		bundledThemes = new HashSet<>();
		bundledThemes.add("/gui/theme/dark_theme.jtheme");
		bundledThemes.add("/gui/theme/light_theme.jtheme");

		loadBundledThemes();
		loadThemesFolder();
		loadStoredThemes();
		assignSelectedAndDefaultTheme();
	}

	/**
	 * Returns the current {@link Theme}. This is the {@link Theme} that will be used.
	 * <p>
	 *
	 * @return the current {@link Theme}.
	 */
	public Theme getSelected() {
		return selectedTheme;
	}

	/**
	 * Attempts to set the registered {@link Theme} that matches the given name
	 * as the selected {@link Theme}. This will fail if there's no {@link Theme}
	 * that matches the name.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean setSelected(String name) {
		Optional<Theme> optional = get(name);
		if (!optional.isPresent()) return false;

		Theme old = selectedTheme;
		SelectedThemeChangeEvent.Before before = callEvent(new SelectedThemeChangeEvent.Before(old, optional.get()));
		if (before.isCancelled()) return false;
		selectedTheme = optional.get();

		callEvent(new SelectedThemeChangeEvent.After(old, selectedTheme));
		return true;
	}

	/**
	 * Returns the {@link Theme} that matches the given name, if present.
	 *
	 * @param name the given name.
	 * @return the {@link Theme}, if present.
	 */
	public Optional<Theme> get(String name) {
		return themes.stream().filter(target -> target.getName().equalsIgnoreCase(name)).findFirst();
	}

	/**
	 * Returns a unmodifiable {@link Set} with all {@link Theme}s
	 * registered in this manager.
	 * <p>
	 * Any attempt to modify this {@link Set} result in an {@link UnsupportedOperationException}.
	 *
	 * @return the unmodifiable {@link Set};
	 * @see Collections#unmodifiableSet(Set)
	 */
	public Set<Theme> getAll() {
		return Collections.unmodifiableSet(themes);
	}

	/**
	 * Attempts to register the given {@link Theme} into the manager.
	 * This will fail if a {@link Theme} with the same name already exists within this manager.
	 *
	 * @param theme the Theme to register.
	 * @return whether the Theme was registered.
	 */
	public boolean register(Theme theme) {
		Validate.notNull(theme, "Theme cannot be null!");

		ThemeRegisterEvent.Before before = callEvent(new ThemeRegisterEvent.Before(theme));
		if (before.isCancelled()) return false;
		boolean result = themes.add(theme);
		if (result) callEvent(new ThemeRegisterEvent.After(theme));
		return result;
	}

	/**
	 * Attempts to unregisters the {@link Theme} that matches the given name.
	 * This will fail if the {@link Theme} to unregister is the default one.
	 * <p>
	 * If the {@link Theme} to unregister is the selected {@link Theme}
	 * the new selected {@link Theme} will be the first one.
	 * <p>
	 * The {@link Theme} won't be unregistered if it's the only registered {@link Theme}.
	 * The event {@link ThemeUnregisterEvent.Before} will be called before this check is executed.
	 *
	 * @param name the given name.
	 * @return whether the operation was successful.
	 */
	public boolean unregister(String name) {
		Validate.notNull(name, "Name cannot be null!");
		Theme theme = get(name).orElse(null);
		if (theme == null) return false;

		ThemeUnregisterEvent.Before before = callEvent(new ThemeUnregisterEvent.Before(theme));
		if (before.isCancelled()) return false;

		if (themes.size() == 1) return false;
		if (selectedTheme.getName().equals(name)) selectedTheme = themes.stream().findAny().get();
		boolean result = themes.remove(theme);
		if (result) callEvent(new ThemeUnregisterEvent.After(theme));
		return result;
	}

	private void loadThemesFolder() {
		folder = new File(Jams.getMainFolder(), FOLDER_NAME);
		FolderUtils.checkFolder(folder);
	}

	private void loadBundledThemes() {
		for (String bundledTheme : bundledThemes) {
			try {
				themes.add(new Theme(Jams.class.getResourceAsStream(bundledTheme)));
			} catch (ThemeFailedLoadException e) {
				System.err.println("Failed to load theme " + bundledTheme + ": ");
				e.printStackTrace();
			}
		}
	}

	private void loadStoredThemes() {
		File[] files = folder.listFiles();
		if (files == null) throw new NullPointerException("There's no themes!");

		for (File file : files) {
			if (!file.getName().toLowerCase().endsWith(".jtheme")) continue;

			try {
				themes.add(new Theme(file.toPath()));
			} catch (ThemeFailedLoadException ex) {
				System.err.println("Failed to load theme " + file.getName() + ": ");
				ex.printStackTrace();
			}
		}

		if (themes.isEmpty()) throw new NullPointerException("There's no themes!");
	}

	private void assignSelectedAndDefaultTheme() {
		Configuration config = Jams.getMainConfiguration();
		String configSelected = config.getString(MainNodes.CONFIG_APPEARANCE_THEME).orElse("Dark Theme");

		Optional<Theme> darkOptional = get("Dark Theme");
		Optional<Theme> selectedOptional = get(configSelected);
		if (!selectedOptional.isPresent()) {
			System.err.println("Selected theme " + configSelected + " not found. Using Dark Theme instead.");
			if (!darkOptional.isPresent()) {
				System.err.println("Dark Theme not found! Using the first found theme instead.");
				selectedTheme = themes.stream().findFirst().get();
			} else selectedTheme = darkOptional.get();
		} else selectedTheme = selectedOptional.get();

	}

}