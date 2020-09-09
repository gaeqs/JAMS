package net.jamsimulator.jams.mips.simulation.change.pipelined;

import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.change.SimulationChange;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.mips.simulation.pipelined.PipelinedSimulation;

/**
 * A {@link SimulationChange} that registers the change of the {@link MultiCycleStep} of a multi-cycle simulation.
 */
public class PipelinedSimulationExitRequest extends SimulationChange<PipelinedArchitecture> {

	@Override
	public void restore(Simulation<? extends PipelinedArchitecture> simulation) {
		((PipelinedSimulation) simulation).removeExitRequest();
	}
}
