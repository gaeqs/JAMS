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

package net.jamsimulator.jams.mips.directive.defaults;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.AssemblingFile;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.utils.LabelUtils;

public class DirectiveGlobl extends Directive {

	public static final String NAME = "globl";

	public DirectiveGlobl() {
		super(NAME);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, Assembler assembler) {
		if (parameters.length < 1)
			throw new AssemblerException(lineNumber, "." + NAME + " must have at least one parameter.");

		for (String parameter : parameters) {
			if (!LabelUtils.isLabelLegal(parameter))
				throw new AssemblerException("Illegal label " + parameter + ".");
		}

		for (String parameter : parameters) {
			assembler.setAsGlobalLabel(lineNumber, parameter);

		}

		return -1;
	}

	@Override
	public void postExecute(String[] parameters, Assembler assembler, AssemblingFile file, int lineNumber, int address) {

	}
}