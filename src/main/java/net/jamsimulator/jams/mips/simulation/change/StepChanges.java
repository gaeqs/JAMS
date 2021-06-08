package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

import java.util.LinkedList;

/**
 * Instances of this class collects all changes made in a step.
 */
public class StepChanges<Arch extends Architecture> {

	private final LinkedList<SimulationChange<? super Arch>> changes;
	private final Object lock = new Object();

	public StepChanges() {
		changes = new LinkedList<>();
	}

	public void addChange(SimulationChange<? super Arch> change) {
		synchronized (lock) {
			changes.addFirst(change);
		}
	}

	public void restore(MIPSSimulation<? extends Arch> simulation) {
		synchronized (lock) {
			changes.forEach(target -> target.restore(simulation));
			changes.clear();
		}
	}

	public void removeCacheChanges(Memory last) {
		synchronized (lock) {
			var iterator = changes.iterator();
			while (iterator.hasNext()) {
				var next = iterator.next();
				if (next instanceof SimulationChangeCacheOperation) iterator.remove();
				if (next instanceof SimulationChangeMemoryByte) ((SimulationChangeMemoryByte) next).setMemory(last);
				if (next instanceof SimulationChangeMemoryWord) ((SimulationChangeMemoryWord) next).setMemory(last);
			}
		}
	}

}
