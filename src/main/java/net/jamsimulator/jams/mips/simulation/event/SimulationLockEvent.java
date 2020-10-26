package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.Simulation;

public class SimulationLockEvent extends SimulationEvent {
	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link Simulation} thatcreated this event.
	 */
	public SimulationLockEvent(Simulation<?> simulation) {
		super(simulation);
	}
}
