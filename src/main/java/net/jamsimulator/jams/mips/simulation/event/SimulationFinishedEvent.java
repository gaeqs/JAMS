package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

/**
 * This event is called when the executing code of a simulation has exited.
 * This method is invoked by the execution itself.
 */
public class SimulationFinishedEvent extends SimulationEvent {

	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link MIPSSimulation} thatcreated this event.
	 */
	public SimulationFinishedEvent(MIPSSimulation<?> simulation) {
		super(simulation);
	}
}
