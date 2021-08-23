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

package net.jamsimulator.jams.mips.memory.cache;

import javafx.beans.property.Property;
import net.jamsimulator.jams.utils.Labeled;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class CacheBuilder<C extends Cache> implements Labeled {

    protected final String name;
    protected final List<Property<?>> properties;

    public CacheBuilder(String name, List<Property<?>> properties) {
        Validate.notNull(name, "Name cannot be null!");
        this.name = name;
        this.properties = properties == null ? Collections.emptyList() : properties;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getLanguageNode() {
        return "CACHE_" + name;
    }

    public List<Property<?>> getProperties() {
        return properties;
    }

    public abstract int getSizeInBytes();

    public abstract C build(Memory parent);

    public abstract CacheBuilder<C> makeNewInstance();

    public abstract CacheBuilder<C> copy();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheBuilder<?> that = (CacheBuilder<?>) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
