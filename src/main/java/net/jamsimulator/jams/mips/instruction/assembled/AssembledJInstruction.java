/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.mips.instruction.assembled;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;

/**
 * Represents a compiled J-Type instruction. A J-Type intruction is composed of a 26-bit address and an operation code.
 */
public abstract class AssembledJInstruction extends AssembledInstruction {

	public static final int ADDRESS_SHIFT = 2;
	public static final int ADDRESS_MASK = 0x3FFFFFF;
	public static final int PC_MASK = 0xF0000000;

	/**
	 * Creates a compiled J-Type instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param value       the value of the instruction.
	 * @param origin      the origin instruction.
	 * @param basicOrigin the origin basic instruction.
	 */
	public AssembledJInstruction(int value, Instruction origin, BasicInstruction<?> basicOrigin) {
		super(value, origin, basicOrigin);
	}

	/**
	 * Creates a compiled J-Type instruction using an operation code, an address, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param operationCode the operation code of the instruction.
	 * @param address       the byte-level address of the instruction.
	 * @param origin        the origin instruction.
	 * @param basicOrigin   the origin basic instruction.
	 */
	public AssembledJInstruction(int operationCode, int address, Instruction origin, BasicInstruction<?> basicOrigin) {
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
		// The addres must be calculated using getAddress() + 4.
		// The low 28 bits of the target address is the instr_index field shifted left 2bits.
		// The remaining upper bits are the corre-sponding bits of the address of the instruction
		// in the delay slot (not the branch itself).
		// MIPS32 Instruction Set Manual, page 194.
		return (pc & PC_MASK) + ((getAddress() + 4) << ADDRESS_SHIFT);
	}


	static int calculateValue(int operationCode, int address) {
		return (operationCode << AssembledInstruction.OPERATION_CODE_SHIFT) + (address >>> ADDRESS_SHIFT & ADDRESS_MASK);
	}

}
