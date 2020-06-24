package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * A {@link SimulationChange} that registers a {@link net.jamsimulator.jams.mips.memory.Memory}'s endianness change.
 */
public class SimulationChangeMemoryEndianness extends SimulationChange<Architecture> {

	private final boolean wasBigEndian;

	public SimulationChangeMemoryEndianness(boolean wasBigEndian) {
		this.wasBigEndian = wasBigEndian;
	}

	@Override
	public void restore(Simulation<? extends Architecture> simulation) {
		simulation.getMemory().setBigEndian(wasBigEndian);
	}
}
