package net.jamsimulator.jams.mips.simulation.change.pipelined;

import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.change.SimulationChange;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;
import net.jamsimulator.jams.mips.simulation.pipelined.Pipeline;
import net.jamsimulator.jams.mips.simulation.pipelined.PipelinedSimulation;

/**
 * A {@link SimulationChange} that registers the change of the {@link MultiCycleStep} of a multi-cycle simulation.
 */
public class PipelinedSimulationChangePipeline extends SimulationChange<PipelinedArchitecture> {

	private final Pipeline old;

	public PipelinedSimulationChangePipeline(Pipeline old) {
		this.old = old;
	}

	@Override
	public void restore(Simulation<? extends PipelinedArchitecture> simulation) {
		((PipelinedSimulation) simulation).getPipeline().restore(old);
	}
}
