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

package net.jamsimulator.jams.mips.instruction.assembled;

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
		instruction = new InstructionAdd.Assembled(10, 11, 9, add, add);
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