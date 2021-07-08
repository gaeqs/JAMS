/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.directive.set.MIPS32DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.MIPS32r6InstructionSet;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.register.MIPS32Registers;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.MIPSSimulationData;
import net.jamsimulator.jams.mips.syscall.SimulationSyscallExecutions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssemblerTest {

	@SuppressWarnings("OptionalGetWithoutIsPresent")
	@Test
	void testAssemble() {
		Map<String, String> files = new HashMap<>();
		List<String> program = new ArrayList<>();

		program.add(".data");
		program.add(".byte 5 9 6 2");
		program.add(".extern global 8");
		program.add("sum: .word 0x10");
		program.add("wordTest: .byte 0");
		program.add("wordTest2: .word wordTest");
		program.add(".text");
		program.add(".eqv ONETWOZERO $s1, $s2, $s0");
		program.add("add ONETWOZERO#ADDS");
		program.add("lw $t0, sum");
		//program.add("addiu $t0, $zero, 5");
		program.add("add $t1, $zero, $zero");
		program.add("loop: beq $t0, $t1, end");
		program.add("addiu $t1, $t1, 1");
		program.add("b loop");
		program.add("end: addiu $t2, $zero, 3");
		program.add("mul $t1, $t1, $t2");
		program.add("sw $t1, sum");
		program.add("lw $t1, sum");
		program.add("lw $s0, wordTest2");

		files.put("test.asm", String.join("\n", program));

		MIPS32Assembler assembler = new MIPS32Assembler(
				files,
				MIPS32r6InstructionSet.INSTANCE,
				MIPS32DirectiveSet.INSTANCE,
				new MIPS32Registers(), new MIPS32Memory(), null);
		assembler.assemble();
		SimulationSyscallExecutions executions = new SimulationSyscallExecutions();

		MIPSSimulationData data = new MIPSSimulationData(executions, new File(""), null, assembler.getOriginals(), assembler.getAllLabels(),
				true, true, true, true, true);
		MIPSSimulation<?> simulation = assembler.createSimulation(SingleCycleArchitecture.INSTANCE, data);

		assertEquals(0x02508820, simulation.getMemory().getWord(simulation.getRegisters().getProgramCounter().getValue()));

		System.out.println("Extern next address: " + (assembler.getAssemblerData().getCurrentExtern() - MIPS32Memory.DATA));

		System.out.println("Starting simulation");
		simulation.executeAll();
		simulation.waitForExecutionFinish();
		System.out.println("Simulation end");
		System.out.println("$t1: " + simulation.getRegisters().getRegister("t1").get().getValue());
		System.out.println("$s0: 0x" + Integer.toHexString(simulation.getRegisters().getRegister("s0").get().getValue()));
		System.out.println("$ra: 0x" + Integer.toHexString(simulation.getRegisters().getRegister("ra").get().getValue()));

		//Check add

		byte[] values = {(byte) 5, (byte) 9, (byte) 6, (byte) 2};
		for (int i = 0; i < 4; i++) {
			assertEquals(values[i], simulation.getMemory().getByte(MIPS32Memory.STATIC_DATA + i), "Incorrect data.");
		}
	}

}