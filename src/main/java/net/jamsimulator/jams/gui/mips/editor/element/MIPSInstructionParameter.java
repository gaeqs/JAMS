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

package net.jamsimulator.jams.gui.mips.editor.element;

import net.jamsimulator.jams.gui.mips.editor.MIPSEditorError;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MIPSInstructionParameter {

	private final String text;
	private final List<MIPSInstructionParameterPart> parts;

	public MIPSInstructionParameter(MIPSFileElements elements, int start, String text) {
		this.text = text;
		this.parts = new ArrayList<>();
		parseText(start, elements);
	}

	public String getText() {
		return text;
	}

	public List<MIPSInstructionParameterPart> getParts() {
		return parts;
	}

	public Optional<String> getLabelParameterPart() {
		for (MIPSInstructionParameterPart part : parts) {
			if (part.getType() == MIPSInstructionParameterPart.InstructionParameterPartType.LABEL
					|| part.getType() == MIPSInstructionParameterPart.InstructionParameterPartType.GLOBAL_LABEL) {
				return Optional.of(part.text);
			}
		}
		return Optional.empty();
	}

	public List<ParameterType> refreshMetadata(RegistersBuilder builder) {
		List<ParameterType> types = ParameterType.getCompatibleParameterTypes(text, builder);
		if (types.isEmpty()) parts.forEach(target -> target.errors.add(MIPSEditorError.INVALID_INSTRUCTION_PARAMETER));
		return types;
	}


	private void parseText(int start, MIPSFileElements elements) {
		Map<Integer, String> parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(text, false, "+", "(", ")");

		//Adds all parts.
		parts.forEach((index, string) -> this.parts.add(
				new MIPSInstructionParameterPart(elements,
						start + index, start + index + string.length(), string)));
	}
}
