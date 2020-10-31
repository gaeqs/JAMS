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
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.configuration.MainNodes;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.theme.Theme;
import net.jamsimulator.jams.gui.theme.event.*;
import net.jamsimulator.jams.gui.theme.exception.ThemeFailedLoadException;
import net.jamsimulator.jams.utils.FolderUtils;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.Optional;

/**
 * This singleton stores all {@link Theme}s that projects may use.
 * <p>
 * To register a {@link Theme} use {@link #add(Labeled)}.
 * To unregister a {@link Theme} use {@link #remove(Object)}.
 * <p>
 * The selected {@link Theme} will be the one to be used by the GUI.
 */
public class ThemeManager extends SelectableManager<Theme> {

	public static final String FOLDER_NAME = "theme";
	public static final String SELECTED_THEME_NODE = "appearance.theme";
	public static final String GENERAL_FONT_NODE = "appearance.general_font";
	public static final String CODE_FONT_NODE = "appearance.code_font";

	public static final ThemeManager INSTANCE = new ThemeManager();

	private String generalFont, codeFont;
	private File folder;


	private ThemeManager() {
		super(ThemeRegisterEvent.Before::new, ThemeRegisterEvent.After::new,
				ThemeUnregisterEvent.Before::new, ThemeUnregisterEvent.After::new,
				DefaultThemeChangeEvent.Before::new, DefaultThemeChangeEvent.After::new,
				SelectedThemeChangeEvent.Before::new, SelectedThemeChangeEvent.After::new);

		loadFonts();
		Jams.getMainConfiguration().registerListeners(this, true);
	}

	/**
	 * Returns the font used for general purposes.
	 *
	 * @return the font.
	 */
	public String getGeneralFont() {
		return generalFont;
	}


	/**
	 * Sets the font used for general purposes.
	 *
	 * @param font the font.
	 */
	public void setGeneralFont(String font) {
		Validate.notNull(font, "Font cannot be null!");

		String old = generalFont;
		GeneralFontChangeEvent.Before before = callEvent(new GeneralFontChangeEvent.Before(codeFont, font));
		if (before.isCancelled()) return;

		this.generalFont = before.getNewFont();
		callEvent(new GeneralFontChangeEvent.After(old, font));
	}

	/**
	 * Returns the font used on the code editor.
	 *
	 * @return the font.
	 */
	public String getCodeFont() {
		return codeFont;
	}

	/**
	 * Sets the font used on the code editor.
	 *
	 * @param font the font.
	 */
	public void setCodeFont(String font) {
		Validate.notNull(font, "Font cannot be null!");

		String old = codeFont;
		CodeFontChangeEvent.Before before = callEvent(new CodeFontChangeEvent.Before(codeFont, font));
		if (before.isCancelled()) return;

		this.codeFont = before.getNewFont();
		callEvent(new CodeFontChangeEvent.After(old, font));
	}

	@Override
	protected void loadDefaultElements() {
		loadBundledThemes();
		loadThemesFolder();
		loadStoredThemes();
	}

	@Override
	protected Theme loadDefaultElement() {
		Optional<Theme> darkOptional = get("Dark Theme");
		if (darkOptional.isEmpty()) {
			System.err.println("Dark Theme not found! Using the first found theme instead.");
			return stream().findFirst().orElseThrow(NullPointerException::new);
		} else return darkOptional.get();
	}

	@Override
	protected Theme loadSelectedElement() {
		Configuration config = Jams.getMainConfiguration();
		String configSelected = config.getString(MainNodes.CONFIG_APPEARANCE_THEME).orElse("Dark Theme");

		Optional<Theme> darkOptional = get("Dark Theme");
		Optional<Theme> selectedOptional = get(configSelected);
		if (selectedOptional.isEmpty()) {
			System.err.println("Selected theme " + configSelected + " not found. Using Dark Theme instead.");
			if (darkOptional.isEmpty()) {
				System.err.println("Dark Theme not found! Using the first found theme instead.");
				return stream().findFirst().orElseThrow(NullPointerException::new);
			} else return darkOptional.get();
		} else return selectedOptional.get();
	}

	private void loadThemesFolder() {
		folder = new File(Jams.getMainFolder(), FOLDER_NAME);
		FolderUtils.checkFolder(folder);
	}

	private void loadBundledThemes() {
		String[] bundledThemes = {"/gui/theme/dark_theme.jtheme", "/gui/theme/light_theme.jtheme"};
		for (String bundledTheme : bundledThemes) {
			try {
				add(new Theme(Jams.class.getResourceAsStream(bundledTheme)));
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
				add(new Theme(file.toPath()));
			} catch (ThemeFailedLoadException ex) {
				System.err.println("Failed to load theme " + file.getName() + ": ");
				ex.printStackTrace();
			}
		}

		if (isEmpty()) throw new NullPointerException("There's no themes!");
	}

	private void loadFonts() {
		Configuration config = Jams.getMainConfiguration();
		generalFont = config.getString(GENERAL_FONT_NODE).orElse("Noto Sans");
		codeFont = config.getString(CODE_FONT_NODE).orElse("JetBrains Mono");
	}

	@Listener
	private void onNodeChange(ConfigurationNodeChangeEvent.After event) {
		switch (event.getNode()) {
			case SELECTED_THEME_NODE -> get(event.getNewValue().orElse("").toString()).ifPresent(this::setSelected);
			case GENERAL_FONT_NODE -> event.getNewValue().ifPresent(target -> setGeneralFont(target.toString()));
			case CODE_FONT_NODE -> event.getNewValue().ifPresent(target -> setCodeFont(target.toString()));
		}
	}

}
