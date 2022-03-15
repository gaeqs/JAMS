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
import net.jamsimulator.jams.mips.directive.defaults.DirectiveMacro;
import net.jamsimulator.jams.mips.label.Label;
import net.jamsimulator.jams.utils.LabelUtils;
import net.jamsimulator.jams.utils.StringUtils;

import java.util.*;

public class MIPS32AssemblerFile {

    private final MIPS32Assembler assembler;
    private final String name;
    private final String rawData;

    private final List<MIPS32AssemblerLine> lines = new ArrayList<>();
    private final List<String> globalIdentifiers = new ArrayList<>();
    private final MIPS32AssemblerScope scope;

    private Macro definingMacro = null;
    private int macroCount = 0;

    public MIPS32AssemblerFile(MIPS32Assembler assembler, String name, String rawData) {
        this.assembler = assembler;
        this.name = name;
        this.rawData = rawData;
        this.scope = new MIPS32AssemblerScope(name, null, assembler.getGlobalScope());
    }

    public MIPS32Assembler getAssembler() {
        return assembler;
    }

    public String getName() {
        return name;
    }

    public MIPS32AssemblerScope getScope() {
        return scope;
    }

    public void startMacroDefinition(int line, String file, String name, String[] parameters) {
        if (definingMacro != null) {
            throw new AssemblerException(line, "There's a macro already being defined!");
        }
        definingMacro = new Macro(name, parameters, file, line);
        macroCount = 1;
    }

    public void stopMacroDefinition(int line, MIPS32AssemblerScope scope) {
        if (definingMacro == null) {
            throw new AssemblerException(line, "There's no macro being defined!");
        }
        scope.addMacro(line, definingMacro);
        definingMacro = null;
        macroCount = 0;
    }

    public boolean isMacroBeingDefined() {
        return definingMacro != null;
    }

    public void addGlobalIdentifier(String identifier) {
        globalIdentifiers.add(identifier);
    }

    public void addGlobalIdentifiers(Collection<String> identifiers) {
        globalIdentifiers.addAll(identifiers);
    }

    public void discoverElements() {
        discoverElements(scope, StringUtils.multiSplit(rawData, "\n", "\r"), lines.size());
        for (String identifier : globalIdentifiers) {
            var macro = scope.getScopeMacros().remove(identifier);
            if (macro != null) {
                assembler.getGlobalScope().addMacro(macro.getOriginLine(), macro);
            }
            var label = scope.getScopeLabels().remove(identifier);
            if (label != null) {
                assembler.getGlobalScope().addLabel(label.getOriginLine(), label);
            }
        }
    }

    public void discoverElements(MIPS32AssemblerScope scope, List<String> rawLines, int startIndex) {
        var equivalents = new HashMap<String, String>();
        for (String raw : rawLines) {
            raw = sanityLine(raw, equivalents);
            if (isMacroBeingDefined() && updateMacroCount(raw)) {
                definingMacro.addLine(raw);
            } else {
                if (isMacroBeingDefined()) stopMacroDefinition(lines.size(), scope);
                var line = new MIPS32AssemblerLine(this, scope, raw, lines.size());
                line.discover();
                if (line.getDirective() != null) {
                    line.getDirective().runDiscovery(equivalents);
                }
                lines.add(startIndex++, line);

                // Add labels
                line.getLabels().forEach(l -> scope.addLabel(line.getIndex(), l));
            }
        }

        if (definingMacro != null) {
            throw new AssemblerException(rawLines.size() + startIndex, "The .endmacro directive is missing!");
        }
    }

    public void expandMacros() {
        for (int i = 0; i < lines.size(); i++) {
            var line = lines.get(i);
            var scope = line.getScope();
            if (line.getMacroCall() != null) {
                var call = line.getMacroCall();
                var macro = scope.findMacro(call.getMnemonic());
                if (macro.isEmpty()) {
                    throw new AssemblerException(i, "Cannot find macro " + call.getMnemonic() +
                            " in scope " + scope.getName() + "!");
                }

                var childScope = new MIPS32AssemblerScope(macro.get().getName(), macro.get(), scope);
                var lines = macro.get().getParsedLines(call.getParameters(), i);
                discoverElements(childScope, lines, i + 1);
            }
            if (line.getDirective() != null) {
                line.getDirective().runExpansion();
            }
        }
    }

    public void assignAddresses() {
        var queue = new LinkedList<Label>();
        for (var line : lines) {
            OptionalInt address = OptionalInt.empty();
            queue.addAll(line.getLabels());
            if (line.getDirective() != null) {
                address = line.getDirective().runAddressAssignation();
            }
            if (line.getInstruction() != null) {
                var data = assembler.getAssemblerData();
                data.align(2);
                address = OptionalInt.of(data.getCurrent());
                data.addCurrent(line.getInstruction().getInstructionSize());
            }

            if (address.isPresent()) {
                int addr = address.getAsInt();
                line.setAddress(addr);
                assembler.getOriginals().put(addr, line.getRaw());

                while (!queue.isEmpty()) {
                    queue.pop().setAddress(addr);
                }
            }
        }
        if (!queue.isEmpty()) {
            String list = queue.stream().map(Label::getKey).toList().toString();
            throw new AssemblerException("Cannot assign addresses to the following labels: "
                    + list.substring(1, list.length() - 1));
        }
    }

    public void assignValues() {
        for (var line : lines) {
            if (line.getDirective() != null) {
                line.getDirective().runValueAssignation();
            }
            if (line.getInstruction() != null) {
                line.getInstruction().assemble(line);
            }
        }
    }

    private boolean updateMacroCount(String line) {
        int labelIndex = LabelUtils.getLabelFinishIndex(line);
        if (labelIndex != -1) {
            line = line.substring(labelIndex + 1).trim();
        }
        int index = StringUtils.indexOf(line, ' ', ',', '\t');
        String mnemonic;
        if (index == -1) {
            mnemonic = line.toLowerCase(Locale.ROOT);
        } else {
            mnemonic = line.substring(0, index).toLowerCase(Locale.ROOT);
        }

        if (mnemonic.equals("." + DirectiveEndmacro.NAME)) macroCount--;
        else if (mnemonic.equals("." + DirectiveMacro.NAME)) macroCount++;
        return macroCount > 0;
    }

    private static String sanityLine(String line, Map<String, String> equivalents) {
        line = StringUtils.removeComments(line).trim();
        for (Map.Entry<String, String> entry : equivalents.entrySet()) {
            line = line.replace(entry.getKey(), entry.getValue());
        }
        return line.trim();
    }

}
