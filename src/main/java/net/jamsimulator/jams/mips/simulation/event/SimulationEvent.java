package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * Represents an {@link Event} related to a {@link Simulation}.
 */
public class SimulationEvent extends Event {

	private final Simulation<?> simulation;

	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link Simulation} thatcreated this event.
	 */
	public SimulationEvent(Simulation<?> simulation) {
		this.simulation = simulation;
	}

	/**
	 * Returns the {@link Simulation} that created this event.
	 *
	 * @return the {@link Simulation}.
	 */
	public Simulation<?> getSimulation() {
		return simulation;
	}
}
