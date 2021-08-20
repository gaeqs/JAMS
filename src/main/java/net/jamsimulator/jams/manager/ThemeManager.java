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
import net.jamsimulator.jams.gui.theme.Theme;
import net.jamsimulator.jams.gui.theme.event.*;
import net.jamsimulator.jams.utils.TempUtils;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;

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

    protected File cacheFile = TempUtils.createTemporalFile("currentTheme");
    protected boolean cacheFileLoaded = false;

    private String generalFont, codeFont;
    private File folder;


    private ThemeManager() {
        super(ThemeRegisterEvent.Before::new, ThemeRegisterEvent.After::new,
                ThemeUnregisterEvent.Before::new, ThemeUnregisterEvent.After::new,
                null, null,
                DefaultThemeChangeEvent.Before::new, DefaultThemeChangeEvent.After::new);

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

        var old = generalFont;
        var before = callEvent(new GeneralFontChangeEvent.Before(codeFont, font));
        if (before.isCancelled()) return;

        this.generalFont = before.getNewFont();
        callEvent(new GeneralFontChangeEvent.After(old, font));
        refresh();
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

        var old = codeFont;
        var before = callEvent(new CodeFontChangeEvent.Before(codeFont, font));
        if (before.isCancelled()) return;

        this.codeFont = before.getNewFont();
        callEvent(new CodeFontChangeEvent.After(old, font));
        refresh();
    }

    public void refresh() {
        callEvent(new ThemeRefreshEvent());
    }

    @Override
    protected void loadDefaultElements() {

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
