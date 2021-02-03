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

import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.project.mips.MIPSProject;

import java.util.*;

public class MIPSInstructionParameter {

	private final MIPSLine line;
	private final MIPSInstruction instruction;
	private final int index;
	private final int start;
	private final String text;
	private final List<MIPSInstructionParameterPart> parts;
	private boolean valid;

	public MIPSInstructionParameter(MIPSLine line, MIPSInstruction instruction, MIPSFileElements elements, int start, String text, int index, ParameterType hint) {
		this.line = line;
		this.instruction = instruction;
		this.index = index;
		this.start = start;
		this.text = text;
		this.parts = new ArrayList<>();

		if (hint != null) parseTextWithHint(start, elements, hint);
		else parseTextSimple(start, elements);
	}

	public int getIndex() {
		return index;
	}

	public int getStart() {
		return start;
	}

	public String getText() {
		return text;
	}

	public MIPSInstruction getInstruction() {
		return instruction;
	}

	public List<MIPSInstructionParameterPart> getParts() {
		return parts;
	}

	public boolean isValid() {
		return valid;
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
		valid = !types.isEmpty();
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

			if (partStart > partEnd) continue;

			parts.add(new MIPSInstructionParameterPart(line, elements, partStart, partEnd,
					text.substring(partStart - start, partEnd - start), this, i, hint.getPart(i)));
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
			parts.add(new MIPSInstructionParameterPart(line, elements, start, start + string.length(), string, this, 0, null));
			return;
		}

		if (string.indexOf('(') != -1 || string.indexOf(')') != -1) {
			List<ParameterType> types = ParameterType.getCompatibleParameterTypes(string, project.get().getData().getRegistersBuilder());
			if (types.size() == 1 && types.get(0) == ParameterType.LABEL) {
				parts.add(new MIPSInstructionParameterPart(line, elements, start, start + string.length(), string, this, 0, null));
				return;
			}
		}

		var parts = new HashMap<Integer, String>();

		int lastAddition = string.lastIndexOf('+');
		if (lastAddition >= 0) {
			parts.put(0, string.substring(0, lastAddition));
			string = string.substring(lastAddition + 1);
		}

		int lastParenthesis = string.lastIndexOf('(');
		if (lastParenthesis >= 0 && lastParenthesis < string.lastIndexOf(')')) {
			parts.put(lastAddition + 1, string.substring(0, lastParenthesis));
			string = string.substring(lastParenthesis + 1, string.lastIndexOf(')'));
		}

		if (!string.isEmpty()) {
			parts.put(lastAddition + lastParenthesis + 2, string);
		}

		List<Map.Entry<Integer, String>> sorted = new ArrayList<>(parts.entrySet());
		sorted.sort(Comparator.comparingInt(Map.Entry::getKey));

		int i = 0;
		for (Map.Entry<Integer, String> entry : sorted) {
			this.parts.add(new MIPSInstructionParameterPart(
					line,
					elements,
					start + entry.getKey(),
					start + entry.getKey() + entry.getValue().length(),
					entry.getValue(),
					this,
					i++,
					null));
		}
	}
}
