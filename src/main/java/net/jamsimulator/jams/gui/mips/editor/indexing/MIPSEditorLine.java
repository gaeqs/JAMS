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

import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.*;
import net.jamsimulator.jams.gui.editor.code.indexing.element.line.EditorIndexedLine;
import net.jamsimulator.jams.gui.mips.editor.indexing.element.MIPSEditorDirective;
import net.jamsimulator.jams.gui.mips.editor.indexing.element.MIPSEditorInstruction;
import net.jamsimulator.jams.mips.directive.defaults.DirectiveEndmacro;
import net.jamsimulator.jams.mips.directive.defaults.DirectiveMacro;
import net.jamsimulator.jams.utils.LabelUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Optional;

public class MIPSEditorLine extends EditorIndexedLine {

    protected EditorElementComment comment;
    protected EditorElementLabel label;
    protected EditorElementMacroCall macroCall;

    protected MIPSEditorInstruction instruction;
    protected MIPSEditorDirective directive;

    public MIPSEditorLine(EditorIndex index, ElementScope scope, int start, int number, String text) {
        super(index, scope, start, number, text);
        parseLine();
    }

    public Optional<EditorElementComment> getComment() {
        return Optional.ofNullable(comment);
    }

    public Optional<EditorElementLabel> getLabel() {
        return Optional.ofNullable(label);
    }

    public Optional<EditorElementMacroCall> getMacroCall() {
        return Optional.ofNullable(macroCall);
    }

    public Optional<MIPSEditorInstruction> getInstruction() {
        return Optional.ofNullable(instruction);
    }

    public Optional<MIPSEditorDirective> getDirective() {
        return Optional.ofNullable(directive);
    }

    @Override
    public boolean isMacroStart() {
        return directive != null && directive.getDirective().orElse(null) instanceof DirectiveMacro;
    }

    @Override
    public boolean isMacroEnd() {
        return directive != null && directive.getDirective().orElse(null) instanceof DirectiveEndmacro;
    }

    @Override
    public Optional<ElementScope> getDefinedMacroScope() {
        if (!isMacroStart() || directive.size() < 2) return Optional.empty();
        return Optional.ofNullable(((EditorElementMacro) directive.getElement(1)).getMacroScope());
    }

    protected void parseLine() {
        String parsing = text;
        int pStart = start;

        //COMMENT
        int commentIndex = StringUtils.getCommentIndex(parsing);
        if (commentIndex != -1) {
            comment = new EditorElementComment(index, scope, this, pStart + commentIndex, parsing.substring(commentIndex));
            elements.add(comment);
            parsing = parsing.substring(0, commentIndex);
        }

        //LABEL
        int labelIndex = LabelUtils.getLabelFinishIndex(parsing);
        if (labelIndex != -1) {
            var labelText = parsing.substring(0, labelIndex + 1).trim();
            var labelOffset = labelText.isEmpty() ? 0 : parsing.indexOf(labelText.charAt(0));
            label = new EditorElementLabelImpl(index, scope, this, pStart + labelOffset, labelText);
            elements.add(label);
            pStart = pStart + labelIndex + 1;
            parsing = parsing.substring(labelIndex + 1);
        }

        //INSTRUCTION / DIRECTIVE / MACRO
        String trim = parsing.trim();
        if (trim.isEmpty()) return;
        pStart += parsing.indexOf(trim.charAt(0));
        if (trim.charAt(0) == '.') {
            directive = new MIPSEditorDirective(index, scope, this, pStart, trim);
            elements.add(directive);
        } else {
            int spaceIndex = trim.indexOf(" ");
            int commaIndex = trim.indexOf(",");
            int tabIndex = trim.indexOf("\t");

            int split = Math.min(spaceIndex == -1 ? Integer.MAX_VALUE : spaceIndex,
                    Math.min(commaIndex == -1 ? Integer.MAX_VALUE : commaIndex,
                            tabIndex == -1 ? Integer.MAX_VALUE : tabIndex));

            if (split != Integer.MAX_VALUE && trim.substring(split + 1).trim().startsWith("(")) {
                macroCall = new EditorElementMacroCall(index, scope, this, pStart, trim, split);
                elements.add(macroCall);
            } else {
                instruction = new MIPSEditorInstruction(index, scope, this, pStart, trim);
                elements.add(instruction);
            }
        }
    }
}
