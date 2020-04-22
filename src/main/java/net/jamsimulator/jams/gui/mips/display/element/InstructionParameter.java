package net.jamsimulator.jams.gui.mips.display.element;

import net.jamsimulator.jams.gui.mips.display.MipsDisplayError;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.register.builder.RegistersBuilder;

import java.util.ArrayList;
import java.util.List;

public class InstructionParameter {

	private final String text;
	private final DisplayInstruction instruction;
	private final List<DisplayInstructionParameterPart> parts;

	public InstructionParameter(String text, DisplayInstruction instruction) {
		this.text = text;
		this.instruction = instruction;
		this.parts = new ArrayList<>();
	}

	public String getText() {
		return text;
	}

	public DisplayInstruction getInstruction() {
		return instruction;
	}

	public List<DisplayInstructionParameterPart> getParts() {
		return parts;
	}

	public void addPart(DisplayInstructionParameterPart part) {
		parts.add(part);
	}

	public List<ParameterType> checkGlobalErrors(RegistersBuilder builder) {
		List<ParameterType> types = ParameterType.getCompatibleParameterTypes(text, builder);
		if (types.isEmpty()) parts.forEach(target -> target.errors.add(MipsDisplayError.INVALID_INSTRUCTION_PARAMETER));
		return types;
	}

}
