package net.jamsimulator.jams.mips.instruction.compiled;

import net.jamsimulator.jams.mips.simulation.Simulation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompiledJInstructionTest {

	static CompiledJInstruction instruction;

	@BeforeAll
	static void initialize() {
		//j this
		instruction = new CompiledJInstruction(2, 0x00400000, null, null) {
			@Override
			public void execute(Simulation simulation) {

			}
		};
	}


	@Test
	void getOperationCode() {
		assertEquals(2, instruction.getOperationCode(), "Bad OP code.");
	}

	@Test
	void getAddress() {
		assertEquals(0x00100000, instruction.getAddress(), "Bad address.");
	}

	@Test
	void getAbsoluteAddress() {
		int pc = 0x00400000;
		assertEquals(0x00400000, instruction.getAbsoluteAddress(pc), "Bad absolute address.");
	}
}