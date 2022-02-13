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

package net.jamsimulator.jams.gui.mips.editor.indexing;

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacroCallMnemonic;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacroParameter;
import net.jamsimulator.jams.gui.editor.code.indexing.line.EditorLineIndex;
import net.jamsimulator.jams.gui.mips.editor.MIPSSpaces;
import net.jamsimulator.jams.gui.mips.editor.indexing.element.MIPSEditorDirectiveMnemonic;
import net.jamsimulator.jams.gui.mips.editor.indexing.element.MIPSEditorDirectiveParameter;
import net.jamsimulator.jams.gui.mips.editor.indexing.element.MIPSEditorInstructionMnemonic;
import net.jamsimulator.jams.gui.mips.editor.indexing.element.MIPSEditorInstructionParameter;
import net.jamsimulator.jams.gui.mips.editor.indexing.inspection.MIPSInspectorManager;
import net.jamsimulator.jams.project.Project;

public class MIPSEditorIndex extends EditorLineIndex<MIPSEditorLine> {

    private static final String NODE_SPACE_AFTER_INSTRUCTION = "editor.mips.space_after_instruction";
    private static final String NODE_SPACE_AFTER_DIRECTIVE = "editor.mips.space_after_directive";
    private static final String NODE_SPACE_AFTER_INSTRUCTION_PARAMETER = "editor.mips.space_after_instruction_parameter";
    private static final String NODE_SPACE_AFTER_DIRECTIVE_PARAMETER = "editor.mips.space_after_directive_parameter";
    private static final String NODE_MAX_BLANK_LINES = "editor.mips.maximum_blank_lines";
    private static final String NODE_USE_TABS = "editor.mips.use_tabs";

    public MIPSEditorIndex(Project project, String name) {
        super(project, MIPSInspectorManager.INSTANCE, name);
    }

    @Override
    protected MIPSEditorLine generateNewLine(int start, int number, String text, ElementScope scope) {
        return new MIPSEditorLine(this, scope, start, number, text);
    }

    @Override
    public String reformat() {
        var builder = new StringBuilder();

        var config = Jams.getMainConfiguration();
        var afterInstruction = config.getEnum(MIPSSpaces.class, NODE_SPACE_AFTER_INSTRUCTION)
                .map(MIPSSpaces::getValue).orElse(" ");
        var afterDirective = config.getEnum(MIPSSpaces.class, NODE_SPACE_AFTER_DIRECTIVE)
                .map(MIPSSpaces::getValue).orElse(" ");
        var afterInstructionParameter = config.getEnum(MIPSSpaces.class, NODE_SPACE_AFTER_INSTRUCTION_PARAMETER)
                .map(MIPSSpaces::getValue).orElse(", ");
        var afterDirectiveParameter = config.getEnum(MIPSSpaces.class, NODE_SPACE_AFTER_DIRECTIVE_PARAMETER)
                .map(MIPSSpaces::getValue).orElse(", ");
        int maxBlankLines = (int) config.get(NODE_MAX_BLANK_LINES).orElse(2);
        var tabText = (boolean) config.get(NODE_USE_TABS).orElse(false) ? "\t" : "    ";

        int blankLineCount = 0;
        int lineIndex = 0;

        for (var line : lines) {

            if (line.isEmpty()) {
                blankLineCount++;
            } else {
                blankLineCount = 0;
            }

            if (blankLineCount <= maxBlankLines && lineIndex > 0) {
                builder.append('\n');
            }

            if (line.label != null) {
                builder.append(line.label.getIdentifier()).append(':');
            }
            builder.append(tabText);

            if (line.directive != null) {
                int i = 0;
                for (var element : line.directive.getElements()) {
                    if (element instanceof MIPSEditorDirectiveMnemonic mnemonic) {
                        builder.append(mnemonic.getText());
                    } else if (element instanceof MIPSEditorDirectiveParameter parameter) {
                        if (i == 1) builder.append(afterDirective);
                        else if (i > 1) builder.append(afterDirectiveParameter);
                        builder.append(parameter.getIdentifier());
                    }
                    i++;
                }
            }

            if (line.instruction != null) {
                int i = 0;
                for (var element : line.instruction.getElements()) {
                    if (element instanceof MIPSEditorInstructionMnemonic mnemonic) {
                        builder.append(mnemonic.getText());
                    } else if (element instanceof MIPSEditorInstructionParameter parameter) {
                        if (i == 1) builder.append(afterInstruction);
                        else if (i > 1) builder.append(afterInstructionParameter);
                        builder.append(parameter.getIdentifier());
                    }
                    i++;
                }
            }

            if (line.macroCall != null) {
                int i = 0;
                for (var element : line.macroCall.getElements()) {
                    if (element instanceof EditorElementMacroCallMnemonic mnemonic) {
                        builder.append(mnemonic.getText()).append(" (");
                    } else if (element instanceof EditorElementMacroParameter parameter) {
                        if (i > 1) builder.append(", ");
                        builder.append(parameter.getIdentifier());
                    }
                    i++;
                }
                builder.append(')');
            }

            if (line.comment != null) {
                if (line.directive != null || line.instruction != null || line.macroCall != null) {
                    builder.append(tabText);
                }
                builder.append(line.comment.getIdentifier());
            }

            lineIndex++;
        }

        indexAll(builder.toString());
        return builder.toString();
    }
}
