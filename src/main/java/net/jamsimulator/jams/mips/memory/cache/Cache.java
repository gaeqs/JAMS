package net.jamsimulator.jams.mips.memory.cache;

import net.jamsimulator.jams.mips.memory.Memory;

/**
 * Represents a cache.
 */
public interface Cache extends Memory {

	/**
	 * Returns the {@link CacheBuilder} of this cache.
	 * @return the {@link CacheBuilder} of this cache.
	 */
	CacheBuilder<?> getBuilder();

	/**
	 * Returns the amount of blocks inside this cache.
	 *
	 * @return the amount of blocks.
	 */
	int getBlocksAmount();

	/**
	 * Returns the size of each block inside this cache in words.
	 *
	 * @return the size of each block in words.
	 */
	int getBlockSize();

	/**
	 * Returns the size of each tag inside this cache in bits.
	 *
	 * @return the size of each tag in bits.
	 */
	int getTagSize();

	/**
	 * Returns a new {@link CacheStats} instance containing all
	 * stats of this cache at this moment.
	 * <p>
	 * This instance won't be updated if any operation is made on the cache.
	 *
	 * @return the {@link CacheStats}.
	 */
	CacheStats getStats();

	/**
	 * Resets this cache
	 * <p>
	 * This method also flushes any pending change.
	 */
	void resetCache();

	/**
	 * Writes to the next step memory any pending change.
	 */
	void flush();

	/**
	 * This method should be used only by undo operations.
	 * <p>
	 * Removes an operation and sets the given block at the given index.
	 * If the parameter hit is true, this method removes one hit too.
	 *
	 * @param hit        whether a hit should be substracted.
	 * @param blockIndex the block index.
	 * @param old        the old {@link CacheBlock}.
	 */
	void undoOperation(boolean hit, int blockIndex, CacheBlock old);

	/**
	 * This method should be used only by undo operations.
	 * <p>
	 * Sets the stats of this cache.
	 *
	 * @param operations the operations.
	 * @param hits       the hits.
	 */
	void forceStats(long operations, long hits);

}
