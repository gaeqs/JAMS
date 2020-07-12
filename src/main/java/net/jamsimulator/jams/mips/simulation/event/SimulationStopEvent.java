package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.Simulation;

public class SimulationStopEvent extends SimulationEvent {
	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link Simulation} tha created this event.
	 */
	public SimulationStopEvent(Simulation<?> simulation) {
		super(simulation);
	}
}
