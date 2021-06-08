package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

public class SimulationUnlockEvent extends SimulationEvent {
	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link MIPSSimulation} thatcreated this event.
	 */
	public SimulationUnlockEvent(MIPSSimulation<?> simulation) {
		super(simulation);
	}
}
