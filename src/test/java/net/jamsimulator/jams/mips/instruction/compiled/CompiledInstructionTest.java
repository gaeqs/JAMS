package net.jamsimulator.jams.mips.instruction.compiled;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompiledInstructionTest {

	static CompiledRInstruction instruction;

	@BeforeAll
	static void initialize() {
		//add $9, $10, $11
		instruction = new CompiledRInstruction(0, 10, 11, 9,
				0, 0x20, null, null) {
			@Override
			public void execute() {


			}
		};
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