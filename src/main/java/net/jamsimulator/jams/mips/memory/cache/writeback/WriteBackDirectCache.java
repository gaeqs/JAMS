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

package net.jamsimulator.jams.mips.memory.cache.writeback;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.event.CacheOperationEvent;
import net.jamsimulator.jams.utils.NumericUtils;

public class WriteBackDirectCache extends WriteBackCache {

    protected final int indexShift, indexMask;

    public WriteBackDirectCache(CacheBuilder<?> builder, Memory parent, int blockSize, int blocksAmount) {
        super(builder, parent, blockSize, blocksAmount, 32 - 2 - NumericUtils.log2(blockSize) - NumericUtils.log2(blocksAmount));
        this.indexShift = 2 + NumericUtils.log2(blockSize);
        this.indexMask = blocksAmount - 1;
    }


    protected WriteBackDirectCache(WriteBackDirectCache copy) {
        super(copy);
        indexShift = copy.indexShift;
        indexMask = copy.indexMask;
    }

    @Override
    protected CacheBlock getBlock(int address, boolean create, boolean callEvent) {
        int tag = calculateTag(address);
        int index = calculateBlockIndex(address);

        operations++;
        CacheBlock b = blocks[index];
        if (b == null || b.getTag() != tag) b = null;

        var isHit = b != null;

        if (!isHit && !create) return null;

        CacheBlock old = b;
        if (b != null) hits++;
        else {
            int start = address & ~byteMask;
            b = new CacheBlock(tag, start, new byte[blockSize << 2]);

            old = blocks[index];
            if (old != null && old.isDirty()) {
                old.write(parent);
            }

            byte[] data = b.getData();
            for (int i = 0; i < data.length; i++) {
                data[i] = parent.getByte(start + i);
            }
            b.setCreationTime(cacheTime);

            blocks[index] = b;
        }

        if (callEvent) {
            callEvent(new CacheOperationEvent(this, operations - 1, isHit, old, b, index));
        }

        return b;
    }


    @Override
    public Memory copy() {
        return new WriteBackDirectCache(this);
    }

    protected int calculateBlockIndex(int address) {
        return address >> indexShift & indexMask;
    }
}
