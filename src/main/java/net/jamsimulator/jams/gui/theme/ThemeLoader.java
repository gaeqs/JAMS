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
import net.jamsimulator.jams.gui.theme.exception.ThemeLoadException;
import org.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.Map;

public class ThemeLoader {

    public static String HEADER_FILE = "theme.json";
    public static String GLOBAL_FILE = "global.css";

    private final URI uri;

    private ThemeHeader header;
    private String globalData;
    private String filesData;
    private boolean loaded;

    public ThemeLoader(URI uri) {
        this.uri = uri;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public URI getUri() {
        return uri;
    }

    public String getGlobalData() {
        return globalData;
    }

    public String getFilesData() {
        return filesData;
    }

    public void load() throws ThemeLoadException {
        if (loaded) throw new ThemeLoadException(ThemeLoadException.Type.ALREADY_LOADED);
        var system = loadFileSystem();
        header = loadHeader(system);
        globalData = loadGlobal(system);

        var builder = new StringBuilder();
        header.files().forEach(it -> builder.append(loadExtraFile(system, it)));
        filesData = builder.toString();

        loaded = true;
    }

    private FileSystem loadFileSystem() throws ThemeLoadException {
        if (uri == null) throw new ThemeLoadException(ThemeLoadException.Type.RESOURCE_NOT_FOUND);
        try {
            return FileSystems.newFileSystem(uri, Map.of());
        } catch (IOException e) {
            throw new ThemeLoadException(e, ThemeLoadException.Type.INVALID_RESOURCE);
        }
    }

    private ThemeHeader loadHeader(FileSystem system) throws ThemeLoadException {
        var headerPath = system.getPath(HEADER_FILE);
        try {
            var json = Files.readString(headerPath);
            var config = new RootConfiguration(json);
            return ThemeHeader.load(config);
        } catch (IOException | JSONException e) {
            throw new ThemeLoadException(e, ThemeLoadException.Type.INVALID_HEADER);
        }
    }

    private String loadGlobal(FileSystem system) {
        var globalPath = system.getPath(GLOBAL_FILE);
        try {
            return Files.readString(globalPath);
        } catch (IOException e) {
            System.err.println("Theme " + header.name() + " doesn't have a " + GLOBAL_FILE + " file.");
            return "";
        }
    }

    private String loadExtraFile(FileSystem system, String name) {
        var path = system.getPath(name);
        try {
            return Files.readString(path);
        } catch (IOException e) {
            System.err.println("Couldn't load file " + name + " from the theme" + header.name() + ".");
            return "";
        }
    }
}
