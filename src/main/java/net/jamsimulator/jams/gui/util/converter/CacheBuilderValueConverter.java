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

package net.jamsimulator.jams.gui.util.converter;

import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;

import java.util.Optional;

public class CacheBuilderValueConverter extends ValueConverter<CacheBuilder<?>> {

    public static final String NAME = "cache_builder";

    @Override
    public String toString(CacheBuilder<?> value) {
        return value == null ? null : value.getName();
    }

    @Override
    public Optional<CacheBuilder<?>> fromStringSafe(String value) {
        return Manager.of(CacheBuilder.class).get(value).map(it -> (CacheBuilder<?>) it);
    }

    @Override
    public Class<?> conversionClass() {
        return CacheBuilder.class;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public ResourceProvider getResourceProvider() {
        return ResourceProvider.JAMS;
    }
}
