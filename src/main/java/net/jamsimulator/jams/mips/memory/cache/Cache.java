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

import net.jamsimulator.jams.mips.memory.Memory;

import java.util.Optional;

/**
 * Represents a cache.
 */
public interface Cache extends Memory {

    /**
     * Returns the {@link CacheBuilder} of this cache.
     *
     * @return the {@link CacheBuilder} of this cache.
     */
    CacheBuilder<?> getBuilder();

    /**
     * Returns the amount of blocks inside this cache.
     *
     * @return the amount of blocks.
     */
    int getBlocksAmount();

    /**
     * Returns the size of each block inside this cache in words.
     *
     * @return the size of each block in words.
     */
    int getBlockSize();

    /**
     * Returns the size of each tag inside this cache in bits.
     *
     * @return the size of each tag in bits.
     */
    int getTagSize();

    /**
     * Returns a new {@link CacheStats} instance containing all
     * stats of this cache at this moment.
     * <p>
     * This instance won't be updated if any operation is made on the cache.
     *
     * @return the {@link CacheStats}.
     */
    CacheStats getStats();

    /**
     * Resets this cache
     * <p>
     * This method also flushes any pending change.
     */
    void resetCache();

    /**
     * Writes to the next step memory any pending change.
     */
    void flush();

    /**
     * This method should be used only by undo operations.
     * <p>
     * Removes an operation and sets the given block at the given index.
     * If the parameter hit is true, this method removes one hit too.
     *
     * @param hit        whether a hit should be substracted.
     * @param blockIndex the block index.
     * @param old        the old {@link CacheBlock}.
     */
    void undoOperation(boolean hit, int blockIndex, CacheBlock old);

    /**
     * This method should be used only by undo operations.
     * <p>
     * Sets the stats of this cache.
     *
     * @param operations the operations.
     * @param hits       the hits.
     */
    void forceStats(long operations, long hits);

    /**
     * Returns the block at the given index, if present.
     * <p>
     * This method should be used only for representation purposes. Modifying the cache blocks
     * would cause unpredictable behaviours.
     *
     * @param index the index of the block.
     * @return the block.
     */
    Optional<CacheBlock> getCacheBlock(int index);

}
