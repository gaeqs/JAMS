package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

/**
 * A {@link SimulationChange} that registers a {@link net.jamsimulator.jams.mips.memory.Memory}'s endianness change.
 */
public class SimulationChangeAllocatedMemory extends SimulationChange<Architecture> {

	private final int oldNext;

	public SimulationChangeAllocatedMemory(int oldNext) {
		this.oldNext = oldNext;
	}

	@Override
	public void restore(MIPSSimulation<? extends Architecture> simulation) {
		simulation.getMemory().setNextDataAddress(oldNext);
	}
}
