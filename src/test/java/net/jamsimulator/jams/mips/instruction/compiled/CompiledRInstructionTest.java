package net.jamsimulator.jams.mips.instruction.compiled;

import net.jamsimulator.jams.mips.simulation.Simulation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompiledRInstructionTest {

	static CompiledRInstruction instruction;

	@BeforeAll
	static void initialize() {
		//add $9, $10, $11
		instruction = new CompiledRInstruction(0, 10, 11, 9,
				0, 0x20, null, null) {
			@Override
			public void execute(Simulation simulation) {

			}
		};
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