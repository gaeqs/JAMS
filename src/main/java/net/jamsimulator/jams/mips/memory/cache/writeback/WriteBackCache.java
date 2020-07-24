package net.jamsimulator.jams.mips.memory.cache.writeback;

import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.MemorySection;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;
import net.jamsimulator.jams.mips.memory.cache.CacheStats;
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

	protected final Memory parent;
	protected final int blockSize, blocksAmount, tagSize;

	protected final int tagShift;
	protected final int byteMask;

	protected final CacheBlock[] blocks;
	protected CacheBlock[] savedBlocks;

	protected long cacheTime;

	protected long operations, hits;
	protected long savedOperations, savedHits;

	public WriteBackCache(Memory parent, int blockSize, int blocksAmount, int tagSize) {
		Validate.notNull(parent, "Parent cannot be null!");
		Validate.isTrue(NumericUtils.is2Elev(blockSize), "BlockSize cannot be expressed as 2^n!");
		Validate.isTrue(NumericUtils.is2Elev(blocksAmount), "BlockAmount cannot be expressed as 2^n!");

		this.parent = parent;
		this.blockSize = blockSize;
		this.blocksAmount = blocksAmount;

		this.tagSize = tagSize;

		this.tagShift = 32 - tagSize;
		this.byteMask = (blockSize - 1) << 2 | 3;

		blocks = new CacheBlock[blocksAmount];
	}

	protected WriteBackCache(WriteBackCache copy) {
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
		flush();
		operations = hits = cacheTime = 0;
		Arrays.fill(blocks, null);
		if (parent instanceof Cache) ((Cache) parent).resetCache();
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
		CacheBlock block = getBlock(address);
		block.setModificationTime(cacheTime++);
		return block.getByte(address & byteMask);
	}

	@Override
	public void setByte(int address, byte b) {
		CacheBlock block = getBlock(address);
		if (block != null) {
			block.setModificationTime(cacheTime++);
			block.setByte(address & byteMask, b);
			block.setDirty(true);
		}
	}

	@Override
	public int getWord(int address) {
		CacheBlock block = getBlock(address);
		block.setModificationTime(cacheTime++);
		return block.getWord(address & byteMask, isBigEndian());
	}

	@Override
	public void setWord(int address, int word) {
		CacheBlock block = getBlock(address);
		if (block != null) {
			block.setModificationTime(cacheTime++);
			block.setWord(address & byteMask, word, isBigEndian());
			block.setDirty(true);
		}
	}

	@Override
	public int getWord(int address, boolean callEvents, boolean bypassCaches) {
		if (bypassCaches) return parent.getWord(address);
		return getWord(address);
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

	protected abstract CacheBlock getBlock(int address);

	protected int calculateTag(int address) {
		return address >> tagShift;
	}

}
