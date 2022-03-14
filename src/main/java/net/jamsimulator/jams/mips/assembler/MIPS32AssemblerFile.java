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
import net.jamsimulator.jams.mips.directive.defaults.DirectiveEndmacro;
import net.jamsimulator.jams.mips.label.Label;
import net.jamsimulator.jams.utils.LabelUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.*;

public class MIPS32AssemblerFile {

    private final MIPS32Assembler assembler;
    private final String name;
    private final String rawData;

    private final List<MIPS32AssemblerLine> lines = new ArrayList<>();

    private Macro definingMacro = null;

    public MIPS32AssemblerFile(MIPS32Assembler assembler, String name, String rawData) {
        this.assembler = assembler;
        this.name = name;
        this.rawData = rawData;
    }

    public MIPS32Assembler getAssembler() {
        return assembler;
    }

    public String getName() {
        return name;
    }

    public Optional<Label> getLocalLabel(String identifier) {
    }

    public Optional<Macro> getLocalMacro(String identifier) {
    }

    public void startMacroDefinition(int line, String file, String name, String[] parameters) {
        if (definingMacro != null) {
            throw new AssemblerException(line, "There's a macro already being defined!");
        }
        definingMacro = new Macro(name, parameters, file, line);
    }

    public void stopMacroDefinition(int line) {
        if (definingMacro != null) {
            throw new AssemblerException(line, "There's no macro being defined!");
        }
    }

    public boolean isMacroBeingDefined() {
        return definingMacro != null;
    }

    public void discoverElements() {
        var equivalents = new HashMap<String, String>();
        for (String raw : StringUtils.multiSplit(rawData, "\n", "\r")) {
            raw = sanityLine(raw, equivalents);
            if (isMacroBeingDefined() && !hasEndMacroDirective(raw)) {
                definingMacro.addLine(raw);
            } else {
                if (isMacroBeingDefined()) stopMacroDefinition(lines.size());
                var line = new MIPS32AssemblerLine(this, raw, lines.size(), "");
                line.discover();
                if (line.getDirective() != null) {
                    line.getDirective().runDiscovery(equivalents);
                }
                lines.add(line);
            }
        }
    }

    private static boolean hasEndMacroDirective(String line) {
        int labelIndex = LabelUtils.getLabelFinishIndex(line);
        if (labelIndex != -1) {
            line = line.substring(labelIndex + 1).trim();
        }
        int index = StringUtils.indexOf(line, ' ', ',', '\t');
        if (index == -1) {
            return line.toLowerCase(Locale.ROOT).equals("." + DirectiveEndmacro.NAME);
        } else {
            return line.substring(index).toLowerCase(Locale.ROOT).equals("." + DirectiveEndmacro.NAME);
        }
    }

    private static String sanityLine(String line, Map<String, String> equivalents) {
        line = StringUtils.removeComments(line).trim();
        for (Map.Entry<String, String> entry : equivalents.entrySet()) {
            line = line.replace(entry.getKey(), entry.getValue());
        }
        return line.trim();
    }

    public void expandMacros() {
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            if (line.getMacroCall() != null) {

            }
            if (line.getDirective() != null) {
                line.getDirective().runExpansion();
            }
        }
    }

}
