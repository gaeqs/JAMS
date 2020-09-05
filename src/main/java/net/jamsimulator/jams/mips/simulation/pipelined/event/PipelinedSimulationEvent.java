package net.jamsimulator.jams.mips.simulation.pipelined.event;

import net.jamsimulator.jams.mips.simulation.event.SimulationEvent;
import net.jamsimulator.jams.mips.simulation.pipelined.PipelinedSimulation;

/**
 * Represents an {@link net.jamsimulator.jams.event.Event} related to a {@link PipelinedSimulation}.
 */
public class PipelinedSimulationEvent extends SimulationEvent {

	/**
	 * Creates the multi-cycle simulation event.
	 *
	 * @param simulation the {@link PipelinedSimulation} that has created this event.
	 */
	public PipelinedSimulationEvent(PipelinedSimulation simulation) {
		super(simulation);
	}

	@Override
	public PipelinedSimulation getSimulation() {
		return (PipelinedSimulation) super.getSimulation();
	}
}
