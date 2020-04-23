package net.jamsimulator.jams.mips.instruction.assembled;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;

/**
 * Represents a compiled I-Type Imm21 instruction. An I-Type Imm21 instruction is composed of an 21-bit immediate,
 * one destination register and one operation code.
 */
public abstract class AssembledI21Instruction extends AssembledInstruction {

	public static final int IMMEDIATE_MASK = 0x1FFFFF;
	public static final int DESTINATION_REGISTER_SHIFT = 21;
	public static final int DESTINATION_REGISTER_MASK = 0x1F;


	/**
	 * Creates a compiled I-Type Imm21 instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param value       the value of the instruction.
	 * @param origin      the origin instruction.
	 * @param basicOrigin the origin basic instruction.
	 */
	public AssembledI21Instruction(int value, Instruction origin, BasicInstruction<?> basicOrigin) {
		super(value, origin, basicOrigin);
	}

	/**
	 * Creates a compiled I-Type Imm21 instruction using an operation code, a destination register, an immediate,
	 * an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param operationCode       the operation code.
	 * @param destinationRegister the destination.
	 * @param immediate           the immediate.
	 * @param origin              the origin instruction.
	 * @param basicOrigin         the origin basic instruction.
	 */
	public AssembledI21Instruction(int operationCode, int destinationRegister, int immediate, Instruction origin, BasicInstruction<?> basicOrigin) {
		super(calculateValue(operationCode, destinationRegister, immediate), origin, basicOrigin);
	}

	/**
	 * Returns the immediate value of the instruction as an unsigned 21-bit number.
	 * For a signed version of this value see {@link #getImmediateAsSigned()}.
	 *
	 * @return the unsigned 21-bit immediate.
	 */
	public int getImmediate() {
		return value & IMMEDIATE_MASK;
	}

	/**
	 * Returns the immediate value of the instruction as a signed 21-bit number.
	 * For a unsigned version of this value see {@link #getImmediate()}.
	 *
	 * @return the signed 21-bit immediate.
	 */
	public int getImmediateAsSigned() {
		final int SHIFT = 32 - 21;
		int immediate = getImmediate();
		immediate <<= SHIFT;
		immediate >>= SHIFT;
		return immediate;
	}

	/**
	 * Returns the destination register of this instruction.
	 *
	 * @return the destination instruction.
	 */
	public int getDestinationRegister() {
		return value >> DESTINATION_REGISTER_SHIFT & DESTINATION_REGISTER_MASK;
	}

	static int calculateValue(int operationCode, int destinationRegister, int immediate) {
		int value = operationCode << AssembledInstruction.OPERATION_CODE_SHIFT;
		value += (destinationRegister & DESTINATION_REGISTER_MASK) << DESTINATION_REGISTER_SHIFT;
		value += immediate & IMMEDIATE_MASK;
		return value;
	}

}
