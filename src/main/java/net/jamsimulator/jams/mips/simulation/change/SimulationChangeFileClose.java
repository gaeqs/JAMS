package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.file.SimulationFile;

/**
 * A {@link SimulationChange} that registers the closing of a file.
 */
public class SimulationChangeFileClose extends SimulationChange<Architecture> {

	private final SimulationFile file;

	public SimulationChangeFileClose(SimulationFile file) {
		this.file = file;
	}

	@Override
	public void restore(Simulation<? extends Architecture> simulation) {
		simulation.getFiles().forceAdd(file);
	}
}
