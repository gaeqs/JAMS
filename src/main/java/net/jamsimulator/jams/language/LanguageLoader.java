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

import net.jamsimulator.jams.configuration.RootConfiguration;
import net.jamsimulator.jams.configuration.format.ConfigurationFormat;
import net.jamsimulator.jams.configuration.format.ConfigurationFormatJSON;
import net.jamsimulator.jams.configuration.format.ConfigurationFormatYAML;
import net.jamsimulator.jams.language.exception.LanguageLoadException;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ResourceProvider;
import org.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class used to load {@link Language}s.
 */
public class LanguageLoader {

    public static String HEADER_FILE = "language.json";

    private final ResourceProvider provider;
    private final Path path;

    private LanguageHeader header;
    private Map<String, String> filesData;
    private boolean loaded;

    private FileSystem usedFileSystem = null;

    /**
     * Creates and initialized the language loader.
     *
     * @param path the path of the language to load.
     * @throws LanguageLoadException whem whomething went wrong during initialization.
     */
    public LanguageLoader(ResourceProvider provider, Path path) throws LanguageLoadException {
        this.provider = provider;
        if (!Files.isDirectory(path)) {
            // .zip handle
            var uri = path.toUri();
            try {
                var newUri = new URI("jar:file", uri.getAuthority(),
                        uri.getPath() + "!/", uri.getFragment());

                usedFileSystem = FileSystems.newFileSystem(newUri, Map.of("create", "true"));
                this.path = Path.of(newUri);
            } catch (URISyntaxException | IOException | ProviderNotFoundException e) {
                throw new LanguageLoadException(e, LanguageLoadException.Type.RESOURCE_NOT_FOUND);
            }
        } else {
            this.path = path;
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public LanguageHeader getHeader() {
        return header;
    }

    public ResourceProvider getProvider() {
        return provider;
    }

    public Path getPath() {
        return path;
    }

    public Map<String, String> getFilesData() {
        return filesData;
    }

    /**
     * Loads the language data.
     *
     * @throws LanguageLoadException when something went wrong.
     */
    public void load() throws LanguageLoadException {
        if (loaded) throw new LanguageLoadException(LanguageLoadException.Type.ALREADY_LOADED);
        header = loadHeader();
        filesData = new HashMap<>();
        header.files().forEach(it -> loadExtraFile(it, filesData));
        loaded = true;
        if (usedFileSystem != null) {
            try {
                usedFileSystem.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private LanguageHeader loadHeader() throws LanguageLoadException {
        var headerPath = path.resolve(HEADER_FILE);
        try {
            var config = new RootConfiguration(
                    Files.readString(headerPath),
                    Manager.of(ConfigurationFormat.class)
                            .getOrNull(ConfigurationFormatJSON.NAME)
            );
            return LanguageHeader.load(provider, config);
        } catch (IOException | JSONException e) {
            throw new LanguageLoadException(e, LanguageLoadException.Type.INVALID_HEADER);
        }
    }

    private void loadExtraFile(String name, Map<String, String> out) {
        var filePath = path.resolve(name);
        try {
            var yaml = Manager.of(ConfigurationFormat.class).getOrNull(ConfigurationFormatYAML.NAME);
            var string = Files.readString(filePath);
            var config = new RootConfiguration(string, yaml);
            config.getAll(true).forEach((k, v) -> out.put(k, v.toString()));
        } catch (IOException e) {
            System.err.println("Couldn't load file " + name + " from the language" + header.name() + ".");
        }
    }
}
