package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.Simulation;

public class SimulationUnlockEvent extends SimulationEvent {
	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link Simulation} thatcreated this event.
	 */
	public SimulationUnlockEvent(Simulation<?> simulation) {
		super(simulation);
	}
}
