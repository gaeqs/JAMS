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

import java.util.Map;

public class MIPS32AssemblerLine {

    private final MIPS32AssemblerFile file;
    private final String raw;
    private final int index;

    private Label label;
    private MIPS32AssemblerInstruction instruction;
    private MIPS32AssemblerDirective directive;
    private MIPS32AssemblerMacro macro;

    public MIPS32AssemblerLine(MIPS32AssemblerFile file, String raw, int index) {
        this.file = file;
        this.raw = raw;
        this.index = index;
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

    public Label getLabel() {
        return label;
    }

    public MIPS32AssemblerInstruction getInstruction() {
        return instruction;
    }

    public MIPS32AssemblerDirective getDirective() {
        return directive;
    }

    public MIPS32AssemblerMacro getMacro() {
        return macro;
    }

    public void discover(Map<String, String> equivalents) {
        var line = sanityLine(raw, equivalents);

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
        } else {
            if (parameters.startsWith("(")) {
                macro = new MIPS32AssemblerMacro(this, mnemonic, parameters.substring(1, parameters.length() - 1));
            } else {
                instruction = new MIPS32AssemblerInstruction(this, mnemonic, parameters);
            }
        }
    }

    private String sanityLine(String line, Map<String, String> equivalents) {
        line = StringUtils.removeComments(line).trim();
        for (Map.Entry<String, String> entry : equivalents.entrySet()) {
            line = line.replace(entry.getKey(), entry.getValue());
        }
        return line.trim();
    }
}