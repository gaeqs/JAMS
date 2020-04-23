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
 * Represents a compiled I-Type Imm26 instruction.
 * An I-Type Imm26 instruction is composed of an 26-bit immediate and one operation code.
 * <p>
 * This instruction has a similar structure as {@link AssembledJInstruction}.
 */
public abstract class AssembledI26Instruction extends AssembledInstruction {

	public static final int IMMEDIATE_MASK = 0x3FFFFFF;

	/**
	 * Creates a compiled I-Type Imm26 instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param value       the value of the instruction.
	 * @param origin      the origin instruction.
	 * @param basicOrigin the origin basic instruction.
	 */
	public AssembledI26Instruction(int value, Instruction origin, BasicInstruction<?> basicOrigin) {
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
	public AssembledI26Instruction(int operationCode, int address, Instruction origin, BasicInstruction<?> basicOrigin) {
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
		return (operationCode << AssembledInstruction.OPERATION_CODE_SHIFT) + (immediate & IMMEDIATE_MASK);
	}

}
