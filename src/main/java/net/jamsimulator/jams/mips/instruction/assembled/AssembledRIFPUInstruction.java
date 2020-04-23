package net.jamsimulator.jams.mips.instruction.assembled;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;

/**
 * Represents a compiled Register-Immediate FPU instruction. Register-Immediate FPU instruction is composed of an 11-bit immediate,
 * one source register, one target register, one subcode and one operation code.
 */
public abstract class AssembledRIFPUInstruction extends AssembledInstruction {

	public static final int IMMEDIATE_MASK = 0x7FF;
	public static final int SOURCE_REGISTER_SHIFT = 11;
	public static final int SOURCE_REGISTER_MASK = 0x1F;
	public static final int TARGET_REGISTER_SHIFT = 16;
	public static final int TARGET_REGISTER_MASK = 0x1F;
	public static final int SUBCODE_SHIFT = 21;
	public static final int SUBCODE_MASK = 0x1F;

	/**
	 * Creates a compiled Register-Immediate FPU instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param value       the value of the instruction.
	 * @param origin      the origin instruction.
	 * @param basicOrigin the origin basic instruction.
	 */
	public AssembledRIFPUInstruction(int value, Instruction origin, BasicInstruction<?> basicOrigin) {
		super(value, origin, basicOrigin);
	}

	/**
	 * Creates a compiled Register-Immediate FPU instruction using an operation code, a subcode, a target register, a source register,
	 * an immediate, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param operationCode  the operation code.
	 * @param subcode        the subcode.
	 * @param targetRegister the target register.
	 * @param immediate      the immediate.
	 * @param origin         the origin instruction.
	 * @param basicOrigin    the origin basic instruction.
	 */
	public AssembledRIFPUInstruction(int operationCode, int subcode, int targetRegister, int sourceRegister,
									 int immediate, Instruction origin, BasicInstruction<?> basicOrigin) {
		super(calculateValue(operationCode, subcode, targetRegister, sourceRegister, immediate), origin, basicOrigin);
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
	 * Returns the subcode of this instruction.
	 *
	 * @return the subcode.
	 */
	public int getSubcode() {
		return value >> SUBCODE_SHIFT & SUBCODE_MASK;
	}


	static int calculateValue(int operationCode, int subcode, int targetRegister, int sourceRegister, int immediate) {
		int value = operationCode << AssembledInstruction.OPERATION_CODE_SHIFT;
		value += (subcode & SUBCODE_MASK) << SUBCODE_SHIFT;
		value += (targetRegister & TARGET_REGISTER_MASK) << TARGET_REGISTER_SHIFT;
		value += (sourceRegister & SOURCE_REGISTER_MASK) << SOURCE_REGISTER_SHIFT;
		value += immediate & IMMEDIATE_MASK;
		return value;
	}

}
