package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.instruction.execution.InstructionExecution;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

/**
 * A {@link SimulationChange} that registers the unlock of a {@link Register}.
 */
public class SimulationChangeRegisterUnlock extends SimulationChange<Architecture> {

	private final Register register;
	private final InstructionExecution<?, ?> execution;

	public SimulationChangeRegisterUnlock(Register register, InstructionExecution<?, ?> execution) {
		this.register = register;
		this.execution = execution;
	}

	@Override
	public void restore(MIPSSimulation<? extends Architecture> simulation) {
		register.lockFirst(execution);
	}
}
