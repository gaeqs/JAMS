package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * This event is called when the executing code of a simulation has exited.
 * This method is invoked by the execution itself.
 */
public class SimulationFinishedEvent extends SimulationEvent {

	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link Simulation} thatcreated this event.
	 */
	public SimulationFinishedEvent(Simulation<?> simulation) {
		super(simulation);
	}
}
