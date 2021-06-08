package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

/**
 * This event is called when all caches inside a simulation are reset.
 */
public class SimulationCachesResetEvent extends SimulationEvent {

	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link MIPSSimulation} that created this event.
	 */
	public SimulationCachesResetEvent(MIPSSimulation<?> simulation) {
		super(simulation);
	}
}
