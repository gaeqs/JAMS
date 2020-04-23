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
import net.jamsimulator.jams.mips.instruction.assembled.defaults.AssembledInstructionMulu;
import net.jamsimulator.jams.mips.instruction.basic.BasicRSOPInstruction;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;

import java.util.Optional;

public class InstructionMulu extends BasicRSOPInstruction<AssembledInstructionMulu> {

	public static final String NAME = "Multiplication unsigned, low word";
	public static final String MNEMONIC = "mulu";
	public static final int OPERATION_CODE = 0;
	public static final int FUNCTION_CODE = 0b011001;
	public static final int SOP_CODE = 0b00010;

	private static final ParameterType[] PARAMETER_TYPES
			= new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.REGISTER};

	public InstructionMulu() {
		super(NAME, MNEMONIC, PARAMETER_TYPES, OPERATION_CODE, FUNCTION_CODE, SOP_CODE);
		addExecutionBuilder(SingleCycleArchitecture.INSTANCE, SingleCycle::new);
	}

	@Override
	public AssembledInstruction assembleBasic(ParameterParseResult[] parameters, Instruction origin) {
		return new AssembledInstructionMulu(parameters[1].getRegister(),
				parameters[2].getRegister(),
				parameters[0].getRegister(), origin, this);
	}

	@Override
	public AssembledInstruction compileFromCode(int instructionCode) {
		return new AssembledInstructionMulu(instructionCode, this, this);
	}

	public static class SingleCycle extends SingleCycleExecution<AssembledInstructionMulu> {

		public SingleCycle(Simulation<SingleCycleArchitecture> simulation, AssembledInstructionMulu instruction) {
			super(simulation, instruction);
		}

		@Override
		public void execute() {
			Registers set = simulation.getRegisterSet();
			Optional<Register> rs = set.getRegister(instruction.getSourceRegister());
			if (!rs.isPresent()) error("Source register not found.");
			Optional<Register> rt = set.getRegister(instruction.getTargetRegister());
			if (!rt.isPresent()) error("Target register not found.");
			Optional<Register> rd = set.getRegister(instruction.getDestinationRegister());
			if (!rd.isPresent()) error("Destination register not found");
			rd.get().setValue(rs.get().getValue() * rt.get().getValue());
		}
	}
}