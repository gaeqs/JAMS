package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

public class SimulationLockEvent extends SimulationEvent {
	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link MIPSSimulation} thatcreated this event.
	 */
	public SimulationLockEvent(MIPSSimulation<?> simulation) {
		super(simulation);
	}
}
