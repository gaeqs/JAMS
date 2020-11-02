package net.jamsimulator.jams.mips.instruction.basic;

/**
 * Represents an instruction that modifies the program counter.
 * In MIPS32r6 these instructions include all branches and jumps, nal, eret, eretnc, deret, wait and pause.
 */
public interface ControlTransferInstruction {

	/**
	 * Returns whether this instruction is compact.
	 * <p>
	 * Compact instructions don't have delay slots. Instead, they have forbidden slots:
	 * the next instruction won't be executed until the branch is solved and only if the jump is not taken.
	 *
	 * @return whether this instruction is compact.
	 */
	boolean isCompact();

}
