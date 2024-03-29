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

package net.jamsimulator.jams.gui.editor.code.indexing.element.basic;

import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElementImpl;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.language.Messages;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a macro call.
 */
public class EditorElementMacroCall extends EditorIndexedParentElementImpl {

    public EditorElementMacroCall(EditorIndex index, ElementScope scope, EditorIndexedParentElement parent,
                                  int start, String text, int splitIndex) {
        super(index, scope, parent, start, text, Messages.ELEMENT_MACRO_CALL);
        parseText(splitIndex);
    }

    private void parseText(int splitIndex) {
        var rawParameters = text.substring(splitIndex + 1);
        var parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(rawParameters, false, " ", ",", "\t");
        var stringParameters = parts.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey)).toList();

        int parameterIndex = -1;
        for (var entry : stringParameters) {
            parameterIndex++;
            var value = entry.getValue();
            int startOffset = 1;

            if (parameterIndex == 0) {
                if (value.equals("(") || value.equals("()")) continue;
                if (value.startsWith("(")) {
                    value = value.substring(1);
                    startOffset++;
                }
            }

            if (parameterIndex == stringParameters.size() - 1) {
                if (value.equals(")")) continue;
                if (value.endsWith(")")) value = value.substring(0, value.length() - 1);
            }

            elements.add(new EditorElementMacroCallParameter(
                    index,
                    scope,
                    this,
                    start + entry.getKey() + splitIndex + startOffset,
                    value
            ));
        }
        parseName(text.substring(0, splitIndex).trim(), elements.size(),
                stringParameters.stream().map(Map.Entry::getValue).toList());
    }

    private void parseName(String name, int parameters, List<String> rawParameters) {
        var trimmed = name.trim();
        if (trimmed.isEmpty()) {
            elements.add(new EditorElementMacroCallMnemonic(index, scope, this, start, name, parameters, rawParameters));
            return;
        }
        var offset = name.indexOf(trimmed.charAt(0));
        elements.add(new EditorElementMacroCallMnemonic(index, scope, this, start + offset, trimmed, parameters, rawParameters));
    }
}
