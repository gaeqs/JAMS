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

package net.jamsimulator.jams.mips.instruction.basic.defaults;

import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.assembled.defaults.AssembledInstructionBc;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;

public class InstructionBc extends BasicInstruction<AssembledInstructionBc> {

	public static final String NAME = "Branch compact";
	public static final String MNEMONIC = "bc";
	public static final int OPERATION_CODE = 0b110010;

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.SIGNED_32_BIT};

	public InstructionBc() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new AssembledInstructionBc(origin, this, parameters[0].getImmediate());
	}

	@Override
	public AssembledInstruction assembleFromCode(int instructionCode) {
		return new AssembledInstructionBc(instructionCode, this, this);
	}

	public static class SingleCycle extends SingleCycleExecution<AssembledInstructionBc> {

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, AssembledInstructionBc instruction) {
			super(simulation, instruction);
		}

		@Override
		public void execute() {
			Registers set = simulation.getRegisterSet();

			Register pc = set.getProgramCounter();
			pc.setValue(pc.getValue() + (instruction.getImmediateAsSigned() << 2));

		}
	}
}
