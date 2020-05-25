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
import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.mips.directive.defaults.DirectiveGlobl;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.project.mips.MipsProject;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MIPSDirective extends MIPSCodeElement {

	private String directive;
	private final List<MIPSDirectiveParameter> parameters;

	public MIPSDirective(int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
		parameters = new ArrayList<>();
		parseText();
	}

	@Override
	public String getSimpleText() {
		return directive;
	}

	public List<MIPSDirectiveParameter> getParameters() {
		return parameters;
	}

	public boolean isGlobl() {
		return directive.equalsIgnoreCase("." + DirectiveGlobl.NAME);
	}

	@Override
	public void move(int offset) {
		super.move(offset);
		parameters.forEach(parameter -> parameter.move(offset));
	}

	@Override
	public List<String> getStyles() {
		if (hasErrors()) return Arrays.asList("mips-directive", "mips-error");
		return Collections.singletonList("mips-directive");
	}

	@Override
	public void refreshMetadata(MIPSFileElements elements) {
		errors.clear();

		MipsProject project = elements.getProject().orElse(null);
		if (project == null) return;

		DirectiveSet set = project.getData().getDirectiveSet();
		Directive directive = set.getDirective(this.directive.substring(1)).orElse(null);
		if (directive == null) {
			errors.add(MIPSEditorError.DIRECTIVE_NOT_FOUND);
		}
	}


	private void parseText() {
		Map<Integer, String> parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(text, false, " ", ",", "\t");
		if (parts.isEmpty()) return;

		//Sorts all entries by their indices.
		List<Map.Entry<Integer, String>> stringParameters = parts.entrySet().stream()
				.sorted(Comparator.comparingInt(Map.Entry::getKey)).collect(Collectors.toList());

		//The first entry is the directive itself.
		Map.Entry<Integer, String> first = stringParameters.get(0);
		directive = first.getValue();
		stringParameters.remove(0);

		//Adds all parameters.
		MIPSDirectiveParameter parameter;
		for (Map.Entry<Integer, String> entry : stringParameters) {
			parameters.add(new MIPSDirectiveParameter(
					startIndex + entry.getKey(),
					startIndex + entry.getKey() + entry.getValue().length(), entry.getValue()));
		}

		startIndex += first.getKey();
	}
}
