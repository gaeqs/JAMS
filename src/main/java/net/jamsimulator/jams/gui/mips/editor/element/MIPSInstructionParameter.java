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
import net.jamsimulator.jams.project.mips.MIPSProject;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MIPSInstructionParameter {

	private final String text;
	private final List<MIPSInstructionParameterPart> parts;

	public MIPSInstructionParameter(MIPSFileElements elements, int start, String text, ParameterType hint) {
		this.text = text;
		this.parts = new ArrayList<>();

		if (hint != null) parseTextWithHint(start, elements, hint);
		else parseTextSimple(start, elements);
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

	private void parseTextWithHint(int start, MIPSFileElements elements, ParameterType hint) {
		int[] indices = hint.split(text);
		int amount = hint.getAmountOfParts();

		int partStart;
		int partEnd;
		for (int i = 0; i < amount; i++) {
			partStart = start + indices[i << 1];
			partEnd = partStart + indices[(i << 1) + 1];
			parts.add(new MIPSInstructionParameterPart(elements, partStart, partEnd,
					text.substring(partStart - start, partEnd - start), hint.getPart(i)));
		}
	}

	private void parseTextSimple(int start, MIPSFileElements elements) {
		StringBuilder builder = new StringBuilder();
		int index = 0;
		for (char c : text.toCharArray()) {
			if (c == '+') {
				addPartSimple(builder.toString(), start, elements);
				builder = new StringBuilder();
				start += index + 1;
			} else {
				builder.append(c);
			}
			index++;
		}
		addPartSimple(builder.toString(), start, elements);
	}

	private void addPartSimple(String string, int start, MIPSFileElements elements) {
		Optional<MIPSProject> project = elements.getProject();
		if (project.isEmpty()) {
			parts.add(new MIPSInstructionParameterPart(elements, start, start + string.length(), string, null));
			return;
		}


		if (string.indexOf('(') != -1 || string.indexOf(')') != -1) {
			List<ParameterType> types = ParameterType.getCompatibleParameterTypes(string, project.get().getData().getRegistersBuilder());
			if (types.size() == 1 && types.get(0) == ParameterType.LABEL) {
				parts.add(new MIPSInstructionParameterPart(elements, start, start + string.length(), string, null));
				return;
			}
		}

		Map<Integer, String> parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(string, false, "+", "(", ")");
		parts.forEach((index, part) -> this.parts.add(new MIPSInstructionParameterPart(elements,
				start + index, start + index + part.length(), part, null)));
	}
}
