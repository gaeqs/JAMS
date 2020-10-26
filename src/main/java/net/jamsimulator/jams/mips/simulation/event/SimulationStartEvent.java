package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * This event is called when the execution of a simulation has stopped.
 * This method is invoked by the execution itself.
 */
public class SimulationStartEvent extends SimulationEvent {
	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link Simulation} thatcreated this event.
	 */
	public SimulationStartEvent(Simulation<?> simulation) {
		super(simulation);
	}
}
