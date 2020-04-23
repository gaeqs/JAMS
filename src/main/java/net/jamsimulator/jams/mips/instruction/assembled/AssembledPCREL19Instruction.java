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
 * Represents a compiled PCREL instruction. An PCREL instruction is composed of an 19-bit immediate,
 * one PCREL function code one source register and one operation code.
 */
public abstract class AssembledPCREL19Instruction extends AssembledInstruction {

	public static final int IMMEDIATE_MASK = 0x7FFFF;
	public static final int PCREL_SHIFT = 19;
	public static final int PCREL_MASK = 0x3;
	public static final int SOURCE_REGISTER_SHIFT = 21;
	public static final int SOURCE_REGISTER_MASK = 0x1F;

	/**
	 * Creates a compiled PCREL instruction using an instruction code, an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param value       the value of the instruction.
	 * @param origin      the origin instruction.
	 * @param basicOrigin the origin basic instruction.
	 */
	public AssembledPCREL19Instruction(int value, Instruction origin, BasicInstruction<?> basicOrigin) {
		super(value, origin, basicOrigin);
	}

	/**
	 * Creates a compiled PCREL instruction using an operation code, a source register, a PCREL code, an immediate,
	 * an origin {@link Instruction} and an origin {@link BasicInstruction}.
	 *
	 * @param operationCode  the operation code.
	 * @param sourceRegister the source register .
	 * @param pcrel          the pc relative code.
	 * @param immediate      the immediate.
	 * @param origin         the origin instruction.
	 * @param basicOrigin    the origin basic instruction.
	 */
	public AssembledPCREL19Instruction(int operationCode, int sourceRegister, int pcrel, int immediate, Instruction origin, BasicInstruction<?> basicOrigin) {
		super(calculateValue(operationCode, sourceRegister, pcrel, immediate), origin, basicOrigin);
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
		final int SHIFT = 32 - 19;
		int immediate = getImmediate();
		immediate <<= SHIFT;
		immediate >>= SHIFT;
		return immediate;
	}

	/**
	 * Returns the pc relative of this instruction.
	 *
	 * @return the pc relative instruction.
	 */
	public int getPCREL() {
		return value >> PCREL_SHIFT & PCREL_MASK;
	}

	/**
	 * Returns the source register of this instruction-
	 *
	 * @return the source register.
	 */
	public int getSourceRegister() {
		return value >> SOURCE_REGISTER_SHIFT & SOURCE_REGISTER_MASK;
	}


	static int calculateValue(int operationCode, int sourceRegister, int pcrel, int immediate) {
		int value = operationCode << AssembledInstruction.OPERATION_CODE_SHIFT;
		value += (sourceRegister & SOURCE_REGISTER_MASK) << SOURCE_REGISTER_SHIFT;
		value += (pcrel & PCREL_MASK) << PCREL_SHIFT;
		value += immediate & IMMEDIATE_MASK;
		return value;
	}

}
