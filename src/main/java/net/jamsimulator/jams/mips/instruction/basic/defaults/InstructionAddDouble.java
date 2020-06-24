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
import net.jamsimulator.jams.mips.instruction.assembled.defaults.AssembledInstructionAddDouble;
import net.jamsimulator.jams.mips.instruction.basic.BasicRFPUInstruction;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.NumericUtils;

import java.util.Optional;

public class InstructionAddDouble extends BasicRFPUInstruction<AssembledInstructionAddDouble> {

	public static final String NAME = "Addition (double)";
	public static final String MNEMONIC = "add.d";
	public static final int OPERATION_CODE = 0b010001;
	public static final int FMT = 0b10001;
	public static final int FUNCTION_CODE = 0b000000;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.EVEN_FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER, ParameterType.EVEN_FLOAT_REGISTER};

	public InstructionAddDouble() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE, FMT);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new AssembledInstructionAddDouble(parameters[2].getRegister(), parameters[1].getRegister(),
				parameters[0].getRegister(), origin, this);
	}

	@Override
	public AssembledInstruction assembleFromCode(int instructionCode) {
		return new AssembledInstructionAddDouble(instructionCode, this, this);
	}

	public static class SingleCycle extends SingleCycleExecution<AssembledInstructionAddDouble> {

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, AssembledInstructionAddDouble instruction) {
			super(simulation, instruction);
		}

		@Override
		public void execute() {
			Registers set = simulation.getRegisters();
			Optional<Register> rt0 = set.getCoprocessor1Register(instruction.getTargetRegister());
			Optional<Register> rt1 = set.getCoprocessor1Register(instruction.getTargetRegister() + 1);
			if (!rt0.isPresent()) error("Target register not found.");
			if (!rt1.isPresent()) error("Target register not found.");
			if (rt0.get().getIdentifier() % 2 != 0) error("Target register identifier is not even.");
			Optional<Register> rs0 = set.getCoprocessor1Register(instruction.getSourceRegister());
			Optional<Register> rs1 = set.getCoprocessor1Register(instruction.getSourceRegister() + 1);
			if (!rs0.isPresent()) error("Source register not found.");
			if (!rs1.isPresent()) error("Source register not found.");
			if (rs0.get().getIdentifier() % 2 != 0) error("Source register identifier is not even.");
			Optional<Register> rd0 = set.getCoprocessor1Register(instruction.getDestinationRegister());
			Optional<Register> rd1 = set.getCoprocessor1Register(instruction.getDestinationRegister() + 1);
			if (!rd0.isPresent()) error("Destination register not found");
			if (!rd1.isPresent()) error("Destination register not found");
			if (rd0.get().getIdentifier() % 2 != 0) error("Destination register identifier is not even.");

			double target = NumericUtils.intsToDouble(rt0.get().getValue(), rt1.get().getValue());
			double source = NumericUtils.intsToDouble(rs0.get().getValue(), rs1.get().getValue());
			double destination = target + source;
			int[] ints = NumericUtils.doubleToInts(destination);
			rs0.get().setValue(ints[0]);
			rs1.get().setValue(ints[1]);
		}
	}
}
