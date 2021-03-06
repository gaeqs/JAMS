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
import net.jamsimulator.jams.mips.instruction.assembled.AssembledJInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.ControlTransferInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.InstructionParameterTypes;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.StringUtils;

public class InstructionJal extends BasicInstruction<InstructionJal.Assembled>  implements ControlTransferInstruction {

	public static final String MNEMONIC = "jal";
	public static final int OPERATION_CODE = 0b000011;

	public static final InstructionParameterTypes PARAMETER_TYPES = new InstructionParameterTypes(ParameterType.SIGNED_32_BIT);

	public InstructionJal() {
		super(MNEMONIC, PARAMETER_TYPES, OPERATION_CODE);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
		addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
		addExecutionBuilder(PipelinedArchitecture.INSTANCE, MultiCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new Assembled(origin, this, parameters[0].getImmediate());
	}

	@Override
	public AssembledInstruction assembleFromCode(int instructionCode) {
		return new Assembled(instructionCode, this, this);
	}

	@Override
	public boolean isCompact() {
		return false;
	}

	public static class Assembled extends AssembledJInstruction {

		public Assembled(Instruction origin, BasicInstruction<Assembled> basicOrigin, int address) {
			super(OPERATION_CODE, address, origin, basicOrigin);
		}

		public Assembled(int instructionCode, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(instructionCode, origin, basicOrigin);
		}

		@Override
		public String parametersToString(String registersStart) {
			return "0x" + StringUtils.addZeros(Integer.toHexString(getAddress()), 7);
		}
	}

	public static class SingleCycle extends SingleCycleExecution<Assembled> {

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, Assembled instruction, int address) {
			super(simulation, instruction, address);
		}

		@Override
		public void execute() {
			register(31).setValue(getAddress() + 4);
			pc().setValue(instruction.getAbsoluteAddress(getAddress() + 4));
		}
	}

	public static class MultiCycle extends MultiCycleExecution<Assembled> {

		public MultiCycle(Simulation<MultiCycleArchitecture> simulation, Assembled instruction, int address) {
			super(simulation, instruction, address, false, true);
		}

		@Override
		public void decode() {
			lock(pc());
			lock(31);

			if (solveBranchOnDecode()) {
				jump(instruction.getAbsoluteAddress(getAddress() + 4));
			}
		}

		@Override
		public void execute() {
			if (solveBranchOnDecode()) {
				forward(31, getAddress() + 4, false);
			}
		}

		@Override
		public void memory() {
			if (solveBranchOnDecode()) {
				forward(31, getAddress() + 4, true);
			}
		}

		@Override
		public void writeBack() {
			if (!solveBranchOnDecode()) {
				jump(instruction.getAbsoluteAddress(getAddress() + 4));
			}
			setAndUnlock(31, getAddress() + 4);
		}
	}
}
