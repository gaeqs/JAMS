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
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRIInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicRIInstruction;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.StringUtils;

public class InstructionBltz extends BasicRIInstruction<InstructionBltz.Assembled> {

	public static final String NAME = "Branch on less than zero";
	public static final String MNEMONIC = "bltz";
	public static final int OPERATION_CODE = 0b000001;
	public static final int FUNCTION_CODE = 0b00000;

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

	public InstructionBltz() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new Assembled(parameters[0].getRegister(), parameters[1].getImmediate(), origin, this);
	}

	@Override
	public AssembledInstruction assembleFromCode(int instructionCode) {
		return new Assembled(instructionCode, this, this);
	}

	public static class Assembled extends AssembledRIInstruction {

		public Assembled(int sourceRegister, int offset, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(InstructionBltz.OPERATION_CODE, sourceRegister, InstructionBltz.FUNCTION_CODE, offset, origin, basicOrigin);
		}

		public Assembled(int instructionCode, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(instructionCode, origin, basicOrigin);
		}

		@Override
		public String parametersToString(String registersStart) {
			return registersStart + getSourceRegister()
					+ ", 0x" + StringUtils.addZeros(Integer.toHexString(getImmediate()), 4);
		}
	}

	public static class SingleCycle extends SingleCycleExecution<Assembled> {

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, Assembled instruction) {
			super(simulation, instruction);
		}

		@Override
		public void execute() {
			Register rs = register(instruction.getSourceRegister());
			if (rs.getValue() >= 0) return;
			Register pc = pc();
			pc.setValue(pc.getValue() + (instruction.getImmediateAsSigned() << 2));
		}
	}
}