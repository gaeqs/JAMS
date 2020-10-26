package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.Simulation;

public class SimulationResetEvent extends SimulationEvent {

	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link Simulation} thatcreated this event.
	 */
	public SimulationResetEvent(Simulation<?> simulation) {
		super(simulation);
	}
}
