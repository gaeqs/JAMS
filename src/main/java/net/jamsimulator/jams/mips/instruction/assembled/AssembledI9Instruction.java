package net.jamsimulator.jams.mips.instruction.assembled;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;

/**
 * Represents a compiled I-Type Off9 instruction. An I-Type Off9 instruction is composed of a function code,
 * an 9-bit offset, one target register, one base register and one operation code.
 */
public abstract class AssembledI9Instruction extends AssembledInstruction {

	public static final int FUNCTION_CODE_MASK = 0X3F;
	public static final int OFFSET_SHIFT = 7;
	public static final int OFFSET_MASK = 0x1FF;
	public static final int TARGET_REGISTER_SHIFT = 16;
	public static final int TARGET_REGISTER_MASK = 0x1F;
	public static final int BASE_REGISTER_SHIFT = 21;
	public static final int BASE_REGISTER_MASK = 0x1F;

	/**
	 * Creates a compiled I-Type Off9 instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param value       the value of the instruction.
	 * @param origin      the origin instruction.
	 * @param basicOrigin the origin basic instruction.
	 */
	public AssembledI9Instruction(int value, Instruction origin, BasicInstruction<?> basicOrigin) {
		super(value, origin, basicOrigin);
	}

	/**
	 * Creates a compiled I-Type Off9 instruction using an operation code, a base register, a target register, an offset, a function code,
	 * an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param operationCode  the operation code.
	 * @param baseRegister   the source register .
	 * @param targetRegister the target register.
	 * @param offset         the immediate.
	 * @param functionCode   the function code.
	 * @param origin         the origin instruction.
	 * @param basicOrigin    the origin basic instruction.
	 */
	public AssembledI9Instruction(int operationCode, int baseRegister, int targetRegister, int offset, int functionCode, Instruction origin, BasicInstruction<?> basicOrigin) {
		super(calculateValue(operationCode, baseRegister, targetRegister, offset, functionCode), origin, basicOrigin);
	}

	/**
	 * Returns the function code of the instruction.
	 *
	 * @return the function code.
	 */
	public int getFunctionCode() {
		return value & FUNCTION_CODE_MASK;
	}

	/**
	 * Returns the offset value of the instruction as an unsigned 9-bit number.
	 * For a signed version of this value see {@link #getOffsetAsSigned()}.
	 *
	 * @return the unsigned 9-bit immediate.
	 */
	public int getOffset() {
		return value << OFFSET_SHIFT & OFFSET_MASK;
	}

	/**
	 * Returns the offset value of the instruction as a signed 9-bit number.
	 * For a unsigned version of this value see {@link #getOffset()}.
	 *
	 * @return the signed 9-bit immediate.
	 */
	public int getOffsetAsSigned() {
		return (short) getOffset();
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
	public int getBaseRegister() {
		return value >> BASE_REGISTER_SHIFT & BASE_REGISTER_MASK;
	}


	static int calculateValue(int operationCode, int baseRegister, int targetRegister, int offset, int functionCode) {
		int value = operationCode << AssembledInstruction.OPERATION_CODE_SHIFT;
		value += (baseRegister & BASE_REGISTER_MASK) << BASE_REGISTER_SHIFT;
		value += (targetRegister & TARGET_REGISTER_MASK) << TARGET_REGISTER_SHIFT;
		value += (offset & OFFSET_MASK) << OFFSET_SHIFT;
		value += functionCode & FUNCTION_CODE_MASK;
		return value;
	}

}
