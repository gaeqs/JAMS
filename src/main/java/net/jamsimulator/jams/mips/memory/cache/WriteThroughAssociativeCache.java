package net.jamsimulator.jams.mips.memory.cache;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.utils.NumericUtils;

public class WriteThroughAssociativeCache extends WriteThroughCache {

	protected final CacheReplacementPolicy replacementPolicy;

	public WriteThroughAssociativeCache(Memory parent, int blockSize, int blocksAmount, CacheReplacementPolicy replacementPolicy) {
		super(parent, blockSize, blocksAmount, 32 - 2 - NumericUtils.log2(blockSize));
		this.replacementPolicy = replacementPolicy;
	}


	protected WriteThroughAssociativeCache(WriteThroughAssociativeCache copy) {
		super(copy);
		replacementPolicy = copy.replacementPolicy;
	}

	@Override
	protected CacheBlock getBlock(int address, boolean create) {
		int tag = calculateTag(address);

		operations++;

		CacheBlock b = null;
		for (CacheBlock block : blocks) {
			if (block != null && block.getTag() == tag) {
				b = block;
				break;
			}
		}

		if (b != null) hits++;

		if (b == null && create) {
			b = new CacheBlock(tag, new byte[blockSize << 2]);
			int start = address & ~byteMask;

			byte[] data = b.getData();
			for (int i = 0; i < data.length; i++) {
				data[i] = parent.getByte(start + i);
			}

			b.setCreationTime(cacheTime);

			blocks[replacementPolicy.getBlockToReplaceIndex(blocks)] = b;
		}

		return b;
	}


	@Override
	public Memory copy() {
		return new WriteThroughAssociativeCache(this);
	}
}
