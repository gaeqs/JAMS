package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * A {@link SimulationChange} that registers a byte change inside the {@link Simulation}'s {@link net.jamsimulator.jams.mips.memory.Memory}.
 */
public class SimulationChangeMemoryByte extends SimulationChange<Architecture> {

	private final Memory memory;
	private final int address;
	private final byte old;
	private final long operations, hits;

	public SimulationChangeMemoryByte(Memory memory, int address, byte old) {
		this.memory = memory;
		this.address = address;
		this.old = old;

		if (memory instanceof Cache) {
			var stats = ((Cache) memory).getStats();
			operations = stats.getOperations();
			hits = stats.getHits();
		} else {
			operations = hits = 0;
		}
	}

	@Override
	public void restore(Simulation<? extends Architecture> simulation) {
		memory.setByte(address, old);
		if (memory instanceof Cache) {
			((Cache) memory).forceStats(operations, hits);
		}
	}
}
