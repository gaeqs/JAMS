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

import net.jamsimulator.jams.gui.mips.editor.element.MIPSFileElements;
import net.jamsimulator.jams.mips.assembler.MIPS32AssemblingFile;
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.directive.parameter.DirectiveParameterType;

public class DirectiveFMask extends Directive {

	/*
		EMPTY DIRECTIVE
	 */

	public static final String NAME = "fmask";
	private static final DirectiveParameterType[] PARAMETERS = {DirectiveParameterType.INT, DirectiveParameterType.BYTE};

	public DirectiveFMask() {
		super(NAME, PARAMETERS, false, false);
	}

	@Override
	public int execute(int lineNumber, String line, String[] parameters, MIPS32AssemblingFile file) {
		return -1;
	}

	@Override
	public void postExecute(String[] parameters, MIPS32AssemblingFile file, int lineNumber, int address) {

	}

	@Override
	public boolean isParameterValidInContext(int index, String value, MIPSFileElements context) {
		return isParameterValid(index, value);
	}
}
