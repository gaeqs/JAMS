package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.file.SimulationFile;

/**
 * A {@link SimulationChange} that registers the writting of a file.
 */
public class SimulationChangeFileOpen extends SimulationChange<Architecture> {

	private final int id;

	public SimulationChangeFileOpen(int id) {
		this.id = id;
	}

	@Override
	public void restore(Simulation<? extends Architecture> simulation) {
		simulation.getFiles().forceRemove(id);
	}

}
