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

package net.jamsimulator.jams.mips.assembler.old;

import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Optional;

public class DirectiveSnapshot {

    public final int line;
    private final String raw;
    private final String labelSufix;

    public int address;

    private String directiveName;

    private Directive directive;
    private String[] parameters;

    public DirectiveSnapshot(int line, int address, String raw, String labelSufix) {
        this.raw = raw;
        this.line = line;
        this.address = address;
        this.labelSufix = labelSufix;
    }

    public void scan(MIPS32Assembler assembler) {
        decode();
        scanDirective(assembler);
    }

    public int executeNonLabelRequiredSteps(MIPS32AssemblingFile file, int labelAddress) {
        if (directive != null) {
            int result = address = directive.execute(line, raw, parameters, labelSufix, file);
            if (result == -1) address = labelAddress;
            return result;
        }
        return -1;
    }

    public void executeLabelRequiredSteps(MIPS32AssemblingFile file) {
        if (directive != null) {
            directive.postExecute(parameters, file, line, address, labelSufix);
        }
    }

    private void decode() {
        int mnemonicIndex = raw.indexOf(' ');
        int tabIndex = raw.indexOf("\t");
        if (mnemonicIndex == -1) mnemonicIndex = tabIndex;
        else if (tabIndex != -1) mnemonicIndex = Math.min(mnemonicIndex, tabIndex);

        if (mnemonicIndex == -1) {
            directiveName = raw.substring(1);
            parameters = new String[0];
            return;
        }

        directiveName = raw.substring(1, mnemonicIndex);
        String raw = this.raw.substring(mnemonicIndex + 1);
        parameters = StringUtils.multiSplitIgnoreInsideString(raw, false, " ", ",", "\t")
                .toArray(new String[0]);
    }

    private void scanDirective(MIPS32Assembler assembler) {
        Optional<Directive> optional = assembler.getDirectiveSet().getDirective(directiveName);
        if (optional.isEmpty()) {
            assembler.getLog().printWarningLn("Directive " + directiveName + " not found!");
            return;
        }
        directive = optional.get();
    }

}
