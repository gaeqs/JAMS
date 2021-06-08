package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

/**
 * Represents a change inside a {@link MIPSSimulation}.
 * <p>
 * These classes are used to register changes inside a {@link MIPSSimulation}, allowing
 * to undo done steps.
 *
 * @param <Arch> the architecture of the simulation.
 */
public abstract class SimulationChange<Arch extends Architecture> {

	/**
	 * Restores the change made.
	 * <p>
	 * This should only be executed if this change is the last one to be made and the given {@link MIPSSimulation}
	 * should be the same one that made the change.
	 * Executing this method without checking those conditions may cause unexpected results.
	 *
	 * @param simulation the simulation to restore.
	 */
	public abstract void restore(MIPSSimulation<? extends Arch> simulation);

}
