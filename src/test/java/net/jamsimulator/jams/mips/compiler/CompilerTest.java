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

		program.add(".text");
		program.add("add $s1, $s2, $s0");

		Compiler compiler = new Compiler(
				new DirectiveSet(true, true),
				new InstructionSet(true, true, true),
				files, new MIPS32RegisterSet(), new Mips32Memory());

		compiler.initialize(Mips32Memory.TEXT, Mips32Memory.DATA, Mips32Memory.KERNEL_TEXT, Mips32Memory.KERNEL_DATA);
		compiler.compile();
		Simulation simulation = compiler.createSimulation();

		//Check add
		assertEquals(0x02508820, simulation.getMemory().getWord(simulation.getRegisterSet().getProgramCounter().getValue()));
	}

}