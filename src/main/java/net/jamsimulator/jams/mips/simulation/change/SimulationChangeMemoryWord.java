package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * A {@link SimulationChange} that registers a word change inside the {@link Simulation}'s {@link net.jamsimulator.jams.mips.memory.Memory}.
 */
public class SimulationChangeMemoryWord extends SimulationChange<Architecture> {

	private Memory memory;
	private final int address, old;
	private final long operations, hits;

	public SimulationChangeMemoryWord(Memory memory, int address, int old) {
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

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

	@Override
	public void restore(Simulation<? extends Architecture> simulation) {
		memory.setWord(address, old);
		if (memory instanceof Cache) {
			((Cache) memory).forceStats(operations, hits);
		}
	}
}
