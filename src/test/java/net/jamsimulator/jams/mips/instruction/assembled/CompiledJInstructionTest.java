package net.jamsimulator.jams.mips.instruction.assembled;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CompiledJInstructionTest {

	static AssembledJInstruction instruction;

	@BeforeAll
	static void initialize() {
		//b this
		//THERE IS NO J INSTRUCTIONS YET.
	}


	@Test
	void getOperationCode() {
		//assertEquals(2, instruction.getOperationCode(), "Bad OP code.");
	}

	@Test
	void getAddress() {
		//assertEquals(0x00100000, instruction.getAddress(), "Bad address.");
	}

	@Test
	void getAbsoluteAddress() {
		//int pc = 0x00400000;
		//assertEquals(0x00400000, instruction.getAbsoluteAddress(pc), "Bad absolute address.");
	}
}