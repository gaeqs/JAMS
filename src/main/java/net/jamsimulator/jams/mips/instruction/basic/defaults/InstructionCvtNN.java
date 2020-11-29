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

import net.jamsimulator.jams.Jams;
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

public class InstructionCvtNN extends BasicRFPUInstruction<InstructionCvtNN.Assembled> {

	public static final String NAME_SUFIX = "CVT";
	public static final String MNEMONIC = "cvt.%s.%s";
	public static final int OPERATION_CODE = 0b010001;

	private final FmtNumbers to, from;

	public InstructionCvtNN(FmtNumbers to, FmtNumbers from) {
		super(
				String.format(MNEMONIC, to.getMnemonic(), from.getMnemonic()),
				new ParameterType[]{to.requiresEvenRegister() ? ParameterType.EVEN_FLOAT_REGISTER : ParameterType.FLOAT_REGISTER,
						from.requiresEvenRegister() ? ParameterType.EVEN_FLOAT_REGISTER : ParameterType.FLOAT_REGISTER},
				OPERATION_CODE, to.getCvt(), from.getFmt());
		this.to = to;
		this.from = from;
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
		addExecutionBuilder(MultiCycleArchitecture.INSTANCE, MultiCycle::new);
		addExecutionBuilder(PipelinedArchitecture.INSTANCE, MultiCycle::new);
	}

	@Override
	public String getName() {
		var name = Jams.getLanguageManager().getSelected().getOrDefault("INSTRUCTION_" + NAME_SUFIX);
		return name.replace("{FROM}", from.getName()).replace("{TO}", to.getName());
	}

	@Override
	public String getDocumentation() {
		var name = Jams.getLanguageManager().getSelected().getOrDefault("INSTRUCTION_" + NAME_SUFIX + "_DOCUMENTATION");
		return name.replace("{FROM}", from.getName()).replace("{TO}", to.getName());
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

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, Assembled instruction, int address) {
			super(simulation, instruction, address);
		}

		@Override
		public void execute() {

			if (instruction.to.requiresEvenRegister()) {
				if (instruction.getDestinationRegister() % 2 != 0)
					evenFloatRegisterException();
			}
			if (instruction.from.requiresEvenRegister()) {
				if (instruction.getSourceRegister() % 2 != 0)
					evenFloatRegisterException();
			}

			Register to0 = registerCop1(instruction.getDestinationRegister());
			Register to1 = instruction.to.requiresEvenRegister()
					? registerCop1(instruction.getDestinationRegister() + 1)
					: null;

			Register from0 = registerCop1(instruction.getSourceRegister());
			Register from1 = instruction.from.requiresEvenRegister()
					? registerCop1(instruction.getSourceRegister() + 1)
					: null;

			Number number = instruction.from.from(from0, from1);
			instruction.to.to(number, to0, to1);
		}
	}


	public static class MultiCycle extends MultiCycleExecution<Assembled> {

		public MultiCycle(Simulation<MultiCycleArchitecture> simulation, Assembled instruction, int address) {
			super(simulation, instruction, address, false, true);
		}

		@Override
		public void decode() {
			if (instruction.to.requiresEvenRegister() && instruction.getDestinationRegister() % 2 != 0)
				evenFloatRegisterException();
			if (instruction.from.requiresEvenRegister() && instruction.getSourceRegister() % 2 != 0)
				evenFloatRegisterException();

			requiresCOP1(instruction.getSourceRegister());
			if (instruction.from.requiresEvenRegister()) {
				requiresCOP1(instruction.getSourceRegister() + 1);
			}

			lockCOP1(instruction.getDestinationRegister());
			if (instruction.to.requiresEvenRegister()) {
				lockCOP1(instruction.getDestinationRegister() + 1);
			}
		}

		@Override
		public void execute() {
			var extension = instruction.from.requiresEvenRegister()
					? valueCOP1(instruction.getSourceRegister() + 1) : 0;
			Number number = instruction.from.from(valueCOP1(instruction.getSourceRegister()), extension);
			executionResult = instruction.to.to(number);

			forwardCOP1(instruction.getDestinationRegister(), executionResult[0], false);
			if (instruction.to.requiresEvenRegister()) {
				forwardCOP1(instruction.getDestinationRegister(), executionResult[1], false);
			}
		}

		@Override
		public void memory() {
			forwardCOP1(instruction.getDestinationRegister(), executionResult[0], true);
			if (instruction.to.requiresEvenRegister()) {
				forwardCOP1(instruction.getDestinationRegister(), executionResult[1], true);
			}
		}

		@Override
		public void writeBack() {
			setAndUnlockCOP1(instruction.getDestinationRegister(), executionResult[0]);
			if (instruction.to.requiresEvenRegister()) {
				setAndUnlockCOP1(instruction.getDestinationRegister() + 1, executionResult[1]);
			}
		}
	}
}
