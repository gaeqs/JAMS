package net.jamsimulator.jams.gui.display.mips.element;

import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.utils.NumericUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisplayInstructionParameterPart extends MipsCodeElement {

	private final DisplayInstruction instruction;
	private final int instructionIndex;
	private final InstructionParameterPartType type;

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

	@Override
	public String getText() {
		return super.getText();
	}

	@Override
	public List<String> getStyles() {
		if (hasErrors()) return Arrays.asList(type.getCssClass(), "assembly-error");
		return Collections.singletonList(type.getCssClass());
	}

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {

	}

	public static enum InstructionParameterPartType {
		REGISTER("assembly-instruction-parameter-register"),
		IMMEDIATE("assembly-instruction-parameter-immediate"),
		STRING("assembly-instruction-parameter-string"),
		LABEL("assembly-instruction-parameter-label");

		private final String cssClass;

		InstructionParameterPartType(String cssClass) {
			this.cssClass = cssClass;
		}

		public String getCssClass() {
			return cssClass;
		}

		public static InstructionParameterPartType getByString(String string) {
			if (NumericUtils.isInteger(string)) return IMMEDIATE;
			if (string.startsWith("$")) return REGISTER;
			if (StringUtils.isStringOrChar(string)) return STRING;
			return LABEL;
		}
	}
}
