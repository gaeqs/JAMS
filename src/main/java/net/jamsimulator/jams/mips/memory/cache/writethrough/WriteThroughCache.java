package net.jamsimulator.jams.mips.memory.cache.writethrough;

import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;
import net.jamsimulator.jams.mips.memory.cache.CacheStats;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.Validate;

import java.util.Optional;

/**
 * Represents a direct mapping cache.
 * <p>
 * This cache uses the tag of the
 */
public abstract class WriteThroughCache extends SimpleEventBroadcast implements Cache {

	protected final Memory parent;
	protected final int blockSize, blocksAmount, tagSize;

	protected final int tagShift;
	protected final int byteMask;

	protected final CacheBlock[] blocks;
	protected CacheBlock[] savedBlocks;

	protected long cacheTime;

	protected long operations, hits;
	protected long savedOperations, savedHits;

	public WriteThroughCache(Memory parent, int blockSize, int blocksAmount, int tagSize) {
		Validate.notNull(parent, "Parent cannot be null!");
		Validate.isTrue(NumericUtils.is2Elev(blockSize), "BlockSize cannot be expressed as 2^n!");
		Validate.isTrue(NumericUtils.is2Elev(blocksAmount), "BlockAmount cannot be expressed as 2^n!");

		this.parent = parent;
		this.blockSize = blockSize;
		this.blocksAmount = blocksAmount;

		this.tagSize = tagSize;

		this.tagShift = 32 - tagSize;
		this.byteMask = blockSize + 2;

		blocks = new CacheBlock[blocksAmount];
	}

	protected WriteThroughCache(WriteThroughCache copy) {
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
	public void resetStats() {
		operations = hits = 0;
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
		CacheBlock block = getBlock(address, true);
		block.setModificationTime(cacheTime++);
		return block.getByte(address & byteMask);
	}

	@Override
	public void setByte(int address, byte b) {
		CacheBlock block = getBlock(address, false);
		parent.setByte(address, b);
		if (block != null) {
			block.setModificationTime(cacheTime++);
			block.setByte(address & byteMask, b);
		}
	}

	@Override
	public int getWord(int address) {
		CacheBlock block = getBlock(address, true);
		block.setModificationTime(cacheTime++);
		return block.getWord(address & byteMask, isBigEndian());
	}

	@Override
	public void setWord(int address, int word) {
		CacheBlock block = getBlock(address, false);
		parent.setWord(address, word);
		if (block != null) {
			block.setModificationTime(cacheTime++);
			block.setWord(address & byteMask, word, isBigEndian());
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
	public String getMemorySectionName(int address) {
		return parent.getMemorySectionName(address);
	}

	protected abstract CacheBlock getBlock(int address, boolean create);

	protected int calculateTag(int address) {
		return address >> tagShift;
	}

}
