package net.jamsimulator.jams.gui.display.mips.element;

import net.jamsimulator.jams.gui.display.mips.MipsDisplayError;
import net.jamsimulator.jams.gui.main.WorkingPane;
import net.jamsimulator.jams.gui.project.MipsProjectPane;
import net.jamsimulator.jams.mips.instruction.Instruction;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.register.MIPS32RegisterSet;
import net.jamsimulator.jams.mips.register.RegisterSet;
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

	@Override
	public List<String> getStyles() {
		if(hasErrors()) return Arrays.asList("assembly-instruction", "assembly-error");
		return Collections.singletonList("assembly-instruction");
	}

	@Override
	public void searchErrors(WorkingPane pane, MipsFileElements elements) {
		if (!(pane instanceof MipsProjectPane)) return;
		MipsProject project = ((MipsProjectPane) pane).getProject();
		InstructionSet set = project.getInstructionSet();

		//TODO change this
		RegisterSet registerSet = new MIPS32RegisterSet();

		List<ParameterType>[] types = new List[parameters.size()];

		for (int i = 0; i < parameters.size(); i++) {
			types[i] = parameters.get(i).checkGlobalErrors(registerSet);
		}

		Instruction instruction = set.getBestCompatibleInstruction(text, types).orElse(null);
		if (instruction == null) {
			errors.add(MipsDisplayError.INSTRUCTION_NOT_FOUND);
		}

	}

}
