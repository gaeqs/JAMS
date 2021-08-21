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
import net.jamsimulator.jams.utils.DisposableFileSystem;
import org.json.JSONException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class ThemeLoader {

    public static String HEADER_FILE = "theme.json";
    public static String GLOBAL_FILE = "global.css";

    private final Path path;
    private final URI uri;

    private ThemeHeader header;
    private String globalData;
    private String filesData;
    private boolean loaded;

    public ThemeLoader(Path path) {
        this.path = path;
        this.uri = path.toUri();
    }

    public boolean isLoaded() {
        return loaded;
    }

    public ThemeHeader getHeader() {
        return header;
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

    public void load() throws ThemeLoadException {
        if (loaded) throw new ThemeLoadException(ThemeLoadException.Type.ALREADY_LOADED);

        var system = loadSystem();

        header = loadHeader();
        globalData = loadGlobal();

        var builder = new StringBuilder();
        header.files().forEach(it -> builder.append(loadExtraFile(it)));
        filesData = builder.toString();

        try {
            system.close();
        } catch (IOException e) {
            throw new ThemeLoadException(e, ThemeLoadException.Type.INVALID_RESOURCE);
        }

        loaded = true;
    }

    public Theme createTheme() throws ThemeLoadException {
        if (!loaded) throw new ThemeLoadException(ThemeLoadException.Type.NOT_LOADED);
        return new Theme(header, globalData, filesData);
    }

    private DisposableFileSystem loadSystem() throws ThemeLoadException {
        try {
            return new DisposableFileSystem(uri);
        } catch (IOException ex) {
            throw new ThemeLoadException(ex, ThemeLoadException.Type.RESOURCE_NOT_FOUND);
        }
    }

    private ThemeHeader loadHeader() throws ThemeLoadException {
        var headerPath = path.resolve(HEADER_FILE);
        try {
            var json = Files.readString(headerPath);
            var config = new RootConfiguration(json);
            return ThemeHeader.load(config);
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
