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

package net.jamsimulator.jams.gui.mips.display.element;

import net.jamsimulator.jams.gui.mips.display.MipsDisplayError;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

import java.util.ArrayList;
import java.util.List;

public class InstructionParameter {

	private final String text;
	private final DisplayInstruction instruction;
	private final List<DisplayInstructionParameterPart> parts;

	public InstructionParameter(String text, DisplayInstruction instruction) {
		this.text = text;
		this.instruction = instruction;
		this.parts = new ArrayList<>();
	}

	public String getText() {
		return text;
	}

	public DisplayInstruction getInstruction() {
		return instruction;
	}

	public List<DisplayInstructionParameterPart> getParts() {
		return parts;
	}

	public void addPart(DisplayInstructionParameterPart part) {
		parts.add(part);
	}

	public List<ParameterType> checkGlobalErrors(RegistersBuilder builder) {
		List<ParameterType> types = ParameterType.getCompatibleParameterTypes(text, builder);
		if (types.isEmpty()) parts.forEach(target -> target.errors.add(MipsDisplayError.INVALID_INSTRUCTION_PARAMETER));
		return types;
	}

	public boolean searchLabelErrors(List<String> labels, List<String> globalLabels) {
		boolean updated = false;

		for (DisplayInstructionParameterPart part : parts) {
			updated |= part.searchLabelErrors(labels, globalLabels);
		}
		return updated;
	}
}
