package net.jamsimulator.jams.mips.memory.cache.writeback;

import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.mips.interrupt.InterruptCause;
import net.jamsimulator.jams.mips.interrupt.RuntimeAddressException;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.MemorySection;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.CacheStats;
import net.jamsimulator.jams.mips.memory.cache.event.CacheResetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryByteGetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryWordGetEvent;
import net.jamsimulator.jams.mips.memory.event.MemoryWordSetEvent;
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
public abstract class WriteBackCache extends SimpleEventBroadcast implements Cache {

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

    public WriteBackCache(CacheBuilder<?> builder, Memory parent, int blockSize, int blocksAmount, int tagSize) {
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

    protected WriteBackCache(WriteBackCache copy) {
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

        flush();
        operations = hits = cacheTime = 0;
        Arrays.fill(blocks, null);
        if (parent instanceof Cache) ((Cache) parent).resetCache();

        callEvent(new CacheResetEvent.After(this));
    }

    @Override
    public void flush() {
        for (CacheBlock block : blocks) {
            if (block != null && block.isDirty()) {
                block.write(parent);
            }
        }
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
        return getByte(address, true, false);
    }

    @Override
    public void setByte(int address, byte b) {
        CacheBlock block = getBlock(address, areEventCallsEnabled());
        if (block != null) {
            if (!areEventCallsEnabled()) {
                block.setModificationTime(cacheTime++);
                block.setByte(address & byteMask, b);
                block.setDirty(true);
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
            block.setDirty(true);

            //Invokes the after event.
            callEvent(new MemoryByteSetEvent.After(this, null, address, b, old));
        }
    }

    @Override
    public int getWord(int address) {
        return getWord(address, true, false);
    }

    @Override
    public void setWord(int address, int word) {
        if ((address & 0x2) != 0) throw new RuntimeAddressException(InterruptCause.ADDRESS_STORE_EXCEPTION, address);
        CacheBlock block = getBlock(address, areEventCallsEnabled());
        if (block != null) {
            if (!areEventCallsEnabled()) {
                block.setModificationTime(cacheTime++);
                block.setWord(address & byteMask, word, isBigEndian());
                block.setDirty(true);
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
            block.setDirty(true);

            //Invokes the after event.
            callEvent(new MemoryWordSetEvent.After(this, null, address, word, old));
        }
    }

    @Override
    public void setWord(int address, int word, boolean callEvents, boolean bypassCaches) {
        if ((address & 0x2) != 0) throw new RuntimeAddressException(InterruptCause.ADDRESS_STORE_EXCEPTION, address);

        if (bypassCaches) {
            parent.setWord(address, word, callEvents, true);
            return;
        }

        CacheBlock block = getBlock(address, areEventCallsEnabled());

        boolean events = callEvents && areEventCallsEnabled();
        if (block != null) {
            if (!events) {
                block.setModificationTime(cacheTime++);
                block.setWord(address & byteMask, word, isBigEndian());
                block.setDirty(true);
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
            block.setDirty(true);

            //Invokes the after event.
            callEvent(new MemoryWordSetEvent.After(this, null, address, word, old));
        }
    }


    @Override
    public int getWord(int address, boolean callEvents, boolean bypassCaches) {
        if ((address & 0x2) != 0) throw new RuntimeAddressException(InterruptCause.ADDRESS_LOAD_EXCEPTION, address);
        if (bypassCaches) return parent.getWord(address, callEvents, true);
        boolean events = callEvents && areEventCallsEnabled();

        CacheBlock block = getBlock(address, events);

        if (!events) {
            block.setModificationTime(cacheTime++);
            return block.getWord(address & byteMask, isBigEndian());
        }

        //Invokes the before event.
        var before = callEvent(new MemoryWordGetEvent.Before(this, address));

        //Refresh data.
        address = before.getAddress();

        //Gets the section and the word.
        block.setModificationTime(cacheTime++);
        int word = block.getWord(address & byteMask, isBigEndian());

        //Invokes the after event.
        return callEvent(new MemoryWordGetEvent.After(this, null, address, word)).getValue();
    }

    @Override
    public byte getByte(int address, boolean callEvents, boolean bypassCaches) {
        if (bypassCaches) return parent.getByte(address, callEvents, true);
        boolean events = callEvents && areEventCallsEnabled();

        CacheBlock block = getBlock(address, events);

        if (!events) {
            block.setModificationTime(cacheTime++);
            return block.getByte(address & byteMask);
        }

        //Invokes the before event.
        var before = callEvent(new MemoryByteGetEvent.Before(this, address));

        //Refresh data.
        address = before.getAddress();

        //Gets the section and the byte.
        block.setModificationTime(cacheTime++);
        byte b = block.getByte(address & byteMask);

        //Invokes the after event.
        return callEvent(new MemoryByteGetEvent.After(this, null, address, b)).getValue();
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

    protected abstract CacheBlock getBlock(int address, boolean callEvent);

    protected int calculateTag(int address) {
        return address >> tagShift;
    }

}
