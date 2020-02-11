package net.jamsimulator.jams.mips.instruction.compiled;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;

/**
 * Represents a compiled I-Type Imm26 instruction.
 * An I-Type Imm26 instruction is composed of an 26-bit immediate and one operation code.
 * <p>
 * This instruction has a similar structure as {@link CompiledJInstruction}.
 */
public abstract class CompiledI26Instruction extends CompiledInstruction {

	public static final int IMMEDIATE_MASK = 0x3FFFFFF;

	/**
	 * Creates a compiled I-Type Imm26 instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param value       the value of the instruction.
	 * @param origin      the origin instruction.
	 * @param basicOrigin the origin basic instruction.
	 */
	public CompiledI26Instruction(int value, Instruction origin, BasicInstruction basicOrigin) {
		super(value, origin, basicOrigin);
	}

	/**
	 * Creates a compiled I-Type Imm26 instruction using an operation code, an immediate, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param operationCode the operation code of the instruction.
	 * @param address       the byte-level address of the instruction.
	 * @param origin        the origin instruction.
	 * @param basicOrigin   the origin basic instruction.
	 */
	public CompiledI26Instruction(int operationCode, int address, Instruction origin, BasicInstruction basicOrigin) {
		super(calculateValue(operationCode, address), origin, basicOrigin);
	}

	/**
	 * Returns the immediate value of the instruction as an unsigned 26-bit number.
	 * For a signed version of this value see {@link #getImmediateAsSigned()}.
	 *
	 * @return the unsigned 26-bit immediate.
	 */
	public int getImmediate() {
		return value & IMMEDIATE_MASK;
	}

	/**
	 * Returns the immediate value of the instruction as a signed 26-bit number.
	 * For a unsigned version of this value see {@link #getImmediate()}.
	 *
	 * @return the signed 26-bit immediate.
	 */
	public int getImmediateAsSigned() {
		final int SHIFT = 32 - 26;
		int immediate = getImmediate();
		immediate <<= SHIFT;
		immediate >>= SHIFT;
		return immediate;
	}


	static int calculateValue(int operationCode, int immediate) {
		return (operationCode << CompiledInstruction.OPERATION_CODE_SHIFT) + (immediate & IMMEDIATE_MASK);
	}

}
