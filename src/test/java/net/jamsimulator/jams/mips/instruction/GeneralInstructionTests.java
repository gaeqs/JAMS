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

package net.jamsimulator.jams.mips.instruction;

import net.jamsimulator.jams.gui.util.Log;
import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.instruction.assembled.AssembledInstruction;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAdd;
import net.jamsimulator.jams.mips.instruction.exception.RuntimeInstructionException;
import net.jamsimulator.jams.mips.instruction.execution.InstructionExecution;
import net.jamsimulator.jams.mips.instruction.execution.SingleCycleExecution;
import net.jamsimulator.jams.mips.instruction.set.MIPS32InstructionSet;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.parameter.parse.ParameterParseResult;
import net.jamsimulator.jams.mips.register.MIPS32Registers;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.singlecycle.SingleCycleSimulation;
import net.jamsimulator.jams.mips.syscall.SimulationSyscallExecutions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GeneralInstructionTests {

	static Simulation<?> simulation = new SingleCycleSimulation(SingleCycleArchitecture.INSTANCE, new MIPS32InstructionSet(),
			new MIPS32Registers(), new MIPS32Memory(), new SimulationSyscallExecutions(), new Log(), 0);

	@Test
	void testBasicInstruction() {
		Registers set = simulation.getRegisters();
		Register t0 = set.getRegister("t0").get();
		Register t1 = set.getRegister("t1").get();
		Register t2 = set.getRegister("t2").get();
		t0.setValue(3);
		t1.setValue(20);

		List<ParameterType>[] list = new List[]{Collections.singletonList(ParameterType.REGISTER),
				Collections.singletonList(ParameterType.REGISTER),
				Collections.singletonList(ParameterType.REGISTER)};

		Optional<Instruction> optional = simulation.getInstructionSet().getBestCompatibleInstruction("add", list);

		if (!optional.isPresent()) fail("Instruction not found.");

		ParameterParseResult[] parameters = new ParameterParseResult[]{
				ParameterParseResult.builder().register(t2.getIdentifier()).build(),
				ParameterParseResult.builder().register(t1.getIdentifier()).build(),
				ParameterParseResult.builder().register(t0.getIdentifier()).build()
		};

		AssembledInstruction[] instructions = optional.get().assemble(null, 0, parameters);
		if (instructions.length != 1) fail("Incorrect instruction.");


		InstructionExecution<?, ?> execution = instructions[0].getBasicOrigin()
				.generateExecution(simulation, instructions[0]).orElse(null);

		assertTrue(execution instanceof SingleCycleExecution, "Execution is not a single cycle execution.");

		((SingleCycleExecution<?>) execution).execute();
		assertEquals(23, t2.getValue(), "Bad add instruction result.");
	}

	@Test
	void testOverflow() {
		Registers set = simulation.getRegisters();
		Register t0 = set.getRegister("t0").get();
		Register t1 = set.getRegister("t1").get();
		Register t2 = set.getRegister("t2").get();
		t0.setValue(Integer.MAX_VALUE);
		t1.setValue(20);

		ParameterParseResult[] parameters = new ParameterParseResult[]{
				ParameterParseResult.builder().register(t2.getIdentifier()).build(),
				ParameterParseResult.builder().register(t1.getIdentifier()).build(),
				ParameterParseResult.builder().register(t0.getIdentifier()).build()
		};

		InstructionAdd add = new InstructionAdd();
		AssembledInstruction instruction = add.assembleBasic(parameters);
		InstructionExecution<?, ?> execution = add.generateExecution(simulation, instruction).orElse(null);

		assertTrue(execution instanceof SingleCycleExecution, "Execution is not a single cycle execution.");

		try {
			((SingleCycleExecution<?>) execution).execute();
			fail("Execution didn't throw an exception.");
		} catch (RuntimeInstructionException ex) {
			assertEquals(ex.getMessage(), "Integer overflow.", "Exception caught, but it's not an Integer Overflow exception.");
		}
	}
}