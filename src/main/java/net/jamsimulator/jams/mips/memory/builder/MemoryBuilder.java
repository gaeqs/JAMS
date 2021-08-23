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

package net.jamsimulator.jams.mips.memory.builder;

import net.jamsimulator.jams.utils.Labeled;
import net.jamsimulator.jams.mips.assembler.builder.AssemblerBuilderManager;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

/**
 * Represents a memory builder. Memory builders are used to create several {@link Memory}
 * using the given parameters.
 * <p>
 * If a plugin wants to add a custom memory to JAMS, it should create a child of this class and register
 * it on the {@link AssemblerBuilderManager}.
 */
public abstract class MemoryBuilder implements Labeled {

    private final String name;

    /**
     * Creates a memory builder using a name.
     * This name must be unique for each memory builder.
     *
     * @param name the name.
     */
    public MemoryBuilder(String name) {
        Validate.notNull(name, "Name cannot be null!");
        this.name = name;
    }

    /**
     * Returns the name of this memory builder.
     * This name must be unique for each memory builder.
     *
     * @return the name of this memory builder.
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Creates a new {@link Memory}.
     *
     * @return the new {@link Memory}.
     */
    public abstract Memory createMemory();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemoryBuilder that = (MemoryBuilder) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
