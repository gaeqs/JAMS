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
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementLabel;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacroCallMnemonic;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacroCallParameter;
import net.jamsimulator.jams.gui.editor.code.indexing.line.EditorLineIndex;
import net.jamsimulator.jams.gui.mips.editor.MIPSSpaces;
import net.jamsimulator.jams.gui.mips.editor.indexing.element.*;
import net.jamsimulator.jams.gui.mips.editor.indexing.inspection.MIPSInspectorManager;
import net.jamsimulator.jams.mips.directive.defaults.DirectiveMacro;
import net.jamsimulator.jams.project.Project;

import java.util.Comparator;

public class MIPSEditorIndex extends EditorLineIndex<MIPSEditorLine> {

    private static final String NODE_SPACE_AFTER_INSTRUCTION = "editor.mips.space_after_instruction";
    private static final String NODE_SPACE_AFTER_DIRECTIVE = "editor.mips.space_after_directive";
    private static final String NODE_SPACE_AFTER_INSTRUCTION_PARAMETER = "editor.mips.space_after_instruction_parameter";
    private static final String NODE_SPACE_AFTER_DIRECTIVE_PARAMETER = "editor.mips.space_after_directive_parameter";
    private static final String NODE_MAX_BLANK_LINES = "editor.mips.maximum_blank_lines";
    private static final String NODE_USE_TABS = "editor.mips.use_tabs";
    private static final String NODE_PRESERVE_TABS_AFTER_LABEL = "editor.mips.preserve_tabs";
    private static final String NODE_PRESERVE_TABS_BEFORE_LABEL = "editor.mips.preserve_tabs_before_labels";

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

        var config = Jams.getMainConfiguration().data();
        var afterInstruction = config.getEnum(MIPSSpaces.class, NODE_SPACE_AFTER_INSTRUCTION)
                .map(MIPSSpaces::getValue).orElse(" ");
        var afterDirective = config.getEnum(MIPSSpaces.class, NODE_SPACE_AFTER_DIRECTIVE)
                .map(MIPSSpaces::getValue).orElse(" ");
        var afterInstructionParameter = config.getEnum(MIPSSpaces.class, NODE_SPACE_AFTER_INSTRUCTION_PARAMETER)
                .map(MIPSSpaces::getValue).orElse(", ");
        var afterDirectiveParameter = config.getEnum(MIPSSpaces.class, NODE_SPACE_AFTER_DIRECTIVE_PARAMETER)
                .map(MIPSSpaces::getValue).orElse(", ");
        int maxBlankLines = (int) config.get(NODE_MAX_BLANK_LINES).orElse(2);
        var useTabs = (boolean) config.get(NODE_USE_TABS).orElse(false);

        boolean tabsAfterLabel = (boolean) config.get(NODE_PRESERVE_TABS_AFTER_LABEL).orElse(false);
        boolean tabsBeforeLabel = (boolean) config.get(NODE_PRESERVE_TABS_BEFORE_LABEL).orElse(false);

        int blankLineCount = 0;
        int lineIndex = 0;

        for (var line : lines) {
            var tabAcumulator = new StringBuilder();
            if (line.isEmpty()) {
                blankLineCount++;
            } else {
                blankLineCount = 0;
            }

            if (blankLineCount <= maxBlankLines && lineIndex > 0) {
                builder.append('\n');
            }

            if (line.label != null) {
                if (tabsBeforeLabel || tabsAfterLabel) {
                    calculateTabsBeforeLabel(line.label, builder, tabAcumulator, tabsBeforeLabel, useTabs);
                }
                builder.append(line.label.getIdentifier()).append(':');
            }

            if (tabsAfterLabel) {
                calculateTabsAfterLabel(line, builder, tabAcumulator, useTabs);
            } else {
                builder.append(useTabs ? "\t" : "    ");
            }

            if (line.directive != null) {
                int i = 0;
                boolean macro = false;
                for (var element : line.directive.getElements()) {
                    if (element instanceof MIPSEditorDirectiveMnemonic mnemonic) {
                        builder.append(mnemonic.getText());
                        macro = mnemonic.getIdentifier().equals(DirectiveMacro.NAME);
                    } else if (element instanceof MIPSEditorDirectiveMacroParameter parameter) {
                        if (i == 1) builder.append(afterDirective);
                        else if (i == 2) builder.append(afterDirectiveParameter).append("(");
                        else if (i > 1) builder.append(afterDirectiveParameter);
                        builder.append(parameter.getIdentifier());
                        if (i == line.directive.getElements().size() - 1) builder.append(")");
                    } else if (element instanceof MIPSEditorDirectiveParameter parameter) {
                        if (i == 1) builder.append(afterDirective);
                        else if (i > 1) builder.append(afterDirectiveParameter);
                        builder.append(parameter.getText());
                    }
                    i++;
                }
                if (macro && line.directive.size() == 2) {
                    builder.append(afterDirectiveParameter).append("()");
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
                        builder.append(parameter.getIdentifier().replaceAll("\\s+", ""));
                    }
                    i++;
                }
            }

            if (line.macroCall != null) {
                int i = 0;

                var elements = line.macroCall.getElements()
                        .stream()
                        .sorted(Comparator.comparingInt(EditorIndexedElement::getStart))
                        .toList();
                for (var element : elements) {
                    if (element instanceof EditorElementMacroCallMnemonic mnemonic) {
                        builder.append(mnemonic.getText()).append(" (");
                    } else if (element instanceof EditorElementMacroCallParameter parameter) {
                        if (i > 1) builder.append(", ");
                        builder.append(parameter.getIdentifier());
                    }
                    i++;
                }
                builder.append(')');
            }

            if (line.comment != null) {
                if (line.directive != null || line.instruction != null || line.macroCall != null) {
                    builder.append(useTabs ? "\t" : "    ");
                }
                builder.append(line.comment.getIdentifier());
            }

            lineIndex++;
        }

        indexAll(builder.toString());
        return builder.toString();
    }

    private void calculateTabsBeforeLabel(EditorElementLabel label, StringBuilder builder,
                                          StringBuilder tabAccumulator, boolean tabsBeforeLabel,
                                          boolean useTabs) {
        var text = label.getText();
        var sub = text.substring(0, text.indexOf(label.getIdentifier()));

        if (useTabs) {
            int spaceCount = (int) sub.chars().filter(it -> it == ' ').count();
            sub = "\t".repeat(sub.length() - spaceCount + Math.round(spaceCount / 4.0f));
        } else {
            sub = sub.replace("\t", "    ");
        }

        if (tabsBeforeLabel) {
            builder.append(sub);
        } else {
            tabAccumulator.append(sub);
        }
    }

    private void calculateTabsAfterLabel(MIPSEditorLine line, StringBuilder builder,
                                         StringBuilder tabAccumulator, boolean useTabs) {
        int dataStart = 0;
        if (line.directive != null) dataStart = line.directive.getStart() - line.getStart();
        else if (line.instruction != null) dataStart = line.instruction.getStart() - line.getStart();
        else if (line.macroCall != null) dataStart = line.macroCall.getStart() - line.getStart();
        else if (line.comment != null) dataStart = line.comment.getStart() - line.getStart();
        if (dataStart == 0) return;

        int tabsStart = line.label == null ? 0 : line.label.getEnd() - line.getStart();
        var afterLabel = line.getText().substring(tabsStart, dataStart);

        if (useTabs) {
            int spaceCount = (int) afterLabel.chars().filter(it -> it == ' ').count();
            afterLabel = "\t".repeat(afterLabel.length() - spaceCount + Math.round(spaceCount / 4.0f));
        } else {
            afterLabel = afterLabel.replace("\t", "    ");
        }

        builder.append(tabAccumulator);
        builder.append(afterLabel);
    }
}
