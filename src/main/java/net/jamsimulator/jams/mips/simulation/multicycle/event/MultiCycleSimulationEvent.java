package net.jamsimulator.jams.mips.simulation.multicycle.event;

import net.jamsimulator.jams.mips.simulation.event.SimulationEvent;
import net.jamsimulator.jams.mips.simulation.multicycle.MultiCycleSimulation;

/**
 * Represents an {@link net.jamsimulator.jams.event.Event} related to a {@link MultiCycleSimulation}.
 */
public class MultiCycleSimulationEvent extends SimulationEvent {

	/**
	 * Creates the multi-cycle simulation event.
	 *
	 * @param simulation the {@link MultiCycleSimulation} that has created this event.
	 */
	public MultiCycleSimulationEvent(MultiCycleSimulation simulation) {
		super(simulation);
	}

	@Override
	public MultiCycleSimulation getSimulation() {
		return (MultiCycleSimulation) super.getSimulation();
	}
}
