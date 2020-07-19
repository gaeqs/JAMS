package net.jamsimulator.jams.gui.mips.editor;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.mips.editor.element.*;

import java.util.Iterator;

public class MIPSCodeFormatter {

	private MIPSFileElements elements;

	private char tabChar;
	private int tabCharNumber;
	private boolean preserveTabs;

	private MIPSSpaces afterInstruction;
	private MIPSSpaces afterInstructionParameter;
	private MIPSSpaces afterDirective;
	private MIPSSpaces afterDirectiveParameter;

	private int maxBlankLines;

	public MIPSCodeFormatter(MIPSFileElements elements) {
		this.elements = elements;
		loadFromConfig();
	}

	public MIPSCodeFormatter(MIPSFileElements elements, char tabChar, int tabCharNumber, boolean preserveTabs,
							 MIPSSpaces afterInstruction, MIPSSpaces afterInstructionParameter,
							 MIPSSpaces afterDirective, MIPSSpaces afterDirectiveParameter, int maxBlankLines) {
		this.elements = elements;
		this.tabChar = tabChar;
		this.tabCharNumber = tabCharNumber;
		this.preserveTabs = preserveTabs;
		this.afterInstruction = afterInstruction;
		this.afterInstructionParameter = afterInstructionParameter;
		this.afterDirective = afterDirective;
		this.afterDirectiveParameter = afterDirectiveParameter;
		this.maxBlankLines = maxBlankLines;
	}


	public String format() {
		StringBuilder builder = new StringBuilder();
		int blankLines = 0;

		boolean first = true;
		for (MIPSLine line : elements.getLines()) {
			//CHECK BLANK LINES
			if (line.isEmpty()) {
				if (!first && blankLines < maxBlankLines) {
					builder.append('\n');
				}
				blankLines++;
				continue;
			} else {
				blankLines = 0;
			}

			if (first) first = false;
			else builder.append('\n');

			line.getLabel().ifPresent(target -> builder.append(target.getText()));
			int amount = preserveTabs ? Math.max(tabCharNumber, line.getTabsAmount()) : tabCharNumber;
			while (amount > 0) {
				builder.append(tabChar);
				amount -= tabChar == '\t' ? 4 : 1;
			}
			line.getInstruction().ifPresent(target -> formatInstruction(builder, target));
			line.getDirective().ifPresent(target -> formatDirective(builder, target));
			line.getComment().ifPresent(target -> {
				if (line.getDirective().isPresent() || line.getInstruction().isPresent())
					builder.append(" ");
				builder.append(target.getSimpleText());
			});
		}


		return builder.toString();
	}


	private void formatInstruction(StringBuilder builder, MIPSInstruction instruction) {
		builder.append(instruction.getSimpleText());
		if (!instruction.getParameters().isEmpty()) builder.append(afterInstruction.getValue());

		Iterator<MIPSInstructionParameter> iterator = instruction.getParameters().iterator();
		MIPSInstructionParameter parameter;
		while (iterator.hasNext()) {
			parameter = iterator.next();
			builder.append(parameter.getText().trim());
			if (iterator.hasNext()) builder.append(afterInstructionParameter.getValue());
		}
	}


	private void formatDirective(StringBuilder builder, MIPSDirective instruction) {
		builder.append(instruction.getSimpleText());
		if (!instruction.getParameters().isEmpty()) builder.append(afterDirective.getValue());

		Iterator<MIPSDirectiveParameter> iterator = instruction.getParameters().iterator();
		MIPSDirectiveParameter parameter;
		while (iterator.hasNext()) {
			parameter = iterator.next();
			builder.append(parameter.getText().trim());
			if (iterator.hasNext()) builder.append(afterDirectiveParameter.getValue());
		}
	}


	private void loadFromConfig() {
		Configuration c = Jams.getMainConfiguration();
		boolean useTabs = (boolean) c.get("editor.mips.use_tabs").orElse(false);
		tabChar = useTabs ? '\t' : ' ';
		tabCharNumber = 4;
		preserveTabs = (boolean) c.get("editor.mips.preserve_tabs").orElse(false);
		afterInstruction = c.getEnum(MIPSSpaces.class, "editor.mips.space_after_instruction").orElse(MIPSSpaces.SPACE);
		afterInstructionParameter = c.getEnum(MIPSSpaces.class, "editor.mips.space_after_instruction_parameter").orElse(MIPSSpaces.SPACE);
		afterDirective = c.getEnum(MIPSSpaces.class, "editor.mips.space_after_directive").orElse(MIPSSpaces.SPACE);
		afterDirectiveParameter = c.getEnum(MIPSSpaces.class, "editor.mips.space_after_directive_parameter").orElse(MIPSSpaces.SPACE);

		maxBlankLines = (int) c.get("editor.mips.maximum_blank_lines").orElse(0);
		if (maxBlankLines < 0) maxBlankLines = 0;
	}
}
