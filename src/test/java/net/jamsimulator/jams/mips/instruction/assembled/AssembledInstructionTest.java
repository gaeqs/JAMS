package net.jamsimulator.jams.mips.instruction.assembled;

import net.jamsimulator.jams.mips.instruction.assembled.defaults.AssembledInstructionAdd;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAdd;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AssembledInstructionTest {

	static AssembledRInstruction instruction;

	@BeforeAll
	static void initialize() {
		//add $9, $10, $11

		InstructionAdd add = new InstructionAdd();
		instruction = new AssembledInstructionAdd(10, 11, 9, add, add);
	}

	@Test
	void getValue() {
		assertEquals(0x014b4820, instruction.getCode(), "Bad value code.");
	}

	@Test
	void getOrigin() {
		//TODO Not implemented yet.
	}

	@Test
	void getBasicOrigin() {
		//TODO Not implemented yet.
	}

	@Test
	void getOperationCode() {
		assertEquals(0, instruction.getOperationCode(), "Bad operation code.");
	}

	@Test
	void execute() {
		//TODO Not implemented yet.
	}
}