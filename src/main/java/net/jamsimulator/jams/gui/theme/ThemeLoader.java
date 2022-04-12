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

import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.configuration.format.ConfigurationFormat;
import net.jamsimulator.jams.configuration.format.ConfigurationFormatJSON;
import net.jamsimulator.jams.gui.theme.exception.ThemeLoadException;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ResourceProvider;
import org.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Helper class used to load {@link Theme}s.
 */
public class ThemeLoader {

    public static String HEADER_FILE = "theme.json";
    public static String GLOBAL_FILE = "global.css";

    private final ResourceProvider provider;
    private final Path path;

    private ThemeHeader header;
    private String globalData;
    private String filesData;
    private boolean loaded;

    private FileSystem usedFileSystem = null;

    /**
     * Creates and initialized the theme loader.
     *
     * @param path the path of the theme to load.
     * @throws ThemeLoadException whem whomething went wrong during initialization.
     */
    public ThemeLoader(ResourceProvider provider, Path path) throws ThemeLoadException {
        this.provider = provider;
        if (!Files.isDirectory(path)) {
            // .zip handle
            var uri = path.toUri();
            try {
                var newUri = new URI("jar:file", uri.getAuthority(),
                        uri.getPath() + "!/", uri.getFragment());

                usedFileSystem = FileSystems.newFileSystem(newUri, Map.of("create", "true"));
                this.path = Path.of(newUri);
            } catch (URISyntaxException | IOException e) {
                throw new ThemeLoadException(e, ThemeLoadException.Type.RESOURCE_NOT_FOUND);
            }
        } else {
            this.path = path;
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public ThemeHeader getHeader() {
        return header;
    }

    public ResourceProvider getProvider() {
        return provider;
    }

    public Path getPath() {
        return path;
    }

    public String getGlobalData() {
        return globalData;
    }

    public String getFilesData() {
        return filesData;
    }

    /**
     * Loads the theme data.
     *
     * @throws ThemeLoadException when something went wrong.
     */
    public void load() throws ThemeLoadException {
        if (loaded) throw new ThemeLoadException(ThemeLoadException.Type.ALREADY_LOADED);
        header = loadHeader();
        globalData = loadGlobal();
        var builder = new StringBuilder();
        header.files().forEach(it -> builder.append(loadExtraFile(it)));
        filesData = builder.toString();
        loaded = true;
        if (usedFileSystem != null) {
            try {
                usedFileSystem.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ThemeHeader loadHeader() throws ThemeLoadException {
        var headerPath = path.resolve(HEADER_FILE);
        try {
            var config = new RootConfiguration(
                    Files.readString(headerPath),
                    Manager.of(ConfigurationFormat.class).getOrNull(ConfigurationFormatJSON.NAME)
            );
            return ThemeHeader.load(provider, config);
        } catch (IOException | JSONException e) {
            throw new ThemeLoadException(e, ThemeLoadException.Type.INVALID_HEADER);
        }
    }

    private String loadGlobal() {
        var globalPath = path.resolve(GLOBAL_FILE);
        try {
            return Files.readString(globalPath);
        } catch (IOException e) {
            System.err.println("Theme " + header.name() + " doesn't have a " + GLOBAL_FILE + " file.");
            return "";
        }
    }

    private String loadExtraFile(String name) {
        var filePath = path.resolve(name);
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            System.err.println("Couldn't load file " + name + " from the theme" + header.name() + ".");
            return "";
        }
    }
}
