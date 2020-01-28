package net.jamsimulator.jams.mips.instruction;

import net.jamsimulator.jams.mips.parameter.ParameterType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BasicInstructionTest {

	static Instruction instruction;

	@BeforeAll
	static void setUp() {
		instruction = new BasicInstruction("Addition", "add",
				new ParameterType[]{ParameterType.REGISTER, ParameterType.REGISTER, ParameterType.REGISTER}, 0);
	}

	@Test
	void getName() {
		assertEquals("Addition", instruction.getName(), "Bad name.");
	}

	@Test
	void getMnemonic() {
		assertEquals("add", instruction.getMnemonic(), "Bad mnemonic.");
	}

	@Test
	void getParameters() {
		ParameterType[] types = instruction.getParameters();
		assertEquals(3, types.length, "Bad parameters array length.");
		for (int i = 0; i < types.length; i++) {
			assertEquals(ParameterType.REGISTER, types[i], "Bad parameter " + i + ".");
		}
	}
}