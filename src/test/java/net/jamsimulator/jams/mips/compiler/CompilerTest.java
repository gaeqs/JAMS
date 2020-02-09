package net.jamsimulator.jams.mips.compiler;

import net.jamsimulator.jams.mips.compiler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Mips32Memory;
import net.jamsimulator.jams.mips.register.MIPS32RegisterSet;
import net.jamsimulator.jams.mips.simulation.Simulation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompilerTest {

	@Test
	void testCompiler() {
		List<List<String>> files = new ArrayList<>();
		List<String> program = new ArrayList<>();
		files.add(program);

		program.add(".data");
		program.add(".byte 5 9 6 2");
		program.add(".extern global 8");
		program.add(".text");
		program.add(".eqv ONETWOZERO $s1, $s2, $s0");
		program.add("add ONETWOZERO#ADDS");

		Compiler compiler = new MIPS32Compiler(
				new DirectiveSet(true, true),
				new InstructionSet(true, true, true),
				new MIPS32RegisterSet(), new Mips32Memory(),
				Mips32Memory.TEXT, Mips32Memory.STATIC_DATA, Mips32Memory.KERNEL_TEXT, Mips32Memory.KERNEL_DATA,
				Mips32Memory.EXTERN);
		compiler.setData(files);
		compiler.compile();
		Simulation simulation = compiler.createSimulation();

		System.out.println(compiler.getCompilerData().getCurrentExtern() - Mips32Memory.DATA);

		//Check add
		assertEquals(0x02508820, simulation.getMemory().getWord(simulation.getRegisterSet().getProgramCounter().getValue()));

		byte[] values = {(byte) 5, (byte) 9, (byte) 6, (byte) 2};
		for (int i = 0; i < 4; i++) {
			assertEquals(values[i], simulation.getMemory().getByte(Mips32Memory.STATIC_DATA + i), "Incorrect data.");
		}
	}

}