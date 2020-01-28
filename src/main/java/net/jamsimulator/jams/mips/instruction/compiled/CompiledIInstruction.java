package net.jamsimulator.jams.mips.instruction.compiled;

import net.jamsimulator.jams.mips.instruction.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.Instruction;

/**
 * Represents a compiled I-Instruction. An I-Instruction is composed of an 16-bit immediate,
 * * one source register, one target register and one operation code.
 */
public abstract class CompiledIInstruction extends CompiledInstruction {

	public static final int IMMEDIATE_MASK = 0xFFFF;
	public static final int TARGET_REGISTER_SHIFT = 16;
	public static final int TARGET_REGISTER_MASK = 0x1F;
	public static final int SOURCE_REGISTER_SHIFT = 21;
	public static final int SOURCE_REGISTER_MASK = 0x1F;

	/**
	 * Creates a compiled I instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param value       the value of the instruction.
	 * @param origin      the origin instruction.
	 * @param basicOrigin the origin basic instruction.
	 */
	public CompiledIInstruction(int value, Instruction origin, BasicInstruction basicOrigin) {
		super(value, origin, basicOrigin);
	}

	/**
	 * Creates a compiled I instruction using an operation code, a source register, a target register, an immediate,
	 * an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param operationCode  the operation code.
	 * @param sourceRegister the source register .
	 * @param targetRegister the target register.
	 * @param immediate      the immediate.
	 * @param origin         the origin instruction.
	 * @param basicOrigin    the origin basic instruction.
	 */
	public CompiledIInstruction(int operationCode, int sourceRegister, int targetRegister, int immediate, Instruction origin, BasicInstruction basicOrigin) {
		super(calculateValue(operationCode, sourceRegister, targetRegister, immediate), origin, basicOrigin);
	}

	/**
	 * Returns the immediate value of the instruction as an unsigned 16-bit number.
	 * For a signed version of this value see {@link #getImmediateAsSigned()}.
	 *
	 * @return the unsigned 16-bit immediate.
	 */
	public int getImmediate() {
		return value & IMMEDIATE_MASK;
	}

	/**
	 * Returns the immediate value of the instruction as a signed 16-bit number.
	 * For a unsigned version of this value see {@link #getImmediate()}.
	 *
	 * @return the signed 16-bit immediate.
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


	static int calculateValue(int operationCode, int sourceRegister, int targetRegister, int immediate) {
		int value = operationCode << CompiledInstruction.OPERATION_CODE_SHIFT;
		value += (sourceRegister & SOURCE_REGISTER_MASK) << SOURCE_REGISTER_SHIFT;
		value += (targetRegister & TARGET_REGISTER_MASK) << TARGET_REGISTER_SHIFT;
		value += immediate & IMMEDIATE_MASK;
		return value;
	}

}
