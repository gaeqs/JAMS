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

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.language.exception.LanguageLoadException;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.utils.Validate;

import java.util.List;
import java.util.Optional;

/**
 * Represents the 'language.json' file inside a language.
 *
 * @param name     the name of the language.
 * @param files    the language's files to load. This field is optional.
 * @param provider the provider of this language.
 */
public record LanguageHeader(String name, List<String> files, ResourceProvider provider, int priority) {

    /**
     * The name field of the JSON file. This field is required.
     */
    public static final String NAME_NODE = "name";

    /**
     * The 'files' field of the JSON file. This field is optional.
     */
    public static final String FILES_NODE = "files";
    /**
     * The priority node of the JSON file. This field is optional.
     */
    public static final String PRIORITY = "priority";

    /**
     * Creates a new {@link LanguageHeader} with the information inside the given {@link Configuration}.
     *
     * @param configuration the {@link Configuration} representing the JSON file.
     * @return the {@link  LanguageHeader}.
     * @throws LanguageLoadException when something went wrong.
     */
    public static LanguageHeader load(ResourceProvider provider, Configuration configuration)
            throws LanguageLoadException {
        var name = configuration.getString(NAME_NODE);
        if (name.isEmpty()) throw new LanguageLoadException(LanguageLoadException.Type.INVALID_HEADER);

        var files = getListOrEmpty(configuration, FILES_NODE);
        int priority = configuration.getOrElse(PRIORITY, 0);

        return new LanguageHeader(name.get(), files, provider, priority);
    }

    private static List<String> getListOrEmpty(Configuration configuration, String node) {
        Optional<List<String>> optional = configuration.get(node);
        return optional.map(List::copyOf).orElse(List.of());
    }

    public LanguageHeader {
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(files, "Files cannot be null!");
        Validate.notNull(provider, "Provider cannot be null!");
    }

}
