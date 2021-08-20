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

import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.theme.exception.ThemeLoadException;

import java.util.List;
import java.util.Optional;

public record ThemeHeader(String name, List<String> files, List<String> dependencies) {

    public static final String NAME_NODE = "name";
    public static final String FILES_NODE = "files";
    public static final String DEPENDENCIES_NODE = "dependencies";

    public static ThemeHeader load(Configuration configuration) throws ThemeLoadException {
        var name = configuration.getString(NAME_NODE);
        if (name.isEmpty()) throw new ThemeLoadException(ThemeLoadException.Type.INVALID_HEADER);

        var files = getListOrEmpty(configuration, FILES_NODE);
        var dependencies = getListOrEmpty(configuration, DEPENDENCIES_NODE);

        return new ThemeHeader(name.get(), files, dependencies);
    }

    private static List<String> getListOrEmpty(Configuration configuration, String node) {
        Optional<List<String>> optional = configuration.get(node);
        return optional.map(List::copyOf).orElse(List.of());
    }

}
