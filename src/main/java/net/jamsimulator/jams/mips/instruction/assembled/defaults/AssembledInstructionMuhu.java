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
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRSOPInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionMuhu;

public class AssembledInstructionMuhu extends AssembledRSOPInstruction {

	public AssembledInstructionMuhu(int sourceRegister, int targetRegister, int destinationRegister,
									Instruction origin, BasicInstruction<AssembledInstructionMuhu> basicOrigin) {
		super(InstructionMuhu.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, InstructionMuhu.SOP_CODE,
				InstructionMuhu.FUNCTION_CODE, origin, basicOrigin);
	}

	public AssembledInstructionMuhu(int instructionCode, Instruction origin, BasicInstruction<AssembledInstructionMuhu> basicOrigin) {
		super(instructionCode, origin, basicOrigin);
	}

	@Override
	public String parametersToString(String registersStart) {
		return registersStart + getDestinationRegister()
				+ ", " + registersStart + getSourceRegister()
				+ ", " + registersStart + getTargetRegister();
	}
}