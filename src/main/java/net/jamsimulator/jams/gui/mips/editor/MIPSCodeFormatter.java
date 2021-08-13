/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.gui.mips.editor;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.configuration.Configuration;
import net.jamsimulator.jams.gui.mips.editor.element.*;

import java.util.Iterator;

public class MIPSCodeFormatter {

    private final MIPSFileElements elements;

    private char tabChar;
    private int tabCharNumber;
    private boolean preserveTabs;
    private boolean preserveTabsBeforeLabels;

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

            if (preserveTabsBeforeLabels) {
                int amount = line.getTabsAmountBeforeLabel();
                while (amount > 0) {
                    builder.append(tabChar);
                    amount -= tabChar == '\t' ? 4 : 1;
                }
            }

            line.getLabel().ifPresent(target -> builder.append(target.getLabel()).append(":"));


            int amount = preserveTabs ? Math.max(tabCharNumber, line.getTabsAmountAfterLabel()) : tabCharNumber;
            while (amount > 0) {
                builder.append(tabChar);
                amount -= tabChar == '\t' ? 4 : 1;
            }

            line.getInstruction().ifPresent(target -> formatInstruction(builder, target));
            line.getDirective().ifPresent(target -> formatDirective(builder, target));
            line.getMacroCall().ifPresent(target -> formatMacro(builder, target));
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

    private void formatMacro(StringBuilder builder, MIPSMacroCall macro) {
        builder.append(macro.getSimpleText()).append(afterInstruction.getValue());
        if (!macro.getParameters().isEmpty()) builder.append("(");
        else {
            builder.append("()");
            return;
        }

        Iterator<MIPSMacroCallParameter> iterator = macro.getParameters().iterator();
        MIPSMacroCallParameter parameter;
        while (iterator.hasNext()) {
            parameter = iterator.next();
            builder.append(parameter.getText().trim());
            if (iterator.hasNext()) builder.append(afterInstructionParameter.getValue());
        }

        builder.append(")");
    }


    private void loadFromConfig() {
        Configuration c = Jams.getMainConfiguration();
        boolean useTabs = (boolean) c.get("editor.mips.use_tabs").orElse(false);
        tabChar = useTabs ? '\t' : ' ';
        tabCharNumber = 4;
        preserveTabs = (boolean) c.get("editor.mips.preserve_tabs").orElse(false);
        preserveTabsBeforeLabels = (boolean) c.get("editor.mips.preserve_tabs_before_labels").orElse(false);
        afterInstruction = c.getEnum(MIPSSpaces.class, "editor.mips.space_after_instruction").orElse(MIPSSpaces.SPACE);
        afterInstructionParameter = c.getEnum(MIPSSpaces.class, "editor.mips.space_after_instruction_parameter").orElse(MIPSSpaces.SPACE);
        afterDirective = c.getEnum(MIPSSpaces.class, "editor.mips.space_after_directive").orElse(MIPSSpaces.SPACE);
        afterDirectiveParameter = c.getEnum(MIPSSpaces.class, "editor.mips.space_after_directive_parameter").orElse(MIPSSpaces.SPACE);

        maxBlankLines = (int) c.get("editor.mips.maximum_blank_lines").orElse(0);
        if (maxBlankLines < 0) maxBlankLines = 0;
    }
}
