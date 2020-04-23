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

package net.jamsimulator.jams.mips.assembler.directive;

import net.jamsimulator.jams.mips.assembler.Assembler;
import net.jamsimulator.jams.mips.assembler.AssemblingFile;
import net.jamsimulator.jams.utils.Validate;

import java.util.Objects;

/**
 * Represents a directive. Directive are the direct equivalent to the preprocessor code in C.
 * They are used to give orders to the assembler.
 */
public abstract class Directive {

	private final String name;


	public Directive(String name) {
		Validate.notNull(name, "Name cannot be null!");
		this.name = name;
	}

	/**
	 * Returns the name of this directive.
	 *
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Executes the directive in the given assembler.
	 *
	 * @param lineNumber the line number the directive is at.
	 * @param line       the line of the directive.
	 * @param parameters the parameters of the directive.
	 * @param assembler  the assembler.
	 * @return the amount of bytes that have been allocated for this directive.
	 */
	public abstract int execute(int lineNumber, String line, String[] parameters, Assembler assembler);

	/**
	 * This method is executed after all labels, instructions and directives had been decoded.
	 *
	 * @param parameters the parameters of the directive.
	 * @param assembler  the assembler.
	 * @param file       the file where the directive is located at.
	 * @param lineNumber the line number the directive is at.
	 * @param address    the start of the memory address dedicated to this directive in the method {@link #execute(int, String, String[], Assembler)}.
	 */
	public abstract void postExecute(String[] parameters, Assembler assembler, AssemblingFile file, int lineNumber, int address);


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Directive directive = (Directive) o;
		return name.equals(directive.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}
