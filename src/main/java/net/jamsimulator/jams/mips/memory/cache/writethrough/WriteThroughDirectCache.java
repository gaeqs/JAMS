package net.jamsimulator.jams.mips.memory.cache.writethrough;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.event.CacheOperationEvent;
import net.jamsimulator.jams.utils.NumericUtils;

public class WriteThroughDirectCache extends WriteThroughCache {

	protected final int indexShift, indexMask;

	public WriteThroughDirectCache(CacheBuilder<?> builder, Memory parent, int blockSize, int blocksAmount) {
		super(builder, parent, blockSize, blocksAmount, 32 - 2 - NumericUtils.log2(blockSize) - NumericUtils.log2(blocksAmount));
		this.indexShift = 2 + NumericUtils.log2(blockSize);
		this.indexMask = blocksAmount - 1;
	}


	protected WriteThroughDirectCache(WriteThroughDirectCache copy) {
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
		else if (create) {
			int start = address & ~byteMask;
			b = new CacheBlock(tag, start, new byte[blockSize << 2]);

			byte[] data = b.getData();
			for (int i = 0; i < data.length; i++) {
				data[i] = parent.getByte(start + i);
			}

			b.setCreationTime(cacheTime);

			old = blocks[index];
			blocks[index] = b;
		} else index = -1;

		if (callEvent) {
			callEvent(new CacheOperationEvent(this, operations - 1, isHit, old, b, index));
		}

		return b;
	}


	@Override
	public Memory copy() {
		return new WriteThroughDirectCache(this);
	}

	protected int calculateBlockIndex(int address) {
		return address >> indexShift & indexMask;
	}
}
