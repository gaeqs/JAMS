package net.jamsimulator.jams.mips.register;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterSetTest {

	static Registers registerSet = new MIPS32Registers();

	@Test
	void getProgramCounter() {
		assertEquals(0x00400000, registerSet.getProgramCounter().getValue(), "Bad program counter.");
	}

	@Test
	void getRegister() {
		Optional<Register> optional = registerSet.getRegister("t7");
		assertTrue(optional.isPresent(), "Register not found.");
		Register register = optional.get();
		register.setValue(20);
		assertEquals(20, register.getValue(), "Bad register value.");
	}

	@Test
	void getCoprocessor0Register() {
		Optional<Register> optional = registerSet.getCoprocessor0Register("12");
		assertTrue(optional.isPresent(), "Register not found.");
		Register register = optional.get();
		register.setValue(20);
		assertEquals(20, register.getValue(), "Bad register value.");
	}

	@Test
	void getCoprocessor1Register() {
		Optional<Register> optional = registerSet.getCoprocessor1Register("f9");
		assertTrue(optional.isPresent(), "Register not found.");
		Register register = optional.get();
		register.setValue(20);
		assertEquals(20, register.getValue(), "Bad register value.");
	}
}