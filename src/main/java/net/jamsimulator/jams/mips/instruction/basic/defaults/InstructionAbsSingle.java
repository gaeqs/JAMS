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
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;

public class InstructionAbsSingle extends BasicRFPUInstruction<InstructionAbsSingle.Assembled> {

	public static final String NAME = "Absolute (single)";
	public static final String MNEMONIC = "abs.s";
	public static final int OPERATION_CODE = 0b010001;
	public static final int FMT = 0b10000;
	public static final int FUNCTION_CODE = 0b000101;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.FLOAT_REGISTER, ParameterType.FLOAT_REGISTER};

	public InstructionAbsSingle() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE, FMT);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
		addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new Assembled(parameters[1].getRegister(), parameters[0].getRegister(), origin, this);
	}

	@Override
	public AssembledInstruction assembleFromCode(int instructionCode) {
		return new Assembled(instructionCode, this, this);
	}

	public static class Assembled extends AssembledRFPUInstruction {

		public Assembled(int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(InstructionAbsSingle.OPERATION_CODE, InstructionAbsSingle.FMT, 0, sourceRegister,
					destinationRegister, InstructionAbsSingle.FUNCTION_CODE, origin, basicOrigin);
		}

		public Assembled(int instructionCode, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(instructionCode, origin, basicOrigin);
		}

		@Override
		public String parametersToString(String registersStart) {
			return registersStart + getDestinationRegister() + ", " + registersStart + getSourceRegister();
		}
	}

	public static class SingleCycle extends SingleCycleExecution<Assembled> {

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, Assembled instruction) {
			super(simulation, instruction);
		}

		@Override
		public void execute() {
			Register rs = registerCop1(instruction.getSourceRegister());
			Register rd = registerCop1(instruction.getDestinationRegister());
			float f = Math.abs(Float.intBitsToFloat(rs.getValue()));
			rd.setValue(Float.floatToIntBits(f));
		}
	}

	public static class MultiCycle extends MultiCycleExecution<Assembled> {

		public MultiCycle(Simulation<MultiCycleArchitecture> simulation, Assembled instruction) {
			super(simulation, instruction);
		}

		@Override
		public void decode() {
			values = new int[]{registerCop1(instruction.getSourceRegister()).getValue()};
		}

		@Override
		public void execute() {
			result = new int[]{Float.floatToIntBits(Math.abs(Float.intBitsToFloat(values[0])))};
		}

		@Override
		public void memory() {

		}

		@Override
		public void writeBack() {
			registerCop1(instruction.getDestinationRegister()).setValue(result[0]);
		}
	}
}
