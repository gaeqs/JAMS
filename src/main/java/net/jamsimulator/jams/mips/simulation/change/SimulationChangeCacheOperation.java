package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.CacheBlock;
import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * A {@link SimulationChange} that registers a word change inside the {@link Simulation}'s {@link Memory}.
 */
public class SimulationChangeCacheOperation extends SimulationChange<Architecture> {

	private final Cache cache;
	private final boolean hit;
	private final int blockIndex;
	private final CacheBlock old;

	public SimulationChangeCacheOperation(Cache cache, boolean hit, int blockIndex, CacheBlock old) {
		this.cache = cache;
		this.hit = hit;
		this.blockIndex = blockIndex;
		this.old = old;
	}

	@Override
	public void restore(Simulation<? extends Architecture> simulation) {
		cache.undoOperation(hit, blockIndex, old);
	}
}
