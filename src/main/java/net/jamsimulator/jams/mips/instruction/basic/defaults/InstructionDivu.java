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
import net.jamsimulator.jams.mips.architecture.PipelinedArchitecture;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRSOPInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicRSOPInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;

public class InstructionDivu extends BasicRSOPInstruction<InstructionDivu.Assembled> {

	public static final String NAME = "Divide unsigned";
	public static final String MNEMONIC = "divu";
	public static final int OPERATION_CODE = 0;
	public static final int FUNCTION_CODE = 0b011011;
	public static final int SOP_CODE = 0b00010;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.REGISTER};

	public InstructionDivu() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE, SOP_CODE);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
		addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
addExecutionBuilder(PipelinedArchitecture.INSTANCE, MultiCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new Assembled(parameters[1].getRegister(),
				parameters[2].getRegister(),
				parameters[0].getRegister(), origin, this);
	}

	@Override
	public AssembledInstruction assembleFromCode(int instructionCode) {
		return new Assembled(instructionCode, this, this);
	}

	public static class Assembled extends AssembledRSOPInstruction {

		public Assembled(int sourceRegister, int targetRegister, int destinationRegister,
						 Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(InstructionDivu.OPERATION_CODE, sourceRegister, targetRegister, destinationRegister, InstructionDivu.SOP_CODE,
					InstructionDivu.FUNCTION_CODE, origin, basicOrigin);
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
			Register rt = register(instruction.getTargetRegister());
			Register rs = register(instruction.getSourceRegister());
			Register rd = register(instruction.getDestinationRegister());

			if (rt.getValue() == 0) {
				//MIP rev 6: If the divisor in GPR rt is zero, the result value is UNPREDICTABLE.
				rd.setValue(0);
				return;
			}
			rd.setValue(Integer.divideUnsigned(rs.getValue(), rt.getValue()));
		}
	}

	public static class MultiCycle extends MultiCycleExecution<Assembled> {

		public MultiCycle(Simulation<MultiCycleArchitecture> simulation, Assembled instruction, int address) {
			super(simulation, instruction, address, false, true);
		}

		@Override
		public void decode() {
			requires(instruction.getSourceRegister());
			requires(instruction.getTargetRegister());
			lock(instruction.getDestinationRegister());
		}

		@Override
		public void execute() {
			var source = value(instruction.getSourceRegister());
			var target = value(instruction.getTargetRegister());

			executionResult = new int[]{target == 0 ? 0 : Integer.divideUnsigned(source, target)};
			forward(instruction.getDestinationRegister(), executionResult[0], false);
		}

		@Override
		public void memory() {
			forward(instruction.getDestinationRegister(), executionResult[0], true);
		}

		@Override
		public void writeBack() {
			setAndUnlock(instruction.getDestinationRegister(), executionResult[0]);
		}
	}
}
