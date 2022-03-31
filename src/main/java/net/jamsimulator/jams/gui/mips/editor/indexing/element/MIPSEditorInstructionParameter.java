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

package net.jamsimulator.jams.gui.mips.editor.indexing.element;

import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElementImpl;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.mips.parameter.ParameterPartType;
import net.jamsimulator.jams.mips.parameter.ParameterType;
import net.jamsimulator.jams.project.mips.MIPSProject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MIPSEditorInstructionParameter extends EditorIndexedParentElementImpl {

    public MIPSEditorInstructionParameter(EditorIndex index, ElementScope scope, EditorIndexedParentElement parent,
                                          int start, String text, ParameterType hint) {
        super(index, scope, parent, start, text, Messages.MIPS_ELEMENT_INSTRUCTION);
        if (hint != null) parseWithHint(hint);
        else parseWithoutHint();
    }


    protected void parseWithHint(ParameterType hint) {
        int[] indices = hint.split(text);
        int amount = hint.getAmountOfParts();

        for (int i = 0; i < amount; i++) {
            int partStart = start + indices[i << 1];
            int partEnd = partStart + indices[(i << 1) + 1];
            if (partStart < partEnd) {
                // add part
                elements.add(new MIPSEditorInstructionParameterPart(
                        index,
                        scope,
                        this,
                        partStart,
                        text.substring(partStart - start, partEnd - start),
                        hint.getPart(i)
                ));
            }
        }
    }

    protected void parseWithoutHint() {
        var builder = new StringBuilder();
        int index = 0;
        int start = this.start;
        for (char c : text.toCharArray()) {
            if (c == '+') {
                parsePartWithoutHint(builder.toString(), start);
                builder = new StringBuilder();
                start = index + 1;
            } else {
                builder.append(c);
            }
            index++;
        }
        parsePartWithoutHint(builder.toString(), start);
    }

    protected void parsePartWithoutHint(String part, int start) {
        if (!(index.getProject() instanceof MIPSProject project)) {
            elements.add(new MIPSEditorInstructionParameterPart(index, scope, this, start, part, null));
            return;
        }

        if (part.indexOf('(') != -1 || part.indexOf(')') != -1) {
            var types =
                    ParameterType.getCompatibleParameterTypes(part, project.getData().getRegistersBuilder());
            if (types.size() == 1 && types.get(0) == ParameterType.LABEL) {
                elements.add(new MIPSEditorInstructionParameterPart(index, scope, this,
                        start, part, ParameterPartType.LABEL));
                return;
            }
        }

        var parts = new HashMap<Integer, String>();

        int lastAddition = part.lastIndexOf('+');
        if (lastAddition >= 0) {
            parts.put(0, part.substring(0, lastAddition));
            part = part.substring(lastAddition + 1);
        }

        int lastParenthesis = part.lastIndexOf('(');
        if (lastParenthesis >= 0 && lastParenthesis < part.lastIndexOf(')')) {
            parts.put(lastAddition + 1, part.substring(0, lastParenthesis));
            part = part.substring(lastParenthesis + 1, part.lastIndexOf(')'));
        }

        if (!part.isEmpty()) {
            parts.put(lastAddition + lastParenthesis + 2, part);
        }

        var sorted = new ArrayList<>(parts.entrySet());
        sorted.sort(Comparator.comparingInt(Map.Entry::getKey));

        for (var entry : sorted) {
            elements.add(new MIPSEditorInstructionParameterPart(
                    index,
                    scope,
                    this,
                    start + entry.getKey(),
                    entry.getValue(),
                    null));
        }
    }
}
