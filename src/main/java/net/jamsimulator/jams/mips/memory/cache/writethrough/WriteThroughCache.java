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

package net.jamsimulator.jams.mips.memory.cache.writethrough;

import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.MIPSAddressException;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.MemorySection;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.CacheStats;
import net.jamsimulator.jams.mips.memory.cache.event.CacheResetEvent;
import net.jamsimulator.jams.mips.memory.event.*;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.Validate;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a direct mapping cache.
 * <p>
 * This cache uses the tag of the
 */
public abstract class WriteThroughCache extends SimpleEventBroadcast implements Cache {

    protected final CacheBuilder<?> builder;
    protected final Memory parent;
    protected final int blockSize, blocksAmount, tagSize;

    protected final int tagShift;
    protected final int byteMask;

    protected final CacheBlock[] blocks;
    protected CacheBlock[] savedBlocks;

    protected long cacheTime;

    protected long operations, hits;
    protected long savedOperations, savedHits;

    public WriteThroughCache(CacheBuilder<?> builder, Memory parent, int blockSize, int blocksAmount, int tagSize) {
        Validate.notNull(parent, "Parent cannot be null!");
        Validate.isTrue(NumericUtils.is2Elev(blockSize), "BlockSize cannot be expressed as 2^n!");
        Validate.isTrue(NumericUtils.is2Elev(blocksAmount), "BlockAmount cannot be expressed as 2^n!");

        this.builder = builder;
        this.parent = parent;
        this.blockSize = blockSize;
        this.blocksAmount = blocksAmount;

        this.tagSize = tagSize;

        this.tagShift = 32 - tagSize;
        this.byteMask = (blockSize - 1) << 2 | 3;

        blocks = new CacheBlock[blocksAmount];
    }

    protected WriteThroughCache(WriteThroughCache copy) {
        builder = copy.builder;
        parent = copy.parent.copy();
        blockSize = copy.blockSize;
        blocksAmount = copy.blocksAmount;
        tagSize = copy.tagSize;

        tagShift = copy.tagShift;
        byteMask = copy.byteMask;

        blocks = new CacheBlock[copy.blocks.length];
        savedBlocks = null;

        cacheTime = copy.cacheTime;
        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = copy.blocks[i] == null ? null : copy.blocks[i].copy();
        }

        operations = copy.operations;
        hits = copy.hits;

        savedOperations = 0;
        savedHits = 0;
    }

    @Override
    public CacheBuilder<?> getBuilder() {
        return builder;
    }

    @Override
    public int getBlocksAmount() {
        return blocksAmount;
    }

    @Override
    public int getBlockSize() {
        return blockSize;
    }

    @Override
    public int getTagSize() {
        return tagSize;
    }

    @Override
    public CacheStats getStats() {
        return new CacheStats(operations, hits, operations - hits);
    }

    @Override
    public void resetCache() {
        var event = callEvent(new CacheResetEvent.Before(this));
        if (event.isCancelled()) return;

        operations = hits = cacheTime = 0;
        Arrays.fill(blocks, null);
        if (parent instanceof Cache) ((Cache) parent).resetCache();

        callEvent(new CacheResetEvent.After(this));
    }

    @Override
    public void flush() {

    }

    @Override
    public boolean isBigEndian() {
        return parent.isBigEndian();
    }

    @Override
    public void setBigEndian(boolean bigEndian) {
        parent.setBigEndian(bigEndian);
    }

    @Override
    public byte getByte(int address) {
        return getByte(address, true, false, true);
    }

    @Override
    public byte getByte(int address, boolean callEvents, boolean bypassCaches, boolean modifyCaches) {
        if (bypassCaches || !isDirectionAffectedByCache(address))
            return parent.getByte(address, callEvents, true, modifyCaches);
        boolean events = callEvents && areEventCallsEnabled();

        CacheBlock block = getBlock(address, modifyCaches, events);

        if (block == null) {
            return parent.getByte(address, callEvents, false, modifyCaches);
        }

        if (!events) {
            if (modifyCaches)
                block.setModificationTime(cacheTime++);
            return block.getByte(address & byteMask);
        }

        //Invokes the before event.
        var before = callEvent(new MemoryByteGetEvent.Before(this, address));

        //Refresh data.
        address = before.getAddress();

        //Gets the section and the byte.
        if (modifyCaches)
            block.setModificationTime(cacheTime++);
        byte b = block.getByte(address & byteMask);

        //Invokes the after event.
        return callEvent(new MemoryByteGetEvent.After(this, null, address, b)).getValue();
    }

    @Override
    public void setByte(int address, byte b) {
        setByte(address, b, true, false, true);
    }

    @Override
    public void setByte(int address, byte b, boolean callEvents, boolean bypassCaches, boolean modifyCaches) {
        parent.setByte(address, b, callEvents, bypassCaches, modifyCaches);

        if (bypassCaches || !isDirectionAffectedByCache(address)) return;

        boolean events = callEvents && areEventCallsEnabled();

        CacheBlock block = getBlock(address, modifyCaches, areEventCallsEnabled());
        if (block != null) {
            if (!events) {
                block.setModificationTime(cacheTime++);
                block.setByte(address & byteMask, b);
                return;
            }

            //Invokes the before event.
            var before = callEvent(new MemoryByteSetEvent.Before(this, address, b));
            if (before.isCancelled()) return;

            //Refresh data.
            address = before.getAddress();
            b = before.getValue();

            //Gets the section and sets the byte.
            block.setModificationTime(cacheTime++);
            byte old = block.setByte(address & byteMask, b);

            //Invokes the after event.
            callEvent(new MemoryByteSetEvent.After(this, null, address, b, old));
        } else {
            parent.setByte(address, b, callEvents, false, modifyCaches);
        }
    }


    @Override
    public short getHalfword(int address) {
        return getHalfword(address, true, false, true);
    }

    @Override
    public short getHalfword(int address, boolean callEvents, boolean bypassCaches, boolean modifyCaches) {
        if ((address & 0x1) != 0) throw new MIPSAddressException(InterruptCause.ADDRESS_LOAD_EXCEPTION, address);
        if (bypassCaches || !isDirectionAffectedByCache(address))
            return parent.getHalfword(address, callEvents, true, modifyCaches);
        boolean events = callEvents && areEventCallsEnabled();

        CacheBlock block = getBlock(address, modifyCaches, events);

        if (block == null) {
            return parent.getHalfword(address, callEvents, false, modifyCaches);
        }

        if (!events) {
            if (modifyCaches)
                block.setModificationTime(cacheTime++);
            return block.getHalfword(address & byteMask, isBigEndian());
        }

        //Invokes the before event.
        var before = callEvent(new MemoryHalfwordGetEvent.Before(this, address));

        //Refresh data.
        address = before.getAddress();

        //Gets the section and the word.
        if (modifyCaches)
            block.setModificationTime(cacheTime++);
        short word = block.getHalfword(address & byteMask, isBigEndian());

        //Invokes the after event.
        return callEvent(new MemoryHalfwordGetEvent.After(this, null, address, word)).getValue();
    }

    @Override
    public void setHalfword(int address, short word) {
        setHalfword(address, word, true, false, true);
    }

    @Override
    public void setHalfword(int address, short word, boolean callEvents, boolean bypassCaches, boolean modifyCaches) {
        if ((address & 0x1) != 0) throw new MIPSAddressException(InterruptCause.ADDRESS_LOAD_EXCEPTION, address);
        parent.setHalfword(address, word, callEvents, bypassCaches, modifyCaches);

        if (bypassCaches || !isDirectionAffectedByCache(address)) return;

        boolean events = callEvents && areEventCallsEnabled();

        CacheBlock block = getBlock(address, modifyCaches, areEventCallsEnabled());
        if (block != null) {
            if (!events) {
                block.setModificationTime(cacheTime++);
                block.setHalfword(address & byteMask, word, isBigEndian());
                return;
            }

            //Invokes the before event.
            var before = callEvent(new MemoryHalfwordSetEvent.Before(this, address, word));
            if (before.isCancelled()) return;

            //Refresh data.
            address = before.getAddress();
            word = before.getValue();

            //Gets the section and sets the word.
            block.setModificationTime(cacheTime++);
            short old = block.setHalfword(address & byteMask, word, isBigEndian());

            //Invokes the after event.
            callEvent(new MemoryHalfwordSetEvent.After(this, null, address, word, old));
        } else {
            parent.setHalfword(address, word, callEvents, false, modifyCaches);
        }
    }

    @Override
    public int getWord(int address) {
        return getWord(address, true, false, true);
    }

    @Override
    public int getWord(int address, boolean callEvents, boolean bypassCaches, boolean modifyCaches) {
        if ((address & 0x3) != 0) throw new MIPSAddressException(InterruptCause.ADDRESS_LOAD_EXCEPTION, address);
        if (bypassCaches || !isDirectionAffectedByCache(address))
            return parent.getWord(address, callEvents, true, modifyCaches);
        boolean events = callEvents && areEventCallsEnabled();

        CacheBlock block = getBlock(address, modifyCaches, events);

        if (block == null) {
            return parent.getWord(address, callEvents, false, modifyCaches);
        }

        if (!events) {
            if (modifyCaches)
                block.setModificationTime(cacheTime++);
            return block.getWord(address & byteMask, isBigEndian());
        }

        //Invokes the before event.
        var before = callEvent(new MemoryWordGetEvent.Before(this, address));

        //Refresh data.
        address = before.getAddress();

        //Gets the section and the word.
        if (modifyCaches)
            block.setModificationTime(cacheTime++);
        int word = block.getWord(address & byteMask, isBigEndian());

        //Invokes the after event.
        return callEvent(new MemoryWordGetEvent.After(this, null, address, word)).getValue();
    }

    @Override
    public void setWord(int address, int word) {
        setWord(address, word, true, false, true);
    }

    @Override
    public void setWord(int address, int word, boolean callEvents, boolean bypassCaches, boolean modifyCaches) {
        if ((address & 0x3) != 0) throw new MIPSAddressException(InterruptCause.ADDRESS_LOAD_EXCEPTION, address);
        parent.setWord(address, word, callEvents, bypassCaches, modifyCaches);

        if (bypassCaches || !isDirectionAffectedByCache(address)) return;

        boolean events = callEvents && areEventCallsEnabled();

        CacheBlock block = getBlock(address, modifyCaches, areEventCallsEnabled());
        if (block != null) {
            if (!events) {
                block.setModificationTime(cacheTime++);
                block.setWord(address & byteMask, word, isBigEndian());
                return;
            }

            //Invokes the before event.
            var before = callEvent(new MemoryWordSetEvent.Before(this, address, word));
            if (before.isCancelled()) return;

            //Refresh data.
            address = before.getAddress();
            word = before.getValue();

            //Gets the section and sets the word.
            block.setModificationTime(cacheTime++);
            int old = block.setWord(address & byteMask, word, isBigEndian());

            //Invokes the after event.
            callEvent(new MemoryWordSetEvent.After(this, null, address, word, old));
        } else {
            parent.setWord(address, word, callEvents, false, modifyCaches);
        }
    }

    @Override
    public int getFirstTextAddress() {
        return parent.getFirstTextAddress();
    }

    @Override
    public int getFirstDataAddress() {
        return parent.getFirstDataAddress();
    }

    @Override
    public int getNextDataAddress() {
        return parent.getNextDataAddress();
    }

    @Override
    public void setNextDataAddress(int nextDataAddress) {
        parent.setNextDataAddress(nextDataAddress);
    }

    @Override
    public int getFirstKernelTextAddress() {
        return parent.getFirstKernelTextAddress();
    }

    @Override
    public int getFirstKernelDataAddress() {
        return parent.getFirstKernelDataAddress();
    }

    @Override
    public int getFirstExternalAddress() {
        return parent.getFirstExternalAddress();
    }

    @Override
    public Optional<Memory> getNextLevelMemory() {
        return Optional.of(parent);
    }

    @Override
    public int allocateMemory(int length) {
        return parent.allocateMemory(length);
    }

    @Override
    public void enableEventCalls(boolean enable) {
        parent.enableEventCalls(enable);
    }

    @Override
    public boolean areEventCallsEnabled() {
        return parent.areEventCallsEnabled();
    }

    @Override
    public void saveState() {
        parent.saveState();
        this.savedBlocks = new CacheBlock[blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            savedBlocks[i] = blocks[i] == null ? null : blocks[i].copy();
        }
        savedOperations = operations;
        savedHits = hits;
    }

    @Override
    public void restoreSavedState() {
        parent.restoreSavedState();

        for (int i = 0; i < blocks.length; i++) {
            blocks[i] = savedBlocks[i] == null ? null : savedBlocks[i].copy();
        }
        operations = savedOperations;
        hits = savedHits;
    }

    @Override
    public void undoOperation(boolean hit, int blockIndex, CacheBlock old) {
        operations--;
        if (hit) hits--;
        if (blockIndex != -1) {
            blocks[blockIndex] = old;
        }
    }

    @Override
    public void forceStats(long operations, long hits) {
        this.operations = operations;
        this.hits = hits;
    }

    @Override
    public Optional<CacheBlock> getCacheBlock(int index) {
        return Optional.ofNullable(blocks[index]);
    }

    @Override
    public Set<MemorySection> getMemorySections() {
        return parent.getMemorySections();
    }

    @Override
    public String getMemorySectionName(int address) {
        return parent.getMemorySectionName(address);
    }

    @Override
    public MemorySection getMemorySection(int address) {
        return parent.getMemorySection(address);
    }

    @Override
    public Optional<MemorySection> getMemorySection(String name) {
        return parent.getMemorySection(name);
    }

    @Override
    public boolean isDirectionAffectedByCache(int address) {
        return parent.isDirectionAffectedByCache(address);
    }

    protected abstract CacheBlock getBlock(int address, boolean create, boolean callEvent);

    protected int calculateTag(int address) {
        return address >> tagShift;
    }

}
