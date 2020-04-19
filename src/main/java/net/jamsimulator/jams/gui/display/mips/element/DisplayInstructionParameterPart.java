package net.jamsimulator.jams.gui.display.mips.element;

import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.display.mips.MipsDisplayError;
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
			if (!elements.hasLabel(text))
				errors.add(MipsDisplayError.LABEL_NOT_FOUND);
		}
	}

	@Override
	public boolean searchLabelErrors(List<String> labels) {
		if (type != InstructionParameterPartType.LABEL) return false;
		if (labels.contains(text)) {
			if (!errors.contains(MipsDisplayError.LABEL_NOT_FOUND)) return false;
			errors.remove(MipsDisplayError.LABEL_NOT_FOUND);
		} else {
			if (errors.contains(MipsDisplayError.LABEL_NOT_FOUND)) return false;
			errors.add(MipsDisplayError.LABEL_NOT_FOUND);
		}
		return true;
	}

	public enum InstructionParameterPartType {
		REGISTER("mips-instruction-parameter-register"),
		IMMEDIATE("mips-instruction-parameter-immediate"),
		STRING("mips-instruction-parameter-string"),
		LABEL("mips-instruction-parameter-label");

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

	@Override
	public void populatePopup(VBox popup) {
		populatePopupWithErrors(popup);
	}
}
