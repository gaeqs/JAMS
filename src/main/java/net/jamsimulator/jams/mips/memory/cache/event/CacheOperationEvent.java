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

package net.jamsimulator.jams.mips.memory.cache.event;

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;

/**
 * This event is called when a {@link Cache} finishes an operation.
 */
public class CacheOperationEvent extends Event {

    protected final boolean hit;
    private final Cache cache;
    private final long operation;
    private final CacheBlock oldBlock, newBlock;
    private final int blockIndex;

    public CacheOperationEvent(Cache cache, long operation, boolean hit, CacheBlock oldBlock, CacheBlock newBlock, int blockIndex) {
        this.cache = cache;
        this.operation = operation;
        this.hit = hit;
        this.oldBlock = oldBlock;
        this.newBlock = newBlock;
        this.blockIndex = blockIndex;
    }

    public Cache getCache() {
        return cache;
    }

    public long getOperation() {
        return operation;
    }

    public boolean isHit() {
        return hit;
    }

    public CacheBlock getOldBlock() {
        return oldBlock;
    }

    public CacheBlock getNewBlock() {
        return newBlock;
    }

    public int getBlockIndex() {
        return blockIndex;
    }
}
