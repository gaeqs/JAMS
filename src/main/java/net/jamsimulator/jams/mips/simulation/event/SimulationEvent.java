package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

/**
 * Represents an {@link Event} related to a {@link MIPSSimulation}.
 */
public class SimulationEvent extends Event {

	private final MIPSSimulation<?> simulation;

	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link MIPSSimulation} thatcreated this event.
	 */
	public SimulationEvent(MIPSSimulation<?> simulation) {
		this.simulation = simulation;
	}

	/**
	 * Returns the {@link MIPSSimulation} that created this event.
	 *
	 * @return the {@link MIPSSimulation}.
	 */
	public MIPSSimulation<?> getSimulation() {
		return simulation;
	}
}
