package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * A {@link SimulationChange} that registers a word change inside the {@link Simulation}'s {@link net.jamsimulator.jams.mips.memory.Memory}.
 */
public class SimulationChangeMemoryWord extends SimulationChange<Architecture> {

	private final int address, old;

	public SimulationChangeMemoryWord(int address, int old) {
		this.address = address;
		this.old = old;
	}

	@Override
	public void restore(Simulation<? extends Architecture> simulation) {
		simulation.getMemory().setWord(address, old);
	}
}
