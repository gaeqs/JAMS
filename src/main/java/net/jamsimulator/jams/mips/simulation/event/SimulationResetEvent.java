package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

public class SimulationResetEvent extends SimulationEvent {

	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link MIPSSimulation} thatcreated this event.
	 */
	public SimulationResetEvent(MIPSSimulation<?> simulation) {
		super(simulation);
	}
}
