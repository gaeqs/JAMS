package net.jamsimulator.jams.mips.instruction.assembled;

import net.jamsimulator.jams.mips.instruction.assembled.defaults.AssembledInstructionAdd;
import net.jamsimulator.jams.mips.instruction.basic.defaults.InstructionAdd;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompiledRInstructionTest {

	static AssembledRInstruction instruction;

	@BeforeAll
	static void initialize() {
		//add $9, $10, $11
		InstructionAdd add = new InstructionAdd();
		instruction = new AssembledInstructionAdd(10, 11, 9, add, add);
	}

	@Test
	void getOperationCode() {
		assertEquals(0, instruction.getOperationCode(), "Bad OP code.");
	}

	@Test
	void getFunctionCode() {
		assertEquals(0x20, instruction.getFunctionCode(), "Bad function code.");
	}

	@Test
	void getShamt() {
		assertEquals(0, instruction.getShiftAmount(), "Bad shamt.");
	}

	@Test
	void getDestinationRegister() {
		assertEquals(9, instruction.getDestinationRegister(), "Bad destination register.");
	}

	@Test
	void getTargetRegister() {
		assertEquals(11, instruction.getTargetRegister(), "Bad target register.");
	}

	@Test
	void getSourceRegister() {
		assertEquals(10, instruction.getSourceRegister(), "Bad source register.");
	}
}