package net.jamsimulator.jams.mips.memory.cache.event;

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;

/**
 * This event is called when a {@link Cache} finishes an operation.
 */
public class CacheOperationEvent extends Event {

	private final Cache cache;
	private final long operation;
	protected final boolean hit;
	private final CacheBlock oldBlock, newBlock;
	private final int blockIndex;

	public CacheOperationEvent(Cache cache, long operation, boolean hit, CacheBlock oldBlock, CacheBlock newBlock, int blockIndex) {
		this.cache = cache;
		this.operation = operation;
		this.hit = hit;
		this.oldBlock = oldBlock;
		this.newBlock = newBlock;
		this.blockIndex = blockIndex;
	}

	public Cache getCache() {
		return cache;
	}

	public long getOperation() {
		return operation;
	}

	public boolean isHit() {
		return hit;
	}

	public CacheBlock getOldBlock() {
		return oldBlock;
	}

	public CacheBlock getNewBlock() {
		return newBlock;
	}

	public int getBlockIndex() {
		return blockIndex;
	}
}
