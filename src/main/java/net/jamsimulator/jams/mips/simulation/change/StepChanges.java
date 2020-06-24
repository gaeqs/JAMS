package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.LinkedList;

/**
 * Instances of this class collects all changes made in a step.
 */
public class StepChanges<Arch extends Architecture> {

	private final LinkedList<SimulationChange<? super Arch>> changes;

	public StepChanges() {
		changes = new LinkedList<>();
	}

	public void addChange(SimulationChange<? super Arch> change) {
		changes.addFirst(change);
	}

	public void restore(Simulation<? extends Arch> simulation) {
		changes.forEach(target -> target.restore(simulation));
	}

}