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

package net.jamsimulator.jams.mips.memory.cache.builder;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.CacheReplacementPolicy;
import net.jamsimulator.jams.mips.memory.cache.writeback.WriteBackAssociativeCache;
import net.jamsimulator.jams.mips.memory.cache.writethrough.WriteThroughAssociativeCache;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.LinkedList;

public class AssociativeCacheBuilder extends CacheBuilder<Cache> {

    public static final String NAME = "ASSOCIATIVE";

    private final SimpleBooleanProperty writeBack;
    private final SimpleIntegerProperty blockSize, blocksAmount;
    private final SimpleObjectProperty<CacheReplacementPolicy> replacementPolicy;

    public AssociativeCacheBuilder(ResourceProvider provider) {
        super(provider, NAME, new LinkedList<>());
        properties.add(writeBack = new SimpleBooleanProperty(null, "WRITE_BACK", false));
        properties.add(blockSize = new SimpleIntegerProperty(null, "BLOCK_SIZE", 4));
        properties.add(blocksAmount = new SimpleIntegerProperty(null, "BLOCKS_AMOUNT", 4));
        properties.add(replacementPolicy = new SimpleObjectProperty<>(null, "REPLACEMENT_POLICY", CacheReplacementPolicy.LRU));
    }

    @Override
    public int getSizeInBytes() {
        return blocksAmount.get() * (blockSize.get() << 2);
    }

    @Override
    public Cache build(Memory parent) {
        int logSize = NumericUtils.log2(blockSize.get());
        int logAmount = NumericUtils.log2(blocksAmount.get());

        if (logSize + logAmount > 32) {
            if (logSize > 32) {
                logSize = 32;
                logAmount = 0;
            } else {
                logAmount = 32 - logSize;
            }
        }

        return writeBack.get()
                ? new WriteBackAssociativeCache(this, parent, 1 << logSize, 1 << logAmount, replacementPolicy.get())
                : new WriteThroughAssociativeCache(this, parent, 1 << logSize, 1 << logAmount, replacementPolicy.get());
    }

    @Override
    public CacheBuilder<Cache> makeNewInstance() {
        return new AssociativeCacheBuilder(provider);
    }

    @Override
    public CacheBuilder<Cache> copy() {
        var builder = new AssociativeCacheBuilder(provider);
        builder.writeBack.setValue(writeBack.getValue());
        builder.blockSize.setValue(blockSize.getValue());
        builder.blocksAmount.setValue(blocksAmount.getValue());
        builder.replacementPolicy.setValue(replacementPolicy.getValue());
        return builder;
    }
}
