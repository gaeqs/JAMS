/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.mips.register;

import java.util.Collections;
import java.util.Set;

/**
 * Represents a default MIPS32 {@link Register} set.
 */
public class MIPS32Registers extends Registers {

	public static final Set<Character> VALID_REGISTERS_START = Collections.singleton('$');

	/**
	 * Creates a default MIPS32 {@link Registers} set.
	 */
	public MIPS32Registers() {
		super(VALID_REGISTERS_START, null, null, null);
		loadPrincipalRegisters();
		loadCoprocessor0Registers();
		loadCoprocessor1Registers();
		loadEssentialRegisters();
	}

	protected void loadPrincipalRegisters() {
		int id = 0;
		registers[id] = new Register(this, 0, 0, false, "zero", String.valueOf(id++));
		registers[id] = new Register(this, id, "at", String.valueOf(id++));
		registers[id] = new Register(this, id, "v0", String.valueOf(id++));
		registers[id] = new Register(this, id, "v1", String.valueOf(id++));
		for (int i = 0; i < 4; i++)
			registers[id] = new Register(this, id, "a" + i, String.valueOf(id++));
		for (int i = 0; i < 8; i++)
			registers[id] = new Register(this, id, "t" + i, String.valueOf(id++));
		for (int i = 0; i < 8; i++)
			registers[id] = new Register(this, id, "s" + i, String.valueOf(id++));
		registers[id] = new Register(this, id, "t8", String.valueOf(id++));
		registers[id] = new Register(this, id, "t9", String.valueOf(id++));

		registers[id] = new Register(this, id, "k0", String.valueOf(id++));
		registers[id] = new Register(this, id, "k1", String.valueOf(id++));

		registers[id] = new Register(this, id, 0x10008000, true, "gp", String.valueOf(id++));
		registers[id] = new Register(this, id, 0x7fffeffc, true, "sp", String.valueOf(id++));
		registers[id] = new Register(this, id, 0, true, "fp", String.valueOf(id++));
		registers[id] = new Register(this, id, "ra", String.valueOf(id));
	}

	protected void loadCoprocessor0Registers() {
		coprocessor0Registers[8] = new Register(this, 8, "8");
		coprocessor0Registers[12] = new Register(this, 12, 0x0000ff11, true, "12");
		coprocessor0Registers[13] = new Register(this, 13, "13");
		coprocessor0Registers[14] = new Register(this, 14, "14");
	}

	protected void loadCoprocessor1Registers() {
		for (int i = 0; i < 32; i++) {
			coprocessor1Registers[i] = new Register(this, i, "f" + i, String.valueOf(i));
		}
	}
}
