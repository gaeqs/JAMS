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

package net.jamsimulator.jams.gui.mips.editor.element;

import java.util.List;

public class MIPSMacroCallParameter extends MIPSCodeElement {

    protected final MIPSMacroCall macro;
    protected final int index;

    public MIPSMacroCallParameter(MIPSLine line, MIPSMacroCall macro, int index, int startIndex, int endIndex, String text) {
        super(line, startIndex, endIndex, text);
        this.macro = macro;
        this.index = index;
    }

    @Override
    public String getTranslatedNameNode() {
        return "MIPS_ELEMENT_MACRO_CALL_PARAMETER";
    }

    public int getIndex() {
        return index;
    }

    public MIPSMacroCall getMacro() {
        return macro;
    }

    @Override
    public String getSimpleText() {
        return text;
    }

    @Override
    public List<String> getStyles() {
        String style = "mips-macro-call-parameter";
        return getGeneralStyles(style);
    }

    @Override
    public void refreshMetadata(MIPSFileElements elements) {
    }
}
