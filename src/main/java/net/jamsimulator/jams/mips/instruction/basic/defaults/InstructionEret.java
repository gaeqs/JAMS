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
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicRInstruction;
import net.jamsimulator.jams.mips.instruction.basic.ControlTransferInstruction;
import net.jamsimulator.jams.mips.instruction.execution.MultiCycleExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.COP0Register;
import net.jamsimulator.jams.mips.register.COP0RegistersBits;
import net.jamsimulator.jams.mips.simulation.Simulation;

public class InstructionEret extends BasicRInstruction<InstructionEret.Assembled> implements ControlTransferInstruction {

	public static final String NAME = "Exception return";
	public static final String MNEMONIC = "eret";
	public static final int OPERATION_CODE = 0b010000;
	public static final int FUNCTION_CODE = 0b011000;

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[0];

	public InstructionEret() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
		addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
		addExecutionBuilder(PipelinedArchitecture.INSTANCE, MultiCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new Assembled(origin, this);
	}

	@Override
	public AssembledInstruction assembleFromCode(int instructionCode) {
		return new Assembled(instructionCode, this, this);
	}

	@Override
	public boolean isCompact() {
		return false;
	}

	public static class Assembled extends AssembledRInstruction {

		public Assembled(Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(OPERATION_CODE, 0b10000, 0, 0, 0, FUNCTION_CODE, origin, basicOrigin);
		}

		public Assembled(int instructionCode, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(instructionCode, origin, basicOrigin);
		}

		@Override
		public String parametersToString(String registersStart) {
			return "";
		}
	}

	public static class SingleCycle extends SingleCycleExecution<Assembled> {

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, Assembled instruction, int address) {
			super(simulation, instruction, address);
		}

		@Override
		public void execute() {
			COP0Register status = (COP0Register) registerCop0(12, 0);
			int temp;
			if (status.getBit(COP0RegistersBits.STATUS_ERL)) {
				temp = registerCop0(30, 0).getValue();
				status.modifyBits(0, COP0RegistersBits.STATUS_ERL, 1);
			} else {
				temp = registerCop0(14, 0).getValue();
				status.modifyBits(0, COP0RegistersBits.STATUS_EXL, 1);
			}
			pc().setValue(temp);
		}
	}

	public static class MultiCycle extends MultiCycleExecution<Assembled> {

		public MultiCycle(Simulation<MultiCycleArchitecture> simulation, Assembled instruction, int address) {
			super(simulation, instruction, address, false, true);
		}

		@Override
		public void decode() {
			requiresCOP0(12, 0);
			requiresCOP0(14, 0);
			requiresCOP0(30, 0);
			lock(pc());
			lockCOP0(12, 0);

			if (solveBranchOnDecode()) {
				solve();
			}
		}

		@Override
		public void execute() {
			if (solveBranchOnDecode()) {
				forwardCOP0(12, 0, valueCOP0(12, 0), false);
				forwardCOP0(14, 0, valueCOP0(14, 0), false);
				forwardCOP0(30, 0, valueCOP0(30, 0), false);
			}
		}

		@Override
		public void memory() {
			if (solveBranchOnDecode()) {
				forwardCOP0(12, 0, valueCOP0(12, 0), true);
				forwardCOP0(14, 0, valueCOP0(14, 0), true);
				forwardCOP0(30, 0, valueCOP0(30, 0), true);
			}
		}

		@Override
		public void writeBack() {
			if (!solveBranchOnDecode()) {
				solve();
			}
			unlockCOP0(12, 0);
			unlockCOP0(14, 0);
			unlockCOP0(30, 0);
		}

		private void solve() {
			valueCOP0(12, 0);
			COP0Register status = (COP0Register) registerCop0(12, 0);
			int temp;
			if (status.getBit(COP0RegistersBits.STATUS_ERL)) {
				temp = valueCOP0(30, 0);
				status.modifyBits(0, COP0RegistersBits.STATUS_ERL, 1);
			} else {
				temp = valueCOP0(14, 0);
				status.modifyBits(0, COP0RegistersBits.STATUS_EXL, 1);
			}
			jump(temp);
		}
	}
}
