package net.jamsimulator.jams.mips.instruction.compiled;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;

/**
 * Represents a compiled R-Type instruction. A R-Type instruction is composed of a function code,
 * a "shamt" (unsigned 5-bit immediate), two source registers (source and target), one destination register
 * and one operation code.
 */
public abstract class CompiledRSOPInstruction extends CompiledInstruction {

	public static final int FUNCTION_CODE_MASK = 0X3F;
	public static final int SOP_SHIFT = 6;
	public static final int SOP_MASK = 0x1F;
	public static final int DESTINATION_REGISTER_SHIFT = 11;
	public static final int DESTINATION_REGISTER_MASK = 0x1F;
	public static final int TARGET_REGISTER_SHIFT = 16;
	public static final int TARGET_REGISTER_MASK = 0x1F;
	public static final int SOURCE_REGISTER_SHIFT = 21;
	public static final int SOURCE_REGISTER_MASK = 0x1F;

	/**
	 * Creates a compiled R-Type instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param value       the value of the instruction.
	 * @param origin      the origin instruction.
	 * @param basicOrigin the origin basic instruction.
	 */
	public CompiledRSOPInstruction(int value, Instruction origin, BasicInstruction basicOrigin) {
		super(value, origin, basicOrigin);
	}


	/**
	 * Creates a compiled R-Type instruction using an operation code, a source register, a target register, a destination register,
	 * a shift amount 5-bit immediate, a function code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param operationCode       the operation code.
	 * @param sourceRegister      the source register.
	 * @param targetRegister      the target register.
	 * @param destinationRegister the destination register.
	 * @param sopCode             the sop code.
	 * @param functionCode        the function code.
	 * @param origin              the origin instruction.
	 * @param basicOrigin         the origin basic instruction.
	 */
	public CompiledRSOPInstruction(int operationCode, int sourceRegister, int targetRegister, int destinationRegister,
								   int sopCode, int functionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(calculateValue(operationCode, sourceRegister, targetRegister, destinationRegister, sopCode, functionCode),
				origin, basicOrigin);
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
	 * Returns the sop code of the instruction.
	 *
	 * @return the sop code.
	 */
	public int getSopCode() {
		return value >>> SOP_SHIFT & SOP_MASK;
	}

	/**
	 * Returns the destination register of the instruction.
	 *
	 * @return the destination register.
	 */
	public int getDestinationRegister() {
		return value >> DESTINATION_REGISTER_SHIFT & DESTINATION_REGISTER_MASK;
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

	static int calculateValue(int operationCode, int sourceRegister, int targetRegister, int destinationRegister,
							  int sopCode, int functionCode) {
		int value = operationCode << CompiledInstruction.OPERATION_CODE_SHIFT;
		value += (sourceRegister & SOURCE_REGISTER_MASK) << SOURCE_REGISTER_SHIFT;
		value += (targetRegister & TARGET_REGISTER_MASK) << TARGET_REGISTER_SHIFT;
		value += (destinationRegister & DESTINATION_REGISTER_MASK) << DESTINATION_REGISTER_SHIFT;
		value += (sopCode & SOP_MASK) << SOP_SHIFT;
		value += functionCode & FUNCTION_CODE_MASK;
		return value;
	}

}
