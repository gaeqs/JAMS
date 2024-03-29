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

package net.jamsimulator.jams.gui.theme;

import javafx.scene.Node;
import javafx.scene.Scene;
import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.gui.theme.event.CodeFontChangeEvent;
import net.jamsimulator.jams.gui.theme.event.GeneralFontChangeEvent;
import net.jamsimulator.jams.gui.theme.event.ThemeRefreshEvent;
import net.jamsimulator.jams.gui.theme.exception.ThemeLoadException;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.manager.SelectableManager;
import net.jamsimulator.jams.utils.FolderUtils;
import net.jamsimulator.jams.utils.TempUtils;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.ProviderNotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * This singleton stores all {@link Theme}s that projects may use.
 * <p>
 * To register a {@link Theme} use {@link #add(net.jamsimulator.jams.manager.ManagerResource)}.
 * To unregister a {@link Theme} use {@link #remove(Object)}.
 * <p>
 * The selected {@link Theme} will be the one to be used by the GUI.
 * <p>
 * The default theme will act as the common data for all themes.
 * <p>
 * You can load new themes using {@link #loadTheme(ResourceProvider, Path)}. This method will
 * load the theme at the given Path. The path can be a .ZIP file or a folder. The path may be
 * a path inside a plugin's .JAR.
 * <p>
 * The method {@link #loadThemesInDirectory(ResourceProvider, Path)} loads all the themes inside a directory.
 * Just like the previous method, the path may be a path inside a plugin's .JAR. See these methods'
 * documentation for more information.
 */
public final class ThemeManager extends SelectableManager<Theme> {

    public static final String FOLDER_NAME = "themes";
    public static final String SELECTED_THEME_NODE = "appearance.theme";
    public static final String GENERAL_FONT_NODE = "appearance.general_font";
    public static final String CODE_FONT_NODE = "appearance.code_font";
    public static final String NAME = "theme";
    public static final String COMMON_THEME = "Common";
    public static final ThemeManager INSTANCE = new ThemeManager(ResourceProvider.JAMS, NAME);

    private boolean cacheFileLoaded = false;
    private File cacheFile;
    public File folder;

    private String generalFont, codeFont;

    public ThemeManager(ResourceProvider provider, String name) {
        super(provider, name, Theme.class, true);
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
     * <p>
     * The selected theme will be refreshed automaticalle after the execution of this method.
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
     * <p>
     * The selected theme will be refreshed automaticalle after the execution of this method.
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
        if (!cacheFileLoaded) refreshFile();
        try {
            scene.getStylesheets().setAll(cacheFile.toURI().toURL().toExternalForm());
        } catch (IOException e) {
            System.err.println("Error while applying themes " + defaultValue.getName() + ", " + selected.getName() + ": ");
            e.printStackTrace();
        }
    }

    /**
     * Invokes the event {@link ThemeRefreshEvent}, calling all listeners to refresh the selected theme.
     * <p>
     * This method will be invoked automatically when the fonts, default theme or selected theme are changed.
     * <p>
     * You may invoke this method when you finish loading new attachments on runtime. If you added those attachments
     * when the manager was loading you DON'T need to invoke this method, as it will throw an exception.
     */
    public void refresh() {
        refreshFile();
        callEvent(new ThemeRefreshEvent());
    }

    /**
     * Loads all themes inside the given directory path.
     * <p>
     * You may need to refresh the selected theme after all loading operations are finished. See {@link #refresh()}
     * for more information.
     * <p>
     * This method won't throw any {@link ThemeLoadException}. Instead, it will return a {@link HashMap} with
     * all {@link ThemeLoadException} thrown by the theme loader. This decision was made for simplicity reasons.
     *
     * @param provider the provider of the themes.
     * @param path     the path of the directory where the themes are. This path may be inside a plugin's .JAR.
     * @throws IOException if there's something wrong with the given path.
     * @see #refresh()
     */
    public Map<Path, ThemeLoadException> loadThemesInDirectory(ResourceProvider provider, Path path) throws IOException {
        Validate.notNull(path, "Path cannot be null!");
        var exceptions = new HashMap<Path, ThemeLoadException>();
        Files.walk(path, 1).forEach(it -> {
            try {
                if (Files.isSameFile(path, it)) return;
                loadTheme(provider, it);
            } catch (IOException e) {
                exceptions.put(path, new ThemeLoadException(e, ThemeLoadException.Type.INVALID_RESOURCE));
            } catch (ThemeLoadException e) {
                exceptions.put(path, e);
            }
        });
        return exceptions;
    }

    /**
     * Loads the theme located at the given path. The theme may be a folder or a .ZIP file.
     * <p>
     * You may need to refresh the selected theme after all loading operations are finished. See {@link #refresh()}
     * for more information.
     *
     * @param provider the provider of the theme to load.
     * @param path     the path of the theme. This path may be inside a plugin's .JAR.
     * @throws ThemeLoadException if something went wrong while the theme is loading.
     * @see #refresh()
     */
    public void loadTheme(ResourceProvider provider, Path path) throws ThemeLoadException {
        var loader = new ThemeLoader(provider, path);
        loader.load();

        if (loader.getHeader().name().equals(COMMON_THEME)) {
            if (defaultValue == null) {
                defaultValue = new Theme(loader.getHeader());
            }
            attach(defaultValue, loader);
        } else {
            var theme = get(loader.getHeader().name()).orElse(null);
            if (theme == null) {
                theme = new Theme(loader.getHeader());
                add(theme);
            }
            attach(theme, loader);
        }
    }

    @Override
    public void load() {
        folder = new File(Jams.getMainFolder(), FOLDER_NAME);
        if (!FolderUtils.checkFolder(folder)) throw new RuntimeException("Couldn't create language folder!");

        super.load();

        Jams.getMainConfiguration().data().registerListeners(this, true);
    }

    @Override
    public boolean setDefault(Theme defaultValue) {
        if (!super.setDefault(defaultValue)) return false;
        refresh();
        return true;
    }

    @Override
    public boolean setSelected(Theme selected) {
        if (!super.setSelected(selected)) return false;
        refresh();
        return true;
    }

    @Override
    protected void loadDefaultElements() {
        cacheFile = TempUtils.createTemporalFile("currentTheme");
        loadFonts();

        try {
            var jarResource = Jams.class.getResource("/gui/theme");
            if (jarResource != null) {
                loadThemesInDirectory(ResourceProvider.JAMS, Path.of(jarResource.toURI()))
                        .forEach(ThemeManager::manageException);
            }
            loadThemesInDirectory(ResourceProvider.JAMS, folder.toPath())
                    .forEach(ThemeManager::manageException);
        } catch (IOException | URISyntaxException | ProviderNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void manageException(Path path, ThemeLoadException e) {
        System.err.println("Error while loading the theme at " + path);
        e.printStackTrace();
    }

    @Override
    protected Theme loadDefaultElement() {
        return defaultValue;
    }

    @Override
    protected Theme loadSelectedElement() {
        var config = Jams.getMainConfiguration();
        var selected = config.data().getString(SELECTED_THEME_NODE).orElse("Dark Theme");
        var theme = get(selected).orElseGet(() -> get("Dark Theme").orElse(null));
        if (theme == null) {
            System.err.println("Dark Theme not found! Using the first found theme instead.");
            theme = stream().findFirst().orElseThrow(NullPointerException::new);
        }
        return theme;
    }

    @Override
    public int removeProvidedBy(ResourceProvider provider) {
        int amount = super.removeProvidedBy(provider);

        // Let's remove the attachments too!

        boolean refresh = false;

        var iterator = iterator();
        while (iterator.hasNext()) {
            var theme = iterator.next();
            boolean u1 = theme.getGlobalAttachments().removeIf(attachment -> attachment.provider().equals(provider));
            boolean u2 = theme.getFilesAttachments().removeIf(attachment -> attachment.provider().equals(provider));

            if (theme == selected || theme.getName().equals(COMMON_THEME)) {
                refresh |= u1 || u2;
            }

            if ((u1 || u2) && theme.getFilesAttachments().isEmpty() && theme.getGlobalAttachments().isEmpty()) {
                iterator.remove();
            }
        }

        if (refresh) {
            refresh();
        }

        return amount;
    }

    private void loadFonts() {
        var config = Jams.getMainConfiguration();
        generalFont = config.data().getString(GENERAL_FONT_NODE).orElse("Noto Sans");
        codeFont = config.data().getString(CODE_FONT_NODE).orElse("JetBrains Mono");
    }

    private void attach(Theme theme, ThemeLoader attachment) {
        if (!attachment.getGlobalData().isEmpty()) {
            theme.getGlobalAttachments().add(new ThemeAttachment(attachment.getGlobalData(), attachment.getProvider()));
        }
        if (!attachment.getFilesData().isEmpty()) {
            theme.getFilesAttachments().add(new ThemeAttachment(attachment.getFilesData(), attachment.getProvider()));
        }
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
