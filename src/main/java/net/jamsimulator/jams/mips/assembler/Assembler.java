package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;

/**
 * Represents an assembler. An assembler transforms assembly code into machine code.
 * <p>
 * To use a implementation of this class you must invoke {@link #assemble()} to assemble the code.
 * Then, invoke {@link #createSimulation(Architecture)} to create a {@link Simulation}.
 */
public interface Assembler {

	/**
	 * Assembles the code. This may fail if the code was already assembled or the code has any error.
	 *
	 * @throws net.jamsimulator.jams.mips.assembler.exception.AssemblerException when the code is already assembled or the code has any error.
	 */
	void assemble();

	/**
	 * Returns whether the code inside this assembler has been assembled.
	 *
	 * @return whether the code inside this assembler has been assembled.
	 */
	boolean isAssembled();

	/**
	 * Creates a {@link Simulation} that matches the given {@link Architecture}.
	 *
	 * @param architecture the {@link Architecture}.
	 * @param <Arch>       the architecture type.
	 * @return the {@link Simulation}.
	 * @throws IllegalStateException when the code is not assembled.
	 */
	<Arch extends Architecture> Simulation<Arch> createSimulation(Arch architecture);

	/**
	 * Returns the {@link InstructionSet} used by this assembler.
	 *
	 * @return the {@link InstructionSet}.
	 */
	InstructionSet getInstructionSet();

	/**
	 * Returns the {@link DirectiveSet} used by this assembler.
	 *
	 * @return the {@link DirectiveSet}.
	 */
	DirectiveSet getDirectiveSet();

	/**
	 * Returns the {@link Registers register set} used by this assembler.
	 * {@link Simulation}s created by this assembler will use a copy of these registers.
	 *
	 * @return the {@link Registers register set}.
	 */
	Registers getRegisters();

	/**
	 * Returns the {@link Memory} used by this assembler.
	 * <p>
	 * This memory should be an empty memory when the code is not assembled and a memory
	 * with data and the assembled code when the code is assembled.
	 * <p>
	 * {@link Simulation}s created by this assembler will use a copy of this memory.
	 *
	 * @return the {@link Memory}.
	 */
	Memory getMemory();

}
