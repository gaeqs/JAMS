package net.jamsimulator.jams.gui.mips.display.element;

import javafx.scene.layout.VBox;
import net.jamsimulator.jams.gui.mips.display.MipsDisplayError;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.gui.mips.project.MipsProjectPane;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.register.MIPS32Registers;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;
import net.jamsimulator.jams.project.MipsProject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DisplayInstruction extends MipsCodeElement {

	private final List<InstructionParameter> parameters;

	public DisplayInstruction(int startIndex, int endIndex, String text) {
		super(startIndex, endIndex, text);
		this.parameters = new ArrayList<>();
	}

	public List<InstructionParameter> getParameters() {
		return parameters;
	}

	public void addParameter(InstructionParameter parameter) {
		parameters.add(parameter);
	}

	public void appendReformattedCode(StringBuilder builder) {
		builder.append(text);

		boolean first = true;
		builder.append(' ');
		for (InstructionParameter parameter : parameters) {
			if(first) first = false;
			else builder.append(", ");
			builder.append(parameter.getText());
		}
	}

	@Override
	public void move(int offset) {
		super.move(offset);
		parameters.forEach(parameter -> parameter.getParts().forEach(target -> target.move(offset)));
	}

	@Override
	public List<String> getStyles() {
		if (hasErrors()) return Arrays.asList("mips-instruction", "mips-error");
		return Collections.singletonList("mips-instruction");
	}

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {
		errors.clear();
		if (!(pane instanceof MipsProjectPane)) return;
		MipsProject project = ((MipsProjectPane) pane).getProject();
		InstructionSet set = project.getInstructionSet();

		RegistersBuilder builder = project.getRegistersBuilder();
		List<ParameterType>[] types = new List[parameters.size()];

		for (int i = 0; i < parameters.size(); i++) {
			types[i] = parameters.get(i).checkGlobalErrors(builder);
		}

		Instruction instruction = set.getBestCompatibleInstruction(text, types).orElse(null);
		if (instruction == null) {
			errors.add(MipsDisplayError.INSTRUCTION_NOT_FOUND);
		}

	}

	@Override
	public boolean searchLabelErrors(List<String> labels) {
		return false;
	}

	@Override
	public void populatePopup(VBox popup) {
		populatePopupWithErrors(popup);
	}

}