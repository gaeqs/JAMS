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

package net.jamsimulator.jams.plugin;

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.plugin.exception.InvalidPluginHeaderException;
import net.jamsimulator.jams.utils.Validate;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * This record contains the basic information about a {@link Plugin}, such as the name, version and main class.
 * <p>
 * You can create a header using a JSON file with the method {@link #loadJSON(Configuration, File)}.
 * <p>
 * Spaces inside the plugin's name will be replaced by an underscore.
 */
public final record PluginHeader(String name,
                                 String version,
                                 String mainClass,
                                 String descriptionLanguageNode,
                                 String url,
                                 String favicon,
                                 List<String> authors,
                                 List<String> dependencies,
                                 List<String> softDependencies,
                                 List<String> compatibleJAMSVersions,
                                 File file,
                                 Configuration rawData) {

    public static final String NAME_FIELD = "name";
    public static final String VERSION_FIELD = "version";
    public static final String MAIN_CLASS_FIELD = "main";
    public static final String DESCRIPTION_NODE_FIELD = "description_node";
    public static final String URL_FIELD = "url";
    public static final String FAVICON_FIELD = "favicon";
    public static final String AUTHORS_FIELD = "authors";
    public static final String DEPENDENCIES_FIELD = "dependencies";
    public static final String SOFT_DEPENDENCIES_FIELD = "soft_dependencies";
    public static final String COMPATIBLE_JAMS_VERSIONS_FIELD = "compatible_jams_versions";

    public PluginHeader {
        Validate.notNull(name, "Name cannot be null!");
        Validate.isTrue(!name.isEmpty(), "Name cannot be empty!");

        Validate.notNull(version, "Version cannot be null!");
        Validate.isTrue(!version.isEmpty(), "Name cannot be empty!");

        Validate.notNull(mainClass, "Main class cannot be null!");

        Validate.notNull(file, "File cannot be null!");
        Validate.notNull(rawData, "Raw data cannot be null!");

        if (authors == null) authors = Collections.emptyList();
        if (dependencies == null) dependencies = Collections.emptyList();
        if (softDependencies == null) softDependencies = Collections.emptyList();
        if (compatibleJAMSVersions == null) compatibleJAMSVersions = Collections.emptyList();
    }

    public static PluginHeader loadJSON(Configuration configuration, File pluginFile) throws InvalidPluginHeaderException {
        var name = configuration.getString(NAME_FIELD).orElseThrow(() ->
                new InvalidPluginHeaderException("Couldn't find name!")).replace(' ', '_');
        if (!name.matches("^[A-Za-z0-9 _.-]+$"))
            throw new InvalidPluginHeaderException("Invalid name " + name + "!");
        var version = configuration.getString(VERSION_FIELD).orElseThrow(() ->
                new InvalidPluginHeaderException("Couldn't find version!"));
        var mainClass = configuration.getString(MAIN_CLASS_FIELD).orElseThrow(() ->
                new InvalidPluginHeaderException("Couldn't find main class!"));

        var description = configuration.getString(DESCRIPTION_NODE_FIELD).orElse(null);
        var url = configuration.getString(URL_FIELD).orElse(null);
        var favicon = configuration.getString(FAVICON_FIELD).orElse(null);

        var authors = list((List<?>) configuration.get(AUTHORS_FIELD).orElseGet(Collections::emptyList));
        var dependencies = pluginList((List<?>) configuration.get(DEPENDENCIES_FIELD)
                .orElseGet(Collections::emptyList));
        var softDependencies = pluginList((List<?>) configuration.get(SOFT_DEPENDENCIES_FIELD)
                .orElseGet(Collections::emptyList));
        var compatibleJAMSVersions = list((List<?>) configuration.get(COMPATIBLE_JAMS_VERSIONS_FIELD)
                .orElseGet(Collections::emptyList));

        return new PluginHeader(name, version, mainClass, description, url, favicon, authors, dependencies,
                softDependencies, compatibleJAMSVersions, pluginFile, configuration);
    }

    private static List<String> pluginList(List<?> list) {
        return list.stream().map(Object::toString).map(v -> v.replace(' ', '_')).toList();
    }

    private static List<String> list(List<?> list) {
        return list.stream().map(Object::toString).toList();
    }
}
