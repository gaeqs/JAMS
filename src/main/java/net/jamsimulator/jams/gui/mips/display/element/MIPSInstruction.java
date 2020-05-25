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

import net.jamsimulator.jams.gui.mips.display.MIPSEditorError;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.project.mips.MipsProject;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MIPSInstruction extends MIPSCodeElement {

	private String instruction;
	private final List<MIPSInstructionParameter> parameters;
	private final Set<String> usedLabels;

	public MIPSInstruction(MIPSFileElements elements, int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
		this.parameters = new ArrayList<>();
		parseText(elements);

		usedLabels = new HashSet<>();
		for (MIPSInstructionParameter parameter : parameters) {
			parameter.getLabelParameterPart().ifPresent(usedLabels::add);
		}
	}

	@Override
	public String getSimpleText() {
		return instruction;
	}

	public List<MIPSInstructionParameter> getParameters() {
		return parameters;
	}

	public Set<String> getUsedLabels() {
		return usedLabels;
	}

	@Override
	public void move(int offset) {
		super.move(offset);
		parameters.forEach(parameter -> parameter.getParts().forEach(target -> target.move(offset)));
	}

	@Override
	public List<String> getStyles() {
		return Collections.singletonList("mips-instruction");
	}

	@Override
	public void refreshMetadata(MIPSFileElements elements) {
		errors.clear();

		MipsProject project = elements.getProject().orElse(null);
		InstructionSet set = project.getData().getInstructionSet();

		RegistersBuilder builder = project.getData().getRegistersBuilder();
		List<ParameterType>[] types = new List[parameters.size()];

		for (int i = 0; i < parameters.size(); i++) {
			types[i] = parameters.get(i).refreshMetadata(builder);
		}

		Instruction instruction = set.getBestCompatibleInstruction(text, types).orElse(null);
		if (instruction == null) {
			errors.add(MIPSEditorError.INSTRUCTION_NOT_FOUND);
		}
	}

	private void parseText(MIPSFileElements elements) {
		Map<Integer, String> parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(text, false, " ", ",", "\t");
		if (parts.isEmpty()) return;

		//Sorts all entries by their indices.
		List<Map.Entry<Integer, String>> stringParameters = parts.entrySet().stream()
				.sorted(Comparator.comparingInt(Map.Entry::getKey)).collect(Collectors.toList());

		//The first entry is the instruction itself.
		Map.Entry<Integer, String> first = stringParameters.get(0);
		instruction = first.getValue();
		stringParameters.remove(0);

		//Adds all parameters.
		for (Map.Entry<Integer, String> entry : stringParameters) {
			parameters.add(new MIPSInstructionParameter(elements, startIndex + entry.getKey(), entry.getValue()));
		}
	}
}
