/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.configuration.format;

import net.jamsimulator.jams.manager.ResourceProvider;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class ConfigurationFormatYAML extends ConfigurationFormat {

    public static final String NAME = "jaml";
    public static final ConfigurationFormatYAML INSTANCE = new ConfigurationFormatYAML(ResourceProvider.JAMS, NAME);

    public ConfigurationFormatYAML(ResourceProvider resourceProvider, String name) {
        super(resourceProvider, name);
    }

    @Override
    public Map<String, Object> deserialize(String string) {
        return new Yaml().load(string);
    }

    @Override
    public String serialize(Map<String, ?> data, boolean prettyOutput) {
        return new Yaml().dump(data);
    }
}
