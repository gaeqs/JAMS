package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.file.SimulationFile;

/**
 * A {@link SimulationChange} that registers the opening of a file.
 */
public class SimulationChangeFileWrite extends SimulationChange<Architecture> {

	private final SimulationFile file;
	private final int amount;

	public SimulationChangeFileWrite(SimulationFile file, int amount) {
		this.file = file;
		this.amount = amount;
	}

	@Override
	public void restore(MIPSSimulation<? extends Architecture> simulation) {
		file.revert(amount);
	}
}
