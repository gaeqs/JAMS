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
import net.jamsimulator.jams.mips.instruction.assembled.AssembledI11Instruction;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicIFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;

public class InstructionMtc0 extends BasicIFPUInstruction<InstructionMtc0.Assembled> {

	public static final String MNEMONIC = "mtc0";
	public static final int OPERATION_CODE = 0b010000;
	public static final int SUBCODE = 0b00100;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.REGISTER, ParameterType.COPROCESSOR_0_REGISTER, ParameterType.UNSIGNED_5_BIT};

	public InstructionMtc0() {
		super(MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, SUBCODE);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
		addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
		addExecutionBuilder(PipelinedArchitecture.INSTANCE, MultiCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new Assembled(parameters[0].getRegister(), parameters[1].getRegister(), parameters[2].getImmediate(), origin, this);
	}

	@Override
	public AssembledInstruction assembleFromCode(int instructionCode) {
		return new Assembled(instructionCode, this, this);
	}

	public static class Assembled extends AssembledI11Instruction {

		public Assembled(int targetRegister, int destinationRegister, int selection, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(OPERATION_CODE, SUBCODE, targetRegister, destinationRegister, selection & 0b111, origin, basicOrigin);
		}

		public Assembled(int instructionCode, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(instructionCode, origin, basicOrigin);
		}

		@Override
		public String parametersToString(String registersStart) {
			return registersStart + getTargetRegister()
					+ ", " + registersStart + getDestinationRegister();
		}
	}

	public static class SingleCycle extends SingleCycleExecution<Assembled> {

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, Assembled instruction, int address) {
			super(simulation, instruction, address);
		}

		@Override
		public void execute() {
			Register rt = register(instruction.getTargetRegister());
			Register rd = registerCop0(instruction.getDestinationRegister());
			rd.setValue(rt.getValue());
		}
	}

	public static class MultiCycle extends MultiCycleExecution<Assembled> {

		public MultiCycle(Simulation<MultiCycleArchitecture> simulation, Assembled instruction, int address) {
			super(simulation, instruction, address, false, true);
		}

		@Override
		public void decode() {
			requires(instruction.getTargetRegister());
			lockCOP0(instruction.getDestinationRegister());
		}

		@Override
		public void execute() {
			executionResult = new int[]{value(instruction.getTargetRegister())};
			forwardCOP0(instruction.getDestinationRegister(), instruction.getImmediate(), executionResult[0], false);
		}

		@Override
		public void memory() {
			forwardCOP0(instruction.getDestinationRegister(), instruction.getImmediate(), executionResult[0], true);
		}

		@Override
		public void writeBack() {
			setAndUnlockCOP0(instruction.getDestinationRegister(), instruction.getImmediate(), instruction.getImmediate());
		}
	}
}
