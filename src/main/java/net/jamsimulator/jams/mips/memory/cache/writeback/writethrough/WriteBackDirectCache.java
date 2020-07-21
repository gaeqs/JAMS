package net.jamsimulator.jams.mips.memory.cache.writeback.writethrough;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;
import net.jamsimulator.jams.utils.NumericUtils;

public class WriteBackDirectCache extends WriteBackCache {

	protected final int indexShift, indexMask;

	public WriteBackDirectCache(Memory parent, int blockSize, int blocksAmount) {
		super(parent, blockSize, blocksAmount, 32 - 2 - NumericUtils.log2(blockSize) - NumericUtils.log2(blocksAmount));
		this.indexShift = 2 + NumericUtils.log2(blockSize);
		this.indexMask = blocksAmount - 1;
	}


	protected WriteBackDirectCache(WriteBackDirectCache copy) {
		super(copy);
		indexShift = copy.indexShift;
		indexMask = copy.indexMask;
	}

	@Override
	protected CacheBlock getBlock(int address) {
		int tag = calculateTag(address);
		int index = calculateBlockIndex(address);

		operations++;
		CacheBlock b = blocks[index];
		if (b == null || b.getTag() != tag) b = null;

		if (b != null) hits++;

		if (b == null) {
			int start = address & ~byteMask;
			b = new CacheBlock(tag, start, new byte[blockSize << 2]);

			CacheBlock old = blocks[index];
			if(old != null && old.isDirty()) {
				old.write(parent);
			}

			byte[] data = b.getData();
			for (int i = 0; i < data.length; i++) {
				data[i] = parent.getByte(start + i);
			}
			b.setCreationTime(cacheTime);

			blocks[index] = b;
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
