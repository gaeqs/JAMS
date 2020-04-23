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

package net.jamsimulator.jams.mips.assembler.directive.defaults;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.AssemblerData;
import net.jamsimulator.jams.mips.assembler.AssemblingFile;
import net.jamsimulator.jams.mips.assembler.directive.Directive;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;

import java.nio.charset.StandardCharsets;

public class DirectiveAsciiz extends Directive {

	public static final String NAME = "asciiz";

	public DirectiveAsciiz() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Assembler assembler) {
		if (parameters.length != 1)
			throw new AssemblerException(lineNumber, "." + NAME + " must have one string parameter.");

		String s = parameters[0];
		if (!s.startsWith("\"") && !s.endsWith("\""))
			throw new AssemblerException(lineNumber, "." + NAME + " parameter '" + s + "' is not a string.");

		AssemblerData data = assembler.getAssemblerData();
		data.align(0);

		int start = data.getCurrent();

		for (byte b : s.getBytes(StandardCharsets.US_ASCII)) {
			assembler.getMemory().setByte(data.getCurrent(), b);
			data.addCurrent(1);
		}
		assembler.getMemory().setByte(data.getCurrent(), (byte) 0);
		data.addCurrent(1);
		return start;
	}


	@Override
	public void postExecute(String[] parameters, Assembler assembler, AssemblingFile file, int lineNumber, int address) {

	}
}
