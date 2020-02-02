package net.jamsimulator.jams.mips.instruction.compiled;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompiledIInstructionTest {

	static CompiledI16Instruction instruction;

	@BeforeAll
	static void initialize() {
		//addi $9, $10, -100
		instruction = new CompiledI16Instruction(8, 10, 9, Short.toUnsignedInt((short) -100), null, null) {
			@Override
			public void execute() {
			}
		};
	}

	@Test
	void getOperationCode() {
		assertEquals(8, instruction.getOperationCode(), "Bad OP code.");
	}

	@Test
	void getImmediate() {
		assertEquals(65436, instruction.getImmediate(), "Bad immediate.");
	}

	@Test
	void getImmediateAsSigned() {
		assertEquals(-100, instruction.getImmediateAsSigned(), "Bad signed immediate.");
	}

	@Test
	void getTargetRegister() {
		assertEquals(9, instruction.getTargetRegister(), "Bad target register.");
	}

	@Test
	void getSourceRegister() {
		assertEquals(10, instruction.getSourceRegister(), "Bad source register.");
	}
}