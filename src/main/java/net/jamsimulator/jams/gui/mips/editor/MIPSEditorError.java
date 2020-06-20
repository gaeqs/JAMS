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

package net.jamsimulator.jams.gui.mips.editor;

/**
 * This enum recollects all error that can be displayed by a {@link MIPSFileEditor}.
 */
public enum MIPSEditorError {

	//LABELS

	/**
	 * Used when a label has an illegal name.
	 */
	ILLEGAL_LABEL,
	/**
	 * Used when a parameter is pointing to a label that doesn't exist.
	 */
	LABEL_NOT_FOUND,
	/**
	 * Used when a label is duplicated.
	 */
	DUPLICATE_LABEL,
	/**
	 * Used when a global label is duplicated.
	 */
	DUPLICATE_GLOBAL_LABEL,

	//DIRECTIVES
	/**
	 * Used when a directive doesn't exist.
	 */
	DIRECTIVE_NOT_FOUND,

	//DIRECTIVE PARAMETERS

	//INSTRUCTIONS
	/**
	 * Used when an instruction doesn't exist.
	 */
	INSTRUCTION_NOT_FOUND,

	//INSTRUCTION PARAMETERS
	/**
	 * Used when an instruction's parameter has an invalid format.
	 */
	INVALID_INSTRUCTION_PARAMETER
}
