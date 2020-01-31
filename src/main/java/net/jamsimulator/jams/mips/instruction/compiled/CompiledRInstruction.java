package net.jamsimulator.jams.mips.instruction.compiled;

import net.jamsimulator.jams.mips.instruction.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.Instruction;

/**
 * Represents a compiled R-Instruction. A R-Instruction is composed of a function code,
 * a "shamt" (unsigned 5-bit immediate), two source registers (source and target), one destination register
 * and one operation code.
 */
public abstract class CompiledRInstruction extends CompiledInstruction {

	public static final int FUNCTION_CODE_MASK = 0X3F;
	public static final int SHIFT_AMOUNT_SHIFT = 6;
	public static final int SHIFT_AMOUNT_MASK = 0x1F;
	public static final int DESTINATION_REGISTER_SHIFT = 11;
	public static final int DESTINATION_REGISTER_MASK = 0x1F;
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
	public CompiledRInstruction(int value, Instruction origin, BasicInstruction basicOrigin) {
		super(value, origin, basicOrigin);
	}


	/**
	 * Creates a compiled I instruction using an operation code, a source register, a target register, a destination register,
	 * a shift amount 5-bit immediate, a function code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param operationCode       the operation code.
	 * @param sourceRegister      the source register.
	 * @param targetRegister      the target register.
	 * @param destinationRegister the destination register.
	 * @param shiftAmount         the shift amount immediate value.
	 * @param functionCode        the function code.
	 * @param origin              the origin instruction.
	 * @param basicOrigin         the origin basic instruction.
	 */
	public CompiledRInstruction(int operationCode, int sourceRegister, int targetRegister, int destinationRegister,
								int shiftAmount, int functionCode, Instruction origin, BasicInstruction basicOrigin) {
		super(calculateValue(operationCode, sourceRegister, targetRegister, destinationRegister, shiftAmount, functionCode),
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
	 * Returns the shift amount immediate of the instruction.
	 *
	 * @return the shift amount immediate.
	 */
	public int getShiftAmount() {
		return value >>> SHIFT_AMOUNT_SHIFT & SHIFT_AMOUNT_MASK;
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
							  int shiftAmount, int functionCode) {
		int value = operationCode << CompiledInstruction.OPERATION_CODE_SHIFT;
		value += (sourceRegister & SOURCE_REGISTER_MASK) << SOURCE_REGISTER_SHIFT;
		value += (targetRegister & TARGET_REGISTER_MASK) << TARGET_REGISTER_SHIFT;
		value += (destinationRegister & DESTINATION_REGISTER_MASK) << DESTINATION_REGISTER_SHIFT;
		value += (shiftAmount & SHIFT_AMOUNT_MASK) << SHIFT_AMOUNT_SHIFT;
		value += functionCode & FUNCTION_CODE_MASK;
		return value;
	}

}
