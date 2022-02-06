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

import net.jamsimulator.jams.manager.ManagerResource;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.utils.Validate;

import java.util.Map;
import java.util.Objects;

public abstract class ConfigurationFormat implements ManagerResource {

    protected final ResourceProvider resourceProvider;
    protected final String name;

    public ConfigurationFormat(ResourceProvider resourceProvider, String name) {
        Validate.notNull(resourceProvider, "Resource provider cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
        this.resourceProvider = resourceProvider;
        this.name = name;
    }

    @Override
    public ResourceProvider getResourceProvider() {
        return resourceProvider;
    }

    @Override
    public String getName() {
        return name;
    }

    public abstract Map<String, Object> deserialize(String string);

    public abstract String serialize(Map<String, ?> data, boolean prettyOutput);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigurationFormat that = (ConfigurationFormat) o;
        return resourceProvider.equals(that.resourceProvider) && name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceProvider, name);
    }

    @Override
    public String toString() {
        return "ConfigurationFormat{" +
                "resourceProvider=" + resourceProvider +
                ", name='" + name + '\'' +
                '}';
    }
}
