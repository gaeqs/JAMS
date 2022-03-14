/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.mips.assembler;

import net.jamsimulator.jams.mips.assembler.exception.AssemblerException;
import net.jamsimulator.jams.mips.label.Label;
import net.jamsimulator.jams.utils.LabelUtils;
import net.jamsimulator.jams.utils.StringUtils;

public class MIPS32AssemblerLine {

    private final MIPS32AssemblerFile file;
    private final String raw;
    private final int index;
    private final String macroSuffix;

    private Label label;
    private MIPS32AssemblerInstruction instruction;
    private MIPS32AssemblerDirective directive;
    private MIPS32AssemblerMacroCall macroCall;

    private int address = 0;

    public MIPS32AssemblerLine(MIPS32AssemblerFile file, String raw, int index, String macroSuffix) {
        this.file = file;
        this.raw = raw;
        this.index = index;
        this.macroSuffix = macroSuffix;
    }

    public MIPS32AssemblerFile getFile() {
        return file;
    }

    public MIPS32Assembler getAssembler() {
        return file.getAssembler();
    }

    public String getRaw() {
        return raw;
    }

    public int getIndex() {
        return index;
    }

    public boolean isLineFromMacroCall() {
        return !macroSuffix.isEmpty();
    }

    public String getMacroSuffix() {
        return macroSuffix;
    }

    public Label getLabel() {
        return label;
    }

    public MIPS32AssemblerInstruction getInstruction() {
        return instruction;
    }

    public MIPS32AssemblerDirective getDirective() {
        return directive;
    }

    public MIPS32AssemblerMacroCall getMacroCall() {
        return macroCall;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void discover() {
        var line = raw;
        int labelIndex = LabelUtils.getLabelFinishIndex(line);
        if (labelIndex != -1) {
            var rawLabel = line.substring(0, labelIndex).trim();
            if (!LabelUtils.isLabelLegal(rawLabel)) {
                throw new AssemblerException(index, "The label " + rawLabel + " is illegal.");
            }
            line = line.substring(labelIndex + 1).trim();
            label = new Label(rawLabel, 0, file.getName(), index, false);
        }

        if (line.isEmpty()) return;

        var mnemonicIndex = StringUtils.indexOf(line, ',', ' ', '\t');
        var mnemonic = mnemonicIndex == -1 ? line : line.substring(0, mnemonicIndex);
        var parameters = mnemonicIndex == -1 ? "" : line.substring(mnemonicIndex + 1).trim();

        // DIRECTIVE
        if (mnemonic.charAt(0) == '.') {
            directive = new MIPS32AssemblerDirective(this, mnemonic.substring(1), parameters);
            if (directive.getDirective() == null) {
                directive = null;
            }
        } else {
            if (parameters.startsWith("(") && parameters.endsWith("(")) {
                macroCall = new MIPS32AssemblerMacroCall(this, mnemonic, parameters.substring(1, parameters.length() - 1));
            } else {
                instruction = new MIPS32AssemblerInstruction(this, mnemonic, parameters);
            }
        }
    }
}