package net.jamsimulator.jams.mips.memory.cache;

public class CacheStats {

	private final long operations, hits, misses;

	public CacheStats(long operations, long hits, long misses) {
		this.operations = operations;
		this.hits = hits;
		this.misses = misses;
	}

	public long getOperations() {
		return operations;
	}

	public long getHits() {
		return hits;
	}

	public long getMisses() {
		return misses;
	}
}
