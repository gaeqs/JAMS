package net.jamsimulator.jams.mips.instruction.compiled;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;

/**
 * Represents a compiled I-Type Imm11 instruction. An I-Type Imm11 instruction is composed of an 11-bit immediate,
 * one destination register, two source registers (source and target) and one operation code.
 */
public abstract class CompiledI11Instruction extends CompiledInstruction {

	public static final int IMMEDIATE_MASK = 0x7FF;
	public static final int DESTINATION_REGISTER_SHIFT = 11;
	public static final int DESTINATION_REGISTER_MASK = 0x1F;
	public static final int TARGET_REGISTER_SHIFT = 16;
	public static final int TARGET_REGISTER_MASK = 0x1F;
	public static final int SOURCE_REGISTER_SHIFT = 21;
	public static final int SOURCE_REGISTER_MASK = 0x1F;

	/**
	 * Creates a compiled I-Type Imm11 instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param value       the value of the instruction.
	 * @param origin      the origin instruction.
	 * @param basicOrigin the origin basic instruction.
	 */
	public CompiledI11Instruction(int value, Instruction origin, BasicInstruction basicOrigin) {
		super(value, origin, basicOrigin);
	}

	/**
	 * Creates a compiled I-Type Imm11 instruction using an operation code, a source register, a target register, a destination register,
	 * an immediate, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param operationCode  the operation code.
	 * @param sourceRegister the source register .
	 * @param targetRegister the target register.
	 * @param immediate      the immediate.
	 * @param origin         the origin instruction.
	 * @param basicOrigin    the origin basic instruction.
	 */
	public CompiledI11Instruction(int operationCode, int sourceRegister, int targetRegister, int destinationRegister,
								  int immediate, Instruction origin, BasicInstruction basicOrigin) {
		super(calculateValue(operationCode, sourceRegister, targetRegister, destinationRegister, immediate), origin, basicOrigin);
	}

	/**
	 * Returns the immediate value of the instruction as an unsigned 11-bit number.
	 * For a signed version of this value see {@link #getImmediateAsSigned()}.
	 *
	 * @return the unsigned 11-bit immediate.
	 */
	public int getImmediate() {
		return value & IMMEDIATE_MASK;
	}

	/**
	 * Returns the immediate value of the instruction as a signed 11-bit number.
	 * For a unsigned version of this value see {@link #getImmediate()}.
	 *
	 * @return the signed 11-bit immediate.
	 */
	public int getImmediateAsSigned() {
		return (short) getImmediate();
	}

	/**
	 * Returns the target register of this instruction.
	 *
	 * @return the target instruction.
	 */
	public int getTargetRegister() {
		return value >> TARGET_REGISTER_SHIFT & TARGET_REGISTER_MASK;
	}

	/**
	 * Returns the source register of this instruction-
	 *
	 * @return the source register.
	 */
	public int getSourceRegister() {
		return value >> SOURCE_REGISTER_SHIFT & SOURCE_REGISTER_MASK;
	}


	static int calculateValue(int operationCode, int sourceRegister, int targetRegister, int destinationRegister, int immediate) {
		int value = operationCode << CompiledInstruction.OPERATION_CODE_SHIFT;
		value += (sourceRegister & SOURCE_REGISTER_MASK) << SOURCE_REGISTER_SHIFT;
		value += (targetRegister & TARGET_REGISTER_MASK) << TARGET_REGISTER_SHIFT;
		value += (destinationRegister & DESTINATION_REGISTER_MASK) << DESTINATION_REGISTER_SHIFT;
		value += immediate & IMMEDIATE_MASK;
		return value;
	}

}
