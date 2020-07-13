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
import net.jamsimulator.jams.mips.instruction.assembled.AssembledRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicInstruction;
import net.jamsimulator.jams.mips.instruction.basic.BasicRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;

public class InstructionCvtNN extends BasicRFPUInstruction<InstructionCvtNN.Assembled> {

	public static final String NAME = "Convert from %s to %s";
	public static final String MNEMONIC = "cvt.%s.%s";
	public static final int OPERATION_CODE = 0b010001;

	private static final ParameterType[] PARAMETER_TYPES = new ParameterType[]{ParameterType.EVEN_FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER};

	private final FmtNumbers to, from;

	public InstructionCvtNN(FmtNumbers to, FmtNumbers from) {
		super(String.format(NAME, to.getName(), from.getName()),
				String.format(MNEMONIC, to.getMnemonic(), from.getMnemonic()),
				PARAMETER_TYPES, OPERATION_CODE, to.getCvt(), from.getFmt());
		this.to = to;
		this.from = from;
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new Assembled(parameters[1].getRegister(), parameters[0].getRegister(), to, from, origin, this);
	}

	@Override
	public AssembledInstruction assembleFromCode(int instructionCode) {
		return new Assembled(instructionCode, to, from, this, this);
	}

	public static class Assembled extends AssembledRFPUInstruction {

		final FmtNumbers to, from;

		public Assembled(int sourceRegister, int destinationRegister, FmtNumbers to, FmtNumbers from, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(InstructionCvtNN.OPERATION_CODE, from.getFmt(), 0, sourceRegister,
					destinationRegister, to.getCvt(), origin, basicOrigin);
			this.to = to;
			this.from = from;
		}

		public Assembled(int instructionCode, FmtNumbers to, FmtNumbers from, Instruction origin, BasicInstruction<Assembled> basicOrigin) {
			super(instructionCode, origin, basicOrigin);
			this.to = to;
			this.from = from;
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

			if (instruction.to.requiresEventRegister()) {
				if (instruction.getDestinationRegister() % 2 != 0)
					error("Destination register identifier is not even.");
			}
			if (instruction.from.requiresEventRegister()) {
				if (instruction.getSourceRegister() % 2 != 0)
					error("Source register identifier is not even.");
			}

			Register to0 = registerCop1(instruction.getDestinationRegister());
			Register to1 = instruction.to.requiresEventRegister()
					? registerCop1(instruction.getDestinationRegister() + 1)
					: null;

			Register from0 = registerCop1(instruction.getSourceRegister());
			Register from1 = instruction.from.requiresEventRegister()
					? registerCop1(instruction.getSourceRegister() + 1)
					: null;

			Number number = instruction.from.from(from0, from1);
			instruction.to.to(number, to0, to1);
		}
	}
}
