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
import net.jamsimulator.jams.configuration.event.ConfigurationNodeChangeEvent;
import net.jamsimulator.jams.event.Listener;
import net.jamsimulator.jams.language.event.LanguageRefreshEvent;
import net.jamsimulator.jams.language.exception.LanguageLoadException;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.manager.SelectableManager;
import net.jamsimulator.jams.utils.FolderUtils;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * This singleton stores all {@link Language}s that JAMS may use.
 * <p>
 * To register a {@link Language} use {@link #add(net.jamsimulator.jams.manager.ManagerResource)}.
 * To unregister a {@link Language} use {@link #remove(Object)}.
 * <p>
 * The selected {@link Language} will be the one to be used by the GUI.
 */
public final class LanguageManager extends SelectableManager<Language> {

    public static final String FOLDER_NAME = "languages";
    public static final String DEFAULT_LANGUAGE_NODE = "language.default";
    public static final String SELECTED_LANGUAGE_NODE = "language.selected";
    public static final String NAME = "language";

    public static final LanguageManager INSTANCE = new LanguageManager(ResourceProvider.JAMS, NAME);

    public File folder;

    public LanguageManager(ResourceProvider provider, String name) {
        super(provider, name, Language.class, false);
    }

    /**
     * Invokes the event {@link LanguageRefreshEvent}, calling all listeners to refresh the selected language.
     * <p>
     * This method will be invoked automatically when the default or selected languages are changed.
     * <p>
     * You may invoke this method when you finish loading new attachments on runtime. If you added those attachments
     * when the manager was loading you DON'T need to invoke this method, as it will throw an exception.
     */
    public void refresh() {
        callEvent(new LanguageRefreshEvent(selected, defaultValue));
    }

    /**
     * Loads all languages inside the given directory path.
     * <p>
     * If 'attach' is true and some of these languages have the same name as one of the languages already loaded,
     * the new languages will be considered an attachment to the already loaded languages. If 'attach' is false and
     * one of these cases occurs, a {@link LanguageLoadException} with the type
     * {@link LanguageLoadException.Type#ALREADY_EXIST} will be thrown.
     * <p>
     * You may need to refresh the selected language after all loading operations are finished. See {@link #refresh()}
     * for more information.
     * <p>
     * This method won't throw any {@link LanguageLoadException}. Instead, it will return a {@link HashMap} with
     * all {@link LanguageLoadException} thrown by the language loader. This decision was made for simplicity reasons.
     *
     * @param provider the provider of the language.
     * @param path     the path of the directory where the languages are. This path may be inside a plugin's .JAR.
     * @param attach   whether the languages should be attached to already loaded languages.
     * @throws IOException if there's something wrong with the given path.
     * @see #refresh()
     */
    public Map<Path, LanguageLoadException> loadLanguagesInDirectory(
            ResourceProvider provider, Path path, boolean attach) throws IOException {
        Validate.notNull(path, "Path cannot be null!");
        var exceptions = new HashMap<Path, LanguageLoadException>();
        Files.walk(path, 1).forEach(it -> {
            try {
                if (Files.isSameFile(path, it)) return;
                loadLanguage(provider, it, attach);
            } catch (IOException e) {
                exceptions.put(path, new LanguageLoadException(e, LanguageLoadException.Type.INVALID_RESOURCE));
            } catch (LanguageLoadException e) {
                exceptions.put(path, e);
            }
        });
        return exceptions;
    }

    /**
     * Loads the language located at the given path. The language may be a folder or a .ZIP file.
     * <p>
     * If 'attach' is true and some the language have the same name as one of the languages already loaded,
     * the new language will be considered an attachment to the already loaded language. If 'attach' is false and
     * one of these cases occurs, a {@link LanguageLoadException} with the type
     * {@link LanguageLoadException.Type#ALREADY_EXIST} will be thrown.
     * <p>
     * You may need to refresh the selected language after all loading operations are finished. See {@link #refresh()}
     * for more information.
     *
     * @param provider the provider of the language to load.
     * @param path     the path of the language. This path may be inside a plugin's .JAR.
     * @param attach   whether the language should be attached to an already loaded language with the same name.
     * @throws LanguageLoadException if something went wrong while the language is loading.
     * @see #refresh()
     */
    public void loadLanguage(ResourceProvider provider, Path path, boolean attach) throws LanguageLoadException {
        var loader = new LanguageLoader(provider, path);
        loader.load();


        var optional = get(loader.getHeader().name());
        if (optional.isPresent()) {
            if (!attach) throw new LanguageLoadException(LanguageLoadException.Type.ALREADY_EXIST);
            attach(optional.get(), loader);
        } else {
            add(loader.createLanguage());
        }
    }

    @Override
    public void load() {
        folder = new File(Jams.getMainFolder(), FOLDER_NAME);
        if (!FolderUtils.checkFolder(folder)) throw new RuntimeException("Couldn't create language folder!");

        super.load();
        Jams.getMainConfiguration()
                .registerListeners(this, true);
    }

    @Override
    public boolean setDefault(Language defaultValue) {
        if (!super.setDefault(defaultValue)) return false;
        refresh();
        return true;
    }

    @Override
    public boolean setSelected(Language selected) {
        if (!super.setSelected(selected)) return false;
        refresh();
        return true;
    }

    @Override
    protected void loadDefaultElements() {
        try {
            var jarResource = Jams.class.getResource("/language");
            if (jarResource != null) {
                loadLanguagesInDirectory(
                        ResourceProvider.JAMS,
                        Path.of(jarResource.toURI()),
                        true
                ).forEach(LanguageManager::manageException);
            }
            loadLanguagesInDirectory(
                    ResourceProvider.JAMS,
                    folder.toPath(),
                    true
            ).forEach(LanguageManager::manageException);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private static void manageException(Path path, LanguageLoadException e) {
        System.err.println("Error while loading the language at " + path);
        e.printStackTrace();
    }

    @Override
    protected Language loadDefaultElement() {
        var config = Jams.getMainConfiguration();
        var selected = config.getString(DEFAULT_LANGUAGE_NODE)
                .orElse("English");
        var language = get(selected).orElseGet(() ->
                get("English").orElse(null));
        if (language == null) {
            System.err.println("English not found! Using the first found language instead.");
            language = stream().findFirst().orElseThrow(NullPointerException::new);
        }
        return language;
    }

    @Override
    protected Language loadSelectedElement() {
        var config = Jams.getMainConfiguration();
        var selected = config.getString(SELECTED_LANGUAGE_NODE)
                .orElse("English");
        var language = get(selected).orElseGet(() ->
                get("English").orElse(null));
        if (language == null) {
            System.err.println("English not found! Using the default language instead.");
            language = stream().findFirst().orElseThrow(NullPointerException::new);
        }
        return language;
    }

    @Override
    public int removeProvidedBy(ResourceProvider provider) {
        int amount = super.removeProvidedBy(provider);

        // Let's remove the attachments too!

        boolean refresh = false;
        for (var language : this) {
            boolean bool = language.removeAttachmentsOf(provider);
            if (language == selected || language == defaultValue) {
                refresh |= bool;
            }
        }

        if (refresh) {
            refresh();
        }

        return amount;
    }

    private void attach(Language language, LanguageLoader attachment) {
        if (!attachment.getFilesData().isEmpty()) {
            language.addAttachment(new LanguageAttachment(attachment.getProvider(),
                    attachment.getFilesData(), attachment.getHeader().priority()));
        }
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
