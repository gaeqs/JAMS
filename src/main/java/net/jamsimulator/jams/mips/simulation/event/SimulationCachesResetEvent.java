package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * This event is called when all caches inside a simulation are reset.
 */
public class SimulationCachesResetEvent extends SimulationEvent {

	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link Simulation} that created this event.
	 */
	public SimulationCachesResetEvent(Simulation<?> simulation) {
		super(simulation);
	}
}
