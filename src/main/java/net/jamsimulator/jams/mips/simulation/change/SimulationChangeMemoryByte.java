package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * A {@link SimulationChange} that registers a byte change inside the {@link Simulation}'s {@link net.jamsimulator.jams.mips.memory.Memory}.
 */
public class SimulationChangeMemoryByte extends SimulationChange<Architecture> {

	private final int address;
	private final byte old;

	public SimulationChangeMemoryByte(int address, byte old) {
		this.address = address;
		this.old = old;
	}

	@Override
	public void restore(Simulation<? extends Architecture> simulation) {
		simulation.getMemory().setByte(address, old);
	}
}
