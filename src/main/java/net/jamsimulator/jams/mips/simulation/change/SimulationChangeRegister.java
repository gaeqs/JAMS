package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * A {@link SimulationChange} that registers the value change of a {@link Register}.
 */
public class SimulationChangeRegister extends SimulationChange<Architecture> {

	private final Register register;
	private final int old;

	public SimulationChangeRegister(Register register, int old) {
		this.register = register;
		this.old = old;
	}

	@Override
	public void restore(Simulation<? extends Architecture> simulation) {
		register.setValue(old);
	}
}
