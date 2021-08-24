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

import net.jamsimulator.jams.manager.DefaultValuableManager;
import net.jamsimulator.jams.manager.ResourceProvider;

/**
 * This singleton stores all {@link MemoryBuilder}s that projects may use.
 * <p>
 * To register an {@link MemoryBuilder} use {@link #add(net.jamsimulator.jams.manager.ManagerResource)}.
 * To unregister an {@link MemoryBuilder} use {@link #remove(Object)}.
 * An {@link MemoryBuilder}'s removal from the manager doesn't make projects
 * to stop using it if they're already using it.
 */
public final class MemoryBuilderManager extends DefaultValuableManager<MemoryBuilder> {

    public static final String NAME = "memory_builder";
    public static final MemoryBuilderManager INSTANCE = new MemoryBuilderManager(ResourceProvider.JAMS, NAME);


    public MemoryBuilderManager(ResourceProvider provider, String name) {
        super(provider, name, MemoryBuilder.class, false);
    }

    @Override
    protected void loadDefaultElements() {
        add(new MIPS32MemoryBuilder(ResourceProvider.JAMS));
    }

    @Override
    protected MemoryBuilder loadDefaultElement() {
        return get(MIPS32MemoryBuilder.NAME).orElseThrow();
    }
}
