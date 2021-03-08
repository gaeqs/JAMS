/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.gui.mips.editor.element;

import net.jamsimulator.jams.utils.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MIPSMacroCall extends MIPSCodeElement {

    private String name;
    private final List<MIPSMacroCallParameter> parameters;

    public MIPSMacroCall(MIPSLine line, int startIndex, int endIndex, int splitIndex, String text) {
        super(line, startIndex, endIndex, text);
        parameters = new ArrayList<>();
        parseText(splitIndex);
    }

    @Override
    public String getTranslatedNameNode() {
        return "MIPS_ELEMENT_MACRO_CALL";
    }

    @Override
    public String getSimpleText() {
        return name;
    }

    public List<MIPSMacroCallParameter> getParameters() {
        return parameters;
    }

    @Override
    public void move(int offset) {
        super.move(offset);
        parameters.forEach(parameter -> parameter.move(offset));
    }

    @Override
    public List<String> getStyles() {
        return getGeneralStyles("mips-macro-call");
    }

    @Override
    public void refreshMetadata(MIPSFileElements elements) {
    }


    private void parseText(int splitIndex) {
        name = text.substring(0, splitIndex).trim();

        var rawParameters = text.substring(splitIndex + 1);
        var parts = StringUtils.multiSplitIgnoreInsideStringWithIndex(rawParameters, false, " ", ",", "\t");
        var stringParameters = parts.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey)).collect(Collectors.toList());

        int index = 0;

        for (var entry : stringParameters) {
            var value = entry.getValue();
            int startOffset = 1;
            if (value.equals(")") || value.equals("(") || value.equals("()")) continue;
            if (value.endsWith(")")) value = value.substring(0, value.length() - 1);
            if (value.startsWith("(")) {
                value = value.substring(1);
                startOffset++;
            }

            parameters.add(new MIPSMacroCallParameter(
                    line,
                    this,
                    index++,
                    startIndex + entry.getKey() + splitIndex + startOffset,
                    startIndex + entry.getKey() + splitIndex + value.length() + startOffset,
                    value
            ));
        }

        startIndex += text.indexOf(name);
        endIndex = startIndex + name.length();
    }
}
