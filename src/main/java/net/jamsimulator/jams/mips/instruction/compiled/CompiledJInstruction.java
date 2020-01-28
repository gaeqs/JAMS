package net.jamsimulator.jams.mips.instruction.compiled;

import net.jamsimulator.jams.mips.instruction.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.Instruction;

/**
 * Represents a compiled J-Instruction. A J-Instruction is composed of a 26-bit address and an operation code.
 */
public abstract class CompiledJInstruction extends CompiledInstruction {

	public static final int ADDRESS_SHIFT = 2;
	public static final int ADDRESS_MASK = 0x3FFFFFF;
	public static final int PC_MASK = 0xF0000000;

	/**
	 * Creates a compiled I instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param value       the value of the instruction.
	 * @param origin      the origin instruction.
	 * @param basicOrigin the origin basic instruction.
	 */
	public CompiledJInstruction(int value, Instruction origin, BasicInstruction basicOrigin) {
		super(value, origin, basicOrigin);
	}

	/**
	 * Creates a compiled I instruction using an operation code, an address, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param operationCode the operation code of the instruction.
	 * @param address       the address of the instruction.
	 * @param origin        the origin instruction.
	 * @param basicOrigin   the origin basic instruction.
	 */
	public CompiledJInstruction(int operationCode, int address, Instruction origin, BasicInstruction basicOrigin) {
		super(calculateValue(operationCode, address), origin, basicOrigin);
	}

	/**
	 * Returns the relative address code of the instruction. This shouldn't be used if you want to get
	 * a value from memory. Use {@link #getAbsoluteAddress(int)} instead.
	 *
	 * @return the relative address.
	 */
	public int getAddress() {
		return value & ADDRESS_MASK;
	}

	/**
	 * Returns the absolute address code of the instruction. This instruction requires the current program count
	 * to work.
	 *
	 * @param pc the current program count value.
	 * @return the absolute address.
	 */
	public int getAbsoluteAddress(int pc) {
		return (pc & PC_MASK) + (getAddress() << ADDRESS_SHIFT);
	}


	static int calculateValue(int operationCode, int address) {
		return (operationCode << CompiledInstruction.OPERATION_CODE_SHIFT) + (address >>> ADDRESS_SHIFT & ADDRESS_MASK);
	}

}
