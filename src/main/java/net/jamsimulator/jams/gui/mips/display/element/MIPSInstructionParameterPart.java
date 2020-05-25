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
import net.jamsimulator.jams.project.mips.MIPSFilesToAssemble;
import net.jamsimulator.jams.project.mips.MipsProject;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MIPSInstructionParameterPart extends MIPSCodeElement {

	private InstructionParameterPartType type;

	public MIPSInstructionParameterPart(MIPSFileElements elements, int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
		this.type = InstructionParameterPartType.getByString(text, elements.getProject().orElse(null));
	}

	public InstructionParameterPartType getType() {
		return type;
	}

	@Override
	public String getSimpleText() {
		return text;
	}

	@Override
	public List<String> getStyles() {
		if (hasErrors()) return Arrays.asList(type.getCssClass(), "mips-error");
		return Collections.singletonList(type.getCssClass());
	}

	@Override
	public void refreshMetadata(MIPSFileElements elements) {
		errors.clear();
		if (type != InstructionParameterPartType.LABEL && type != InstructionParameterPartType.GLOBAL_LABEL) return;

		MIPSFilesToAssemble filesToAssemble = elements.getFilesToAssemble().orElse(null);

		boolean containsLocal = elements.getLabels().contains(text);
		boolean isGlobal;
		if (filesToAssemble == null) {
			isGlobal = containsLocal && elements.getSetAsGlobalLabel().contains(text);
		} else {
			isGlobal = filesToAssemble.getGlobalLabels().contains(text);
		}

		type = isGlobal ? InstructionParameterPartType.GLOBAL_LABEL : InstructionParameterPartType.LABEL;

		if (!containsLocal && !isGlobal) {
			errors.add(MIPSEditorError.LABEL_NOT_FOUND);
		}
	}

	public enum InstructionParameterPartType {
		REGISTER("mips-instruction-parameter-register"),
		IMMEDIATE("mips-instruction-parameter-immediate"),
		STRING("mips-instruction-parameter-string"),
		LABEL("mips-instruction-parameter-label"),
		GLOBAL_LABEL("mips-instruction-parameter-global-label");

		private final String cssClass;

		InstructionParameterPartType(String cssClass) {
			this.cssClass = cssClass;
		}

		public String getCssClass() {
			return cssClass;
		}

		public static InstructionParameterPartType getByString(String string, MipsProject project) {
			if (NumericUtils.isInteger(string)) return IMMEDIATE;

			if (project == null) {
				if (string.startsWith("$")) return REGISTER;
			} else {
				if (project.getData().getRegistersBuilder().getValidRegistersStarts()
						.stream().anyMatch(target -> string.startsWith(target.toString()))) return REGISTER;
			}

			if (StringUtils.isStringOrChar(string)) return STRING;
			return LABEL;
		}
	}
}
