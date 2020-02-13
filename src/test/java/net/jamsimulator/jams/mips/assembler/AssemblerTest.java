package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.memory.Mips32Memory;
import net.jamsimulator.jams.mips.register.MIPS32RegisterSet;
import net.jamsimulator.jams.mips.register.Register;
import net.jamsimulator.jams.mips.simulation.Simulation;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssemblerTest {

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
		program.add("addiu $t0, $zero, 5");
		program.add("addiu $t1, $zero, 0");
		program.add("loop: beq $t0, $t1, end");
		program.add("addiu $t1, $t1, 1");
		program.add("b loop");
		program.add("end: addiu $t2, $zero, 3");
		program.add("mul $t1, $t1, $t2");

		Assembler assembler = new MIPS32Assembler(
				new DirectiveSet(true, true),
				new InstructionSet(true, true, true),
				new MIPS32RegisterSet(), new Mips32Memory(),
				Mips32Memory.TEXT, Mips32Memory.STATIC_DATA, Mips32Memory.KERNEL_TEXT, Mips32Memory.KERNEL_DATA,
				Mips32Memory.EXTERN);
		assembler.setData(files);
		assembler.compile();
		Simulation simulation = assembler.createSimulation();

		assertEquals(0x02508820, simulation.getMemory().getWord(simulation.getRegisterSet().getProgramCounter().getValue()));

		System.out.println("Extern next address: " + (assembler.getAssemblerData().getCurrentExtern() - Mips32Memory.DATA));

		System.out.println("Starting simulation");
		Register pc = simulation.getRegisterSet().getProgramCounter();
		while (pc.getValue() < assembler.getAssemblerData().getCurrentText()) {
			simulation.executeNextInstruction(true);
		}
		System.out.println("Simulation end");
		System.out.println("$t1: "+ simulation.getRegisterSet().getRegister("t1").get().getValue());
		System.out.println("$ra: 0x"+ Integer.toHexString(simulation.getRegisterSet().getRegister("ra").get().getValue()));

		//Check add

		byte[] values = {(byte) 5, (byte) 9, (byte) 6, (byte) 2};
		for (int i = 0; i < 4; i++) {
			assertEquals(values[i], simulation.getMemory().getByte(Mips32Memory.STATIC_DATA + i), "Incorrect data.");
		}
	}

}