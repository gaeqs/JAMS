package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

/**
 * A {@link SimulationChange} that registers a {@link net.jamsimulator.jams.mips.memory.Memory}'s endianness change.
 */
public class SimulationChangeMemoryEndianness extends SimulationChange<Architecture> {

	private final boolean wasBigEndian;

	public SimulationChangeMemoryEndianness(boolean wasBigEndian) {
		this.wasBigEndian = wasBigEndian;
	}

	@Override
	public void restore(MIPSSimulation<? extends Architecture> simulation) {
		simulation.getMemory().setBigEndian(wasBigEndian);
	}
}
