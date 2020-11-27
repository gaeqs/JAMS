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

package net.jamsimulator.jams.mips.instruction.pseudo.defaults;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionSlti;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class PseudoInstructionSgtiRRI extends PseudoInstruction {


	public static final String NAME = "Set on greater than immediate";
	public static final String MNEMONIC = "sgti";

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

	public PseudoInstructionSgtiRRI() {
		super(MNEMONIC, PARAMETER_TYPES);
	}

	@Override
	public int getInstructionAmount(String[] parameters) {
		return 1;
	}

	@Override
	public AssembledInstruction[] assemble(InstructionSet set, int address, ParameterParseResult[] parameters) {
		//Get instructions
		Instruction slti = set.getInstruction(InstructionSlti.MNEMONIC, PARAMETER_TYPES).orElse(null);
		if (!(slti instanceof BasicInstruction))
			throw new AssemblerException("Basic instruction '" + InstructionSlti.MNEMONIC + "' not found.");

		//Get parameters
		ParameterParseResult[] sltiParameters = new ParameterParseResult[]{
				parameters[0],
				parameters[2],
				parameters[1]
		};

		return new AssembledInstruction[]{((BasicInstruction<?>) slti).assembleBasic(sltiParameters, this)};
	}
}
