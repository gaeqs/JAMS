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
import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
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

public class InstructionCeilWDouble extends BasicRFPUInstruction<InstructionCeilWDouble.Assembled> {

	public static final String MNEMONIC = "ceil.w.d";
	public static final int OPERATION_CODE = 0b010001;
	public static final int FMT = 0b10001;
	public static final int FUNCTION_CODE = 0b001110;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER};

	public InstructionCeilWDouble() {
		super(MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE, FMT);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
		addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
		addExecutionBuilder(PipelinedArchitecture.INSTANCE, MultiCycle::new);
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
			super(InstructionCeilWDouble.OPERATION_CODE, InstructionCeilWDouble.FMT, 0, sourceRegister,
					destinationRegister, InstructionCeilWDouble.FUNCTION_CODE, origin, basicOrigin);
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

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, Assembled instruction, int address) {
			super(simulation, instruction, address);
		}

		@Override
		public void execute() {
			if (instruction.getSourceRegister() % 2 != 0) evenFloatRegisterException();
			Register rs0 = registerCop1(instruction.getSourceRegister());
			Register rs1 = registerCop1(instruction.getSourceRegister() + 1);
			double d = NumericUtils.intsToDouble(rs0.getValue(), rs1.getValue());
			int i = (int) Math.ceil(d);
			registerCop1(instruction.getDestinationRegister()).setValue(i);
		}
	}

	public static class MultiCycle extends MultiCycleExecution<Assembled> {

		public MultiCycle(Simulation<MultiCycleArchitecture> simulation, Assembled instruction, int address) {
			super(simulation, instruction, address, false, true);
		}

		@Override
		public void decode() {
			if (instruction.getSourceRegister() % 2 != 0) evenFloatRegisterException();

			requiresCOP1(instruction.getSourceRegister());
			requiresCOP1(instruction.getSourceRegister() + 1);
			lockCOP1(instruction.getDestinationRegister());
		}

		@Override
		public void execute() {
			var id = instruction.getSourceRegister();
			var to = instruction.getDestinationRegister();
			var ceil = (int) Math.ceil(NumericUtils.intsToDouble(valueCOP1(id), valueCOP1(id + 1)));
			executionResult = new int[]{ceil};
			forwardCOP1(to, executionResult[0], false);
		}

		@Override
		public void memory() {
			var to = instruction.getDestinationRegister();
			forwardCOP1(to, executionResult[0], true);
		}

		@Override
		public void writeBack() {
			setAndUnlockCOP1(instruction.getDestinationRegister(), executionResult[0]);
		}
	}
}
