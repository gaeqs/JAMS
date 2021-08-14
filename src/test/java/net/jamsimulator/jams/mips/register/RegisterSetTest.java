/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.mips.register;

import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.utils.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RegisterSetTest {

	static Registers registerSet = new MIPS32Registers();

	@Test
	void getProgramCounter() {
		assertEquals(MIPS32Memory.TEXT, registerSet.getProgramCounter().getValue(), "Bad program counter 0x"
				+ StringUtils.addZeros(Integer.toHexString(registerSet.getProgramCounter().getValue()), 8) + ".");
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
		//It cannot be the same because of the mask.
		assertNotEquals(20, register.getValue(), "Bad register value.");
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