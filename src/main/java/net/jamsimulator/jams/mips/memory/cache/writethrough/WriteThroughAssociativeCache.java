package net.jamsimulator.jams.mips.memory.cache.writethrough;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.CacheReplacementPolicy;
import net.jamsimulator.jams.mips.memory.cache.event.CacheOperationEvent;
import net.jamsimulator.jams.utils.NumericUtils;

public class WriteThroughAssociativeCache extends WriteThroughCache {

	protected final CacheReplacementPolicy replacementPolicy;

	public WriteThroughAssociativeCache(CacheBuilder<?> builder, Memory parent, int blockSize, int blocksAmount, CacheReplacementPolicy replacementPolicy) {
		super(builder, parent, blockSize, blocksAmount, 32 - 2 - NumericUtils.log2(blockSize));
		this.replacementPolicy = replacementPolicy;
	}


	protected WriteThroughAssociativeCache(WriteThroughAssociativeCache copy) {
		super(copy);
		replacementPolicy = copy.replacementPolicy;
	}

	@Override
	protected CacheBlock getBlock(int address, boolean create, boolean callEvent) {
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
		else if (create) {
			int start = address & ~byteMask;

			b = new CacheBlock(tag, start, new byte[blockSize << 2]);

			byte[] data = b.getData();
			for (int i = 0; i < data.length; i++) {
				data[i] = parent.getByte(start + i);
			}

			b.setCreationTime(cacheTime);

			blockIndex = replacementPolicy.getBlockToReplaceIndex(blocks);
			old = blocks[blockIndex];
			blocks[blockIndex] = b;
		} else {
			blockIndex = -1;
		}

		if (callEvent) {
			callEvent(new CacheOperationEvent(this, operations - 1, isHit, old, b, blockIndex));
		}

		return b;
	}


	@Override
	public Memory copy() {
		return new WriteThroughAssociativeCache(this);
	}
}
