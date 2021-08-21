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

import javafx.scene.Node;
import javafx.scene.Scene;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.theme.Theme;
import net.jamsimulator.jams.gui.theme.ThemeLoader;
import net.jamsimulator.jams.gui.theme.event.*;
import net.jamsimulator.jams.gui.theme.exception.ThemeLoadException;
import net.jamsimulator.jams.utils.TempUtils;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

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

    private boolean cacheFileLoaded = false;
    protected File cacheFile = TempUtils.createTemporalFile("currentTheme");

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

    /**
     * Applies the current style to the given {@link Node}.
     *
     * @param node the  {@link Node}.
     */
    public void apply(Node node) {
        node.setStyle(selected.build(this, true));
    }

    /**
     * Applies the current style to all the nodes of the given {@link Scene}.
     *
     * @param scene the {@link Scene}.
     */
    public void apply(Scene scene) {
        if(!cacheFileLoaded) refreshFile();
        try {
            scene.getStylesheets().setAll(cacheFile.toURI().toURL().toExternalForm());
        } catch (IOException e) {
            System.err.println("Error while applying themes " + defaultValue.getName() + ", " + selected.getName() + ": ");
            e.printStackTrace();
        }
    }

    public void refresh() {
        callEvent(new ThemeRefreshEvent());
    }

    @Override
    public boolean setSelected(Theme selected) {
        if (!super.setSelected(selected)) return false;
        refreshFile();
        return true;
    }

    @Override
    protected void loadDefaultElements() {
        folder = new File(Jams.getMainFolder(), FOLDER_NAME);

        loadFonts();

        try {
            loadThemesInFolder(Path.of(Jams.class.getResource("/gui/theme/").toURI()));
            loadThemesInFolder(folder.toPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Theme loadDefaultElement() {
        return get("Dark Theme").orElse(null);
    }

    @Override
    protected Theme loadSelectedElement() {
        var config = Jams.getMainConfiguration();
        var selected = config.getString(SELECTED_THEME_NODE).orElse("Dark Theme");
        var theme = get(selected).orElseGet(() -> get("Dark Theme").orElse(null));
        if (theme == null) {
            System.err.println("Dark Theme not found! Using the first found theme instead.");
            theme = stream().findFirst().orElseThrow(NullPointerException::new);
        }
        return theme;
    }

    private void loadFonts() {
        var config = Jams.getMainConfiguration();
        generalFont = config.getString(GENERAL_FONT_NODE).orElse("Noto Sans");
        codeFont = config.getString(CODE_FONT_NODE).orElse("JetBrains Mono");
    }


    public void loadThemesInFolder(Path path) {
        Validate.notNull(path, "Path cannot be null!");
        try {
            walkAndLoad(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void walkAndLoad(Path path) throws IOException {
        Files.walk(path, 1).forEach(it -> {
            try {
                if (Files.isSameFile(path, it)) return;
                var loader = new ThemeLoader(it);
                loader.load();
                add(loader.createTheme());
            } catch (IOException | ThemeLoadException e) {
                e.printStackTrace();
            }
        });
    }

    private void refreshFile() {
        try {
            var writer = new FileWriter(cacheFile);
            writer.write(selected.build(this, true));
            writer.close();
            cacheFileLoaded = true;
        } catch (IOException e) {
            System.err.println("Error while applying themes " + defaultValue.getName() + ", " + selected.getName() + ": ");
            e.printStackTrace();
        }
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
