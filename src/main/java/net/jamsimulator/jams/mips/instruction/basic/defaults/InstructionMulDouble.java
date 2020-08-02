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
import net.jamsimulator.jams.utils.NumericUtils;

public class InstructionMulDouble extends BasicRFPUInstruction<InstructionMulDouble.Assembled> {

	public static final String NAME = "Multiplication (double)";
	public static final String MNEMONIC = "mul.d";
	public static final int OPERATION_CODE = 0b010001;
	public static final int FMT = 0b10001;
	public static final int FUNCTION_CODE = 0b000010;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.EVEN_FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER};

	public InstructionMulDouble() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE, FMT);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
		addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new Assembled(parameters[2].getRegister(), parameters[1].getRegister(),
				parameters[0].getRegister(), origin, this);
	}

	@Override
	public AssembledInstruction assembleFromCode(int instructionCode) {
		return new Assembled(instructionCode, this, this);
	}

	public static class Assembled extends AssembledRFPUInstruction {

		public Assembled(int targetRegister, int sourceRegister, int destinationRegister, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(InstructionMulDouble.OPERATION_CODE, InstructionMulDouble.FMT, targetRegister, sourceRegister, destinationRegister,
					InstructionMulDouble.FUNCTION_CODE, origin, basicOrigin);
		}

		public Assembled(int instructionCode, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(instructionCode, origin, basicOrigin);
		}

		@Override
		public String parametersToString(String registersStart) {
			return registersStart + getDestinationRegister()
					+ ", " + registersStart + getSourceRegister()
					+ ", " + registersStart + getTargetRegister();
		}
	}

	public static class SingleCycle extends SingleCycleExecution<Assembled> {

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, Assembled instruction, int address) {
			super(simulation, instruction, address);
		}

		@Override
		public void execute() {
			if (instruction.getTargetRegister() % 2 != 0) error("Target register identifier is not even.");
			if (instruction.getSourceRegister() % 2 != 0) error("Source register identifier is not even.");
			if (instruction.getDestinationRegister() % 2 != 0) error("Destination register identifier is not even.");

			Register rt0 = registerCop1(instruction.getTargetRegister());
			Register rt1 = registerCop1(instruction.getTargetRegister() + 1);
			Register rs0 = registerCop1(instruction.getSourceRegister());
			Register rs1 = registerCop1(instruction.getSourceRegister() + 1);
			Register rd0 = registerCop1(instruction.getDestinationRegister());
			Register rd1 = registerCop1(instruction.getDestinationRegister() + 1);

			double target = NumericUtils.intsToDouble(rt0.getValue(), rt1.getValue());
			double source = NumericUtils.intsToDouble(rs0.getValue(), rs1.getValue());
			double destination = source * target;
			int[] ints = NumericUtils.doubleToInts(destination);

			rd0.setValue(ints[0]);
			rd1.setValue(ints[1]);
		}
	}

	public static class MultiCycle extends MultiCycleExecution<Assembled> {

		public MultiCycle(Simulation<MultiCycleArchitecture> simulation, Assembled instruction, int address) {
			super(simulation, instruction, address, false, true);
		}

		@Override
		public void decode() {
			if (instruction.getTargetRegister() % 2 != 0) error("Target register identifier is not even.");
			if (instruction.getSourceRegister() % 2 != 0) error("Source register identifier is not even.");
			if (instruction.getDestinationRegister() % 2 != 0) error("Destination register identifier is not even.");

			Register rt0 = registerCop1(instruction.getTargetRegister());
			Register rt1 = registerCop1(instruction.getTargetRegister() + 1);
			Register rs0 = registerCop1(instruction.getSourceRegister());
			Register rs1 = registerCop1(instruction.getSourceRegister() + 1);
			decodeResult = new int[]{rt0.getValue(), rt1.getValue(), rs0.getValue(), rs1.getValue()};
		}

		@Override
		public void execute() {
			double target = NumericUtils.intsToDouble(decodeResult[0], decodeResult[1]);
			double source = NumericUtils.intsToDouble(decodeResult[2], decodeResult[3]);
			double destination = source * target;
			executionResult = NumericUtils.doubleToInts(destination);
		}

		@Override
		public void memory() {

		}

		@Override
		public void writeBack() {
			registerCop1(instruction.getDestinationRegister()).setValue(executionResult[0]);
			registerCop1(instruction.getDestinationRegister() + 1).setValue(executionResult[1]);
		}
	}
}