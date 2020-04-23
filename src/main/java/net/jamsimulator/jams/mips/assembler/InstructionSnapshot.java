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

package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;

import java.util.List;

/**
 * Represents a instruction snapshot. Because labels may be below instructions
 * they cannot be compiled on the first scan. They must be stored in a snapshot.
 */
public class InstructionSnapshot {

	public Instruction instruction;
	public List<String> parameters;
	public int line;
	public int address;

	public InstructionSnapshot(Instruction instruction, List<String> parameters, int line, int address) {
		this.instruction = instruction;
		this.parameters = parameters;
		this.line = line;
		this.address = address;
	}

	public void compile(Assembler assembler, AssemblingFile file) {
		ParameterParseResult[] compiledParameters = new ParameterParseResult[parameters.size()];

		int index = 0;
		ParameterParseResult result;
		for (ParameterType parameter : instruction.getParameters()) {
			result = parameter.parse(parameters.get(index), assembler.getRegisterSet());

			//Parse label
			if (result.isHasLabel()) {
				result.setLabelValue(file.getLabelValue(assembler, result.getLabel(), line));
			}

			compiledParameters[index++] = result;
		}

		try {
			AssembledInstruction[] assembledInstructions = instruction.assemble(assembler.getInstructionSet(), address, compiledParameters);

			//Add instructions to memory
			int relativeAddress = address;
			for (AssembledInstruction assembledInstruction : assembledInstructions) {
				assembler.getMemory().setWord(relativeAddress, assembledInstruction.getCode());
				relativeAddress += 4;
			}
		} catch (AssemblerException ex) {
			throw new AssemblerException(line, "Error while compiling instruction.", ex);
		}

	}
}
