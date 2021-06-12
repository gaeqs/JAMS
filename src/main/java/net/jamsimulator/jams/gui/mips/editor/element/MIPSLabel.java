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

public class MIPSLabel extends MIPSCodeElement {

    private boolean global;

    public MIPSLabel(MIPSLine line, int startIndex, int endIndex, String text) {
        super(line, startIndex, endIndex, text);
        global = false;
        registerLabel(getLabel(), false);
    }

    @Override
    public String getTranslatedNameNode() {
        return global ? "MIPS_ELEMENT_GLOBAL_LABEL" : "MIPS_ELEMENT_LABEL";
    }

    @Override
    public String getSimpleText() {
        return text;
    }

    public String getLabel() {
        return text.substring(0, text.length() - 1).trim();
    }

    public boolean isGlobal() {
        return global;
    }

    @Override
    public List<String> getStyles() {
        return getGeneralStyles(global ? "mips-global-label" : "mips-label");
    }

    @Override
    public void refreshMetadata(MIPSFileElements elements) {
        String label = getLabel();
        global = elements.getSetAsGlobalLabel().contains(label);
    }

}
