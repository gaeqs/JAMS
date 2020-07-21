package net.jamsimulator.jams.mips.memory.cache;

import java.util.Random;
import java.util.function.Function;

public enum CacheReplacementPolicy {

	RANDOM,
	FIFO,
	LRU;

	private static final Random RANDOM_INSTANCE = new Random();

	public int getBlockToReplaceIndex(CacheBlock[] blocks) {
		Function<CacheBlock, Long> comparingFunction;
		switch (this) {
			case RANDOM:
				return RANDOM_INSTANCE.nextInt(blocks.length);
			case FIFO:
				comparingFunction = CacheBlock::getCreationTime;
				break;
			default:
			case LRU:
				comparingFunction = CacheBlock::getModificationTime;
				break;
		}

		long min = Long.MAX_VALUE;
		int minIndex = 0;

		CacheBlock b;
		long v;
		for (int i = 0; i < blocks.length; i++) {
			b = blocks[i];
			if (b == null) return i;
			v = comparingFunction.apply(b);
			if (v < min) {
				min = v;
				minIndex = i;
			}
		}

		return minIndex;
	}

}
