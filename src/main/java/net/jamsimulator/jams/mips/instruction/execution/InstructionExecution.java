package net.jamsimulator.jams.mips.instruction.execution;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.exception.RuntimeInstructionException;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.Validate;

public abstract class InstructionExecution<Arch extends Architecture, Inst extends AssembledInstruction> {

	protected final Simulation<Arch> simulation;
	protected final Inst instruction;

	public InstructionExecution(Simulation<Arch> simulation, Inst instruction) {
		Validate.notNull(simulation, "Simulation cannot be null!");
		Validate.notNull(instruction, "Instruction cannot be null!");
		this.simulation = simulation;
		this.instruction = instruction;
	}

	public Simulation<Arch> getSimulation() {
		return simulation;
	}

	public Inst getInstruction() {
		return instruction;
	}

	/**
	 * Throws a {@link RuntimeInstructionException} with the given message.
	 *
	 * @param message the message.
	 */
	protected void error(String message) {
		throw new RuntimeInstructionException(message);
	}


	/**
	 * Throws a {@link RuntimeInstructionException} with the given message.
	 *
	 * @param message the message.
	 * @param ex      the cause.
	 */
	protected void error(String message, Exception ex) {
		throw new RuntimeInstructionException(message, ex);
	}
}
