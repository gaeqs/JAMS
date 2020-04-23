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

package net.jamsimulator.jams.mips.instruction.assembled.defaults;

import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAlign;

public class AssembledInstructionAlign extends AssembledRInstruction {

	public static final int ALIGN_CODE_SHIFT = 8;
	public static final int ALIGN_CODE_MASK = 0x7;
	public static final int SHIFT_AMOUNT_MASK = 0x3;

	public AssembledInstructionAlign(int sourceRegister, int targetRegister, int destinationRegister, int shiftAmount,
									 Instruction origin, BasicInstruction<AssembledInstructionAlign> basicOrigin) {
		super(InstructionAlign.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister,
				(InstructionAlign.ALIGN_CODE << (ALIGN_CODE_SHIFT - SHIFT_AMOUNT_SHIFT)) + shiftAmount,
				InstructionAlign.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionAlign(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionAlign> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}


	@Override
	public int getShiftAmount() {
		return super.getShiftAmount() & SHIFT_AMOUNT_MASK;
	}

	public int getAlignCode() {
		return value >> ALIGN_CODE_SHIFT & ALIGN_CODE_MASK;
	}
}
