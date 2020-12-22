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
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAui;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionMtc1;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionOri;
import net.jamsimulator.jams.mips.instruction.pseudo.PseudoInstruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

public class PseudoInstructionLifRF extends PseudoInstruction {

	public static final String MNEMONIC = "lif";

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.FLOAT_REGISTER, ParameterType.FLOAT};

	private static final ParameterType[] AUI_BASIC_PARAMETER_TYPES =
			new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};
	private static final ParameterType[] ORI_BASIC_PARAMETER_TYPES =
			new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

	private static final ParameterType[] MTC1_BASIC_PARAMETER_TYPES =
			new ParameterType[]{ParameterType.REGISTER, ParameterType.FLOAT_REGISTER};

	private static final ParameterParseResult ZERO = ParameterParseResult.builder().register(0).build();
	private static final ParameterParseResult AT = ParameterParseResult.builder().register(1).build();

	public PseudoInstructionLifRF() {
		super(MNEMONIC, PARAMETER_TYPES);
	}

	@Override
	public int getInstructionAmount(String[] parameters) {
		return 3;
	}

	@Override
	public AssembledInstruction[] assemble(InstructionSet set, int address, ParameterParseResult[] parameters) {
		//Get instructions
		Instruction aui = set.getInstruction(InstructionAui.MNEMONIC, AUI_BASIC_PARAMETER_TYPES).orElse(null);
		if (!(aui instanceof BasicInstruction))
			throw new AssemblerException("Basic instruction '" + InstructionAui.MNEMONIC + "' not found.");
		Instruction ori = set.getInstruction(InstructionOri.MNEMONIC, ORI_BASIC_PARAMETER_TYPES).orElse(null);
		if (!(ori instanceof BasicInstruction))
			throw new AssemblerException("Basic instruction '" + InstructionOri.MNEMONIC + "' not found.");
		Instruction mtc1 = set.getInstruction(InstructionMtc1.MNEMONIC, MTC1_BASIC_PARAMETER_TYPES).orElse(null);
		if(!(mtc1 instanceof BasicInstruction))
			throw new AssemblerException("Basic instruction '" + InstructionMtc1.MNEMONIC + "' not found.");


		int load = parameters[1].getImmediate();
		int upper = load >> 16;
		int lower = load & 0xFFFF;

		//Get parameters
		ParameterParseResult[] auiParameters = new ParameterParseResult[]{
				AT, ZERO, ParameterParseResult.builder().immediate(upper).build()
		};


		ParameterParseResult[] oriParameters = new ParameterParseResult[]{
				AT, AT, ParameterParseResult.builder().immediate(lower).build()
		};

		ParameterParseResult[] mtc1Parameters = new ParameterParseResult[] {
				AT, parameters[0]
		};

		return new AssembledInstruction[]{
				((BasicInstruction<?>) aui).assembleBasic(auiParameters, this),
				((BasicInstruction<?>) ori).assembleBasic(oriParameters, this),
				((BasicInstruction<?>) mtc1).assembleBasic(mtc1Parameters, this)
		};
	}
}
