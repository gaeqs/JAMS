package net.jamsimulator.jams.mips.simulation.change.multicycle;

import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.change.SimulationChange;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleSimulation;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleStep;

/**
 * A {@link SimulationChange} that registers the change of the {@link MultiCycleStep} of a multi-cycle simulation.
 */
public class MultiCycleSimulationChangeStep extends SimulationChange<MultiCycleArchitecture> {

	private final MultiCycleStep old;

	public MultiCycleSimulationChangeStep(MultiCycleStep old) {
		this.old = old;
	}

	@Override
	public void restore(Simulation<? extends MultiCycleArchitecture> simulation) {
		((MultiCycleSimulation) simulation).forceStepChange(old);
	}
}
