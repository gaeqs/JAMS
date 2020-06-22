package net.jamsimulator.jams.mips.simulation.singlecycle.event;

import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.event.SimulationEvent;
import net.jamsimulator.jams.mips.simulation.singlecycle.SingleCycleSimulation;

/**
 * Represents an {@link net.jamsimulator.jams.event.Event} related to a {@link SingleCycleSimulation}.
 */
public class SingleCycleSimulationEvent extends SimulationEvent {

	/**
	 * Creates the single-cycle simulation event.
	 *
	 * @param simulation the {@link Simulation} tha created this event.
	 */
	public SingleCycleSimulationEvent(SingleCycleSimulation simulation) {
		super(simulation);
	}

	@Override
	public SingleCycleSimulation getSimulation() {
		return (SingleCycleSimulation) super.getSimulation();
	}
}
