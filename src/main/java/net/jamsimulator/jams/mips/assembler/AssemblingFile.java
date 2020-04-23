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

package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents a list of instructions that are currently being compiled.
 * <p>
 * Local labels and equivalents are stored here.
 */
public class AssemblingFile {

	List<String> rawCode;
	Map<String, Integer> labels;
	List<InstructionSnapshot> instructionSnapshots;
	List<DirectiveSnapshot> directiveSnapshots;
	Map<String, String> equivalents;

	AssemblingFile(List<String> rawCode) {
		this.rawCode = rawCode;
		this.labels = new HashMap<>();
		this.instructionSnapshots = new LinkedList<>();
		this.directiveSnapshots = new LinkedList<>();
		this.equivalents = new HashMap<>();
	}

	public List<String> getRawCode() {
		return rawCode;
	}

	public Map<String, Integer> getLabels() {
		return labels;
	}

	public List<InstructionSnapshot> getInstructionSnapshots() {
		return instructionSnapshots;
	}

	public List<DirectiveSnapshot> getDirectiveSnapshots() {
		return directiveSnapshots;
	}

	public Map<String, String> getEquivalents() {
		return equivalents;
	}

	public int getLabelValue(Assembler assembler, String label, int lineNumber) {
		if (!labels.containsKey(label)) {
			if (!assembler.getGlobalLabels().containsKey(label))
				throw new AssemblerException(lineNumber, "Label " + label + " not found.");
			return assembler.getGlobalLabels().get(label);
		} else {
			return labels.get(label);
		}
	}
}
