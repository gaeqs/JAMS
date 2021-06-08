package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

/**
 * This event is called when the execution of a simulation has stopped.
 * This method is invoked by the execution itself.
 */
public class SimulationStartEvent extends SimulationEvent {
	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link MIPSSimulation} thatcreated this event.
	 */
	public SimulationStartEvent(MIPSSimulation<?> simulation) {
		super(simulation);
	}
}
