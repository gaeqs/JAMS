package net.jamsimulator.jams.mips.memory.cache.writethrough;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;
import net.jamsimulator.jams.mips.memory.cache.CacheBuilder;
import net.jamsimulator.jams.mips.memory.cache.CacheReplacementPolicy;
import net.jamsimulator.jams.mips.memory.cache.event.CacheOperationEvent;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.Validate;

public class WriteThroughSetAssociativeCache extends WriteThroughCache {

	protected final CacheReplacementPolicy replacementPolicy;
	protected final int setSize, setsAmount, setShift;

	public WriteThroughSetAssociativeCache(CacheBuilder<?> builder, Memory parent, int blockSize, int blocksAmount, int setSize, CacheReplacementPolicy replacementPolicy) {
		super(builder, parent, blockSize, blocksAmount, 32 - 2 - NumericUtils.log2(blockSize) - NumericUtils.log2(blocksAmount / setSize));
		Validate.isTrue(NumericUtils.is2Elev(setSize), "SetSize cannot be expressed as 2^n!");
		Validate.isTrue(setSize <= blocksAmount, "Set size must be lower or equal to BlockAmount!");

		this.replacementPolicy = replacementPolicy;
		this.setSize = setSize;
		this.setsAmount = blocksAmount / setSize;

		this.setShift = 2 + NumericUtils.log2(blockSize);
	}

	protected WriteThroughSetAssociativeCache(WriteThroughSetAssociativeCache copy) {
		super(copy);
		replacementPolicy = copy.replacementPolicy;
		setSize = copy.setSize;
		setsAmount = copy.setsAmount;
		setShift = copy.setShift;
	}

	@Override
	protected CacheBlock getBlock(int address, boolean create, boolean callEvent) {
		int tag = calculateTag(address);
		int index = calculateSetIndex(address) * setSize;

		operations++;

		CacheBlock b = null;
		CacheBlock current;
		int blockIndex = index;
		for (int i = 0; i < setSize; i++) {
			current = blocks[index + i];
			if (current != null && current.getTag() == tag) {
				b = current;
				break;
			}
			blockIndex++;
		}

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

			CacheBlock[] set = new CacheBlock[setSize];
			System.arraycopy(blocks, index, set, 0, setSize);
			blockIndex = index + replacementPolicy.getBlockToReplaceIndex(set);
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
		return new WriteThroughSetAssociativeCache(this);
	}

	protected int calculateSetIndex(int address) {
		return address >> setShift & setsAmount - 1;
	}
}
