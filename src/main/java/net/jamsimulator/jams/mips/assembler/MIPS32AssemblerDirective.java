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

import net.jamsimulator.jams.mips.directive.Directive;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.Map;
import java.util.OptionalInt;

/**
 * This class represents a directive in an assembler.
 */
public class MIPS32AssemblerDirective {

    private final MIPS32AssemblerLine line;
    private final String mnemonic;
    private final String rawParameters;
    private final String[] parameters;

    private final Directive directive;

    MIPS32AssemblerDirective(MIPS32AssemblerLine line, String mnemonic, String rawParameters) {
        this.line = line;
        this.mnemonic = mnemonic;
        this.rawParameters = rawParameters;
        this.parameters = StringUtils.multiSplitIgnoreInsideString(rawParameters, false, " ", ",", "\t")
                .toArray(new String[0]);

        var set = line.getAssembler().getDirectiveSet();
        directive = set.getDirective(mnemonic).orElse(null);
        if (directive == null) {
            line.getAssembler().printWarning("Directive " + mnemonic + " not found!");
        }
    }

    public MIPS32AssemblerLine getLine() {
        return line;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public String getRawParameters() {
        return rawParameters;
    }

    public String[] getParameters() {
        return parameters;
    }

    public Directive getDirective() {
        return directive;
    }

    public void runDiscovery(Map<String, String> equivalents) {
        directive.onDiscovery(line, parameters, rawParameters, equivalents);
    }

    public void runExpansion() {
        directive.onExpansion(line, parameters, rawParameters);
    }

    public OptionalInt runAddressAssignation() {
        return directive.onAddressAssignation(line, parameters, rawParameters);
    }

    public void runValueAssignation() {
        directive.onValueAssignation(line, parameters, rawParameters);
    }
}
