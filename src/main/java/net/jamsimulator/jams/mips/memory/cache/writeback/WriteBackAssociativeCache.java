package net.jamsimulator.jams.mips.memory.cache.writeback;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;
import net.jamsimulator.jams.mips.memory.cache.CacheReplacementPolicy;
import net.jamsimulator.jams.mips.memory.cache.event.CacheOperationEvent;
import net.jamsimulator.jams.utils.NumericUtils;

public class WriteBackAssociativeCache extends WriteBackCache {

	protected final CacheReplacementPolicy replacementPolicy;

	public WriteBackAssociativeCache(Memory parent, int blockSize, int blocksAmount, CacheReplacementPolicy replacementPolicy) {
		super(parent, blockSize, blocksAmount, 32 - 2 - NumericUtils.log2(blockSize));
		this.replacementPolicy = replacementPolicy;
	}


	protected WriteBackAssociativeCache(WriteBackAssociativeCache copy) {
		super(copy);
		replacementPolicy = copy.replacementPolicy;
	}

	@Override
	protected CacheBlock getBlock(int address, boolean callEvent) {
		int tag = calculateTag(address);

		operations++;

		CacheBlock b = null;
		int blockIndex = 0;
		for (CacheBlock block : blocks) {
			if (block != null && block.getTag() == tag) {
				b = block;
				break;
			}
			blockIndex++;
		}

		var isHit = b != null;
		CacheBlock old = b;

		if (b != null) hits++;
		else {
			int start = address & ~byteMask;
			b = new CacheBlock(tag, start, new byte[blockSize << 2]);

			blockIndex = replacementPolicy.getBlockToReplaceIndex(blocks);

			old = blocks[blockIndex];
			if (old != null && old.isDirty()) {
				old.write(parent);
			}

			byte[] data = b.getData();
			for (int i = 0; i < data.length; i++) {
				data[i] = parent.getByte(start + i);
			}

			b.setCreationTime(cacheTime);

			blocks[blockIndex] = b;
		}

		if (callEvent) {
			callEvent(new CacheOperationEvent(this, operations - 1, isHit, old, b, blockIndex));
		}

		return b;
	}


	@Override
	public Memory copy() {
		return new WriteBackAssociativeCache(this);
	}
}
