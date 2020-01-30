package net.jamsimulator.jams.mips.register;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegistersTest {

	static Registers registers = new Registers();

	@Test
	void getProgramCounter() {
		assertEquals(0x00400000, registers.getProgramCounter().getValue(), "Bad program counter.");
	}

	@Test
	void getHighRegister() {
		registers.getHighRegister().setValue(3000);
		assertEquals(3000, registers.getHighRegister().getValue(), "Bad high register value.");
	}

	@Test
	void getLowRegister() {
		registers.getLowRegister().setValue(3000);
		assertEquals(3000, registers.getLowRegister().getValue(), "Bad low register value.");
	}

	@Test
	void getRegister() {
		Optional<Register> optional = registers.getRegister("t7");
		assertTrue(optional.isPresent(), "Register not found.");
		Register register = optional.get();
		register.setValue(20);
		assertEquals(20, register.getValue(), "Bad register value.");
	}

	@Test
	void getCoprocessor0Register() {
		Optional<Register> optional = registers.getCoprocessor0Register("12");
		assertTrue(optional.isPresent(), "Register not found.");
		Register register = optional.get();
		register.setValue(20);
		assertEquals(20, register.getValue(), "Bad register value.");
	}

	@Test
	void getCoprocessor1Register() {
		Optional<Register> optional = registers.getCoprocessor1Register("f9");
		assertTrue(optional.isPresent(), "Register not found.");
		Register register = optional.get();
		register.setValue(20);
		assertEquals(20, register.getValue(), "Bad register value.");
	}
}