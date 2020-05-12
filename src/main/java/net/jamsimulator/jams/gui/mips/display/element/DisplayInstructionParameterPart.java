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

import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.gui.mips.display.MipsDisplayError;
import net.jamsimulator.jams.project.mips.MipsProject;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisplayInstructionParameterPart extends MipsCodeElement {

	private final DisplayInstruction instruction;
	private final int instructionIndex;
	private InstructionParameterPartType type;

	public DisplayInstructionParameterPart(DisplayInstruction instruction, int instructionIndex,
										   int startIndex, int endIndex, String text,
										   InstructionParameterPartType type) {
		super(startIndex, endIndex, text);
		this.instruction = instruction;
		this.instructionIndex = instructionIndex;
		this.type = type;
	}

	public DisplayInstruction getInstruction() {
		return instruction;
	}

	public int getInstructionIndex() {
		return instructionIndex;
	}

	public InstructionParameterPartType getType() {
		return type;
	}

	@Override
	public String getText() {
		return super.getText();
	}

	@Override
	public List<String> getStyles() {
		if (hasErrors()) return Arrays.asList(type.getCssClass(), "mips-error");
		return Collections.singletonList(type.getCssClass());
	}

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {
		errors.clear();
		if (type == InstructionParameterPartType.LABEL) {
			if (!elements.hasLabel(text)) {
				errors.add(MipsDisplayError.LABEL_NOT_FOUND);
				type = InstructionParameterPartType.LABEL;
			}
		}
	}

	public boolean searchLabelErrors(List<String> labels, List<String> globalLabels) {
		if (type != InstructionParameterPartType.LABEL && type != InstructionParameterPartType.GLOBAL_LABEL)
			return false;

		boolean inGlobal = globalLabels.contains(text);
		boolean changed = type == InstructionParameterPartType.GLOBAL_LABEL != inGlobal;
		type = inGlobal ? InstructionParameterPartType.GLOBAL_LABEL : InstructionParameterPartType.LABEL;

		if (inGlobal || labels.contains(text)) {
			if (!errors.contains(MipsDisplayError.LABEL_NOT_FOUND)) return changed;
			errors.remove(MipsDisplayError.LABEL_NOT_FOUND);
		} else {
			if (errors.contains(MipsDisplayError.LABEL_NOT_FOUND)) return changed;
			errors.add(MipsDisplayError.LABEL_NOT_FOUND);
		}
		return true;
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

	@Override
	public void populatePopup(VBox popup) {
		populatePopupWithErrors(popup);
	}
}
