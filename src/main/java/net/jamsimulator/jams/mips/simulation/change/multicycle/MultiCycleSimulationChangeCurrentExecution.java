package net.jamsimulator.jams.mips.simulation.change.multicycle;

import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.change.SimulationChange;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleSimulation;

/**
 * A {@link SimulationChange}  that registers the change of the {@link MultiCycleExecution} of a multi-cycle simulation.
 */
public class MultiCycleSimulationChangeCurrentExecution extends SimulationChange<MultiCycleArchitecture> {

	private final MultiCycleExecution<?> old;

	public MultiCycleSimulationChangeCurrentExecution(MultiCycleExecution<?> old) {
		this.old = old;
	}

	@Override
	public void restore(Simulation<? extends MultiCycleArchitecture> simulation) {
		((MultiCycleSimulation) simulation).forceCurrentExecutionChange(old);
	}
}
