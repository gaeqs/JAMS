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

import net.jamsimulator.jams.mips.architecture.MultiCycleArchitecture;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI16Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.StringUtils;

public class InstructionBltuc extends BasicInstruction<InstructionBltuc.Assembled> {

	public static final String NAME = "Branch on lower than unsigned compact";
	public static final String MNEMONIC = "bltuc";
	public static final int OPERATION_CODE = 0b000111;

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.SIGNED_16_BIT};

	public InstructionBltuc() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
		addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new Assembled(parameters[0].getRegister(), parameters[1].getRegister(), parameters[2].getImmediate(), origin, this);
	}

	@Override
	public AssembledInstruction assembleFromCode(int instructionCode) {
		return new Assembled(instructionCode, this, this);
	}

	@Override
	public boolean match(int instructionCode) {
		int rs = instructionCode >> AssembledI16Instruction.SOURCE_REGISTER_SHIFT & AssembledI16Instruction.SOURCE_REGISTER_MASK;
		int rt = instructionCode >> AssembledI16Instruction.TARGET_REGISTER_SHIFT & AssembledI16Instruction.TARGET_REGISTER_MASK;
		return super.match(instructionCode) && rt != rs && rs != 0 && rt != 0;
	}

	public static class Assembled extends AssembledI16Instruction {

		public Assembled(int sourceRegister, int targetRegister, int offset, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(InstructionBltuc.OPERATION_CODE, sourceRegister, targetRegister, offset, origin, basicOrigin);
		}

		public Assembled(int instructionCode, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(instructionCode, origin, basicOrigin);
		}

		@Override
		public String parametersToString(String registersStart) {
			return registersStart + getSourceRegister()
					+ ", " + registersStart + getTargetRegister()
					+ ", 0x" + StringUtils.addZeros(Integer.toHexString(getImmediate()), 4);
		}
	}

	public static class SingleCycle extends SingleCycleExecution<Assembled> {

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, Assembled instruction) {
			super(simulation, instruction);
		}

		@Override
		public void execute() {
			Register rt = register(instruction.getTargetRegister());
			Register rs = register(instruction.getSourceRegister());
			if (Integer.compareUnsigned(rs.getValue(), rt.getValue()) < 0) return;
			Register pc = pc();
			pc.setValue(pc.getValue() + (instruction.getImmediateAsSigned() << 2));

		}
	}

	public static class MultiCycle extends MultiCycleExecution<Assembled> {

		public MultiCycle(Simulation<MultiCycleArchitecture> simulation, Assembled instruction) {
			super(simulation, instruction, false, false);
		}

		@Override
		public void decode() {
			Register rt = register(instruction.getTargetRegister());
			Register rs = register(instruction.getSourceRegister());
			decodeResult = new int[]{rs.getValue(), rt.getValue()};
		}

		@Override
		public void execute() {
			executionResult = new int[0];
			if (Integer.compareUnsigned(decodeResult[0], decodeResult[1]) < 0) return;
			pc().setValue(pc().getValue() + (instruction.getImmediateAsSigned() << 2));
		}

		@Override
		public void memory() {

		}

		@Override
		public void writeBack() {
		}
	}
}
