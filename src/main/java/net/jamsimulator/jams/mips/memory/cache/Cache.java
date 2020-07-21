package net.jamsimulator.jams.mips.memory.cache;

import net.jamsimulator.jams.mips.memory.Memory;

/**
 * Represents a cache.
 */
public interface Cache extends Memory {

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
	 * Resets the stats of this cache.
	 */
	void resetStats();

}
