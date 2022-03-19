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
import net.jamsimulator.jams.utils.Validate;

import java.util.*;

/**
 * This class represents a file in an assembler.
 */
public class MIPS32AssemblerFile {

    private final MIPS32Assembler assembler;
    private final String name;
    private final String rawData;

    private final List<MIPS32AssemblerLine> lines = new ArrayList<>();
    private final List<String> globalIdentifiers = new ArrayList<>();
    private final AssemblerScope scope;

    private Macro definingMacro = null;
    private int macroCount = 0;

    /**
     * Creates a new assembler file.
     *
     * @param assembler the assembler of this file.
     * @param name      the name of the file.
     * @param rawData   the raw data of the file.
     */
    public MIPS32AssemblerFile(MIPS32Assembler assembler, String name, String rawData) {
        Validate.notNull(assembler, "Assembler cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(rawData, "Raw data cannot be null!");
        this.assembler = assembler;
        this.name = name;
        this.rawData = rawData;
        this.scope = new AssemblerScope(name, null, assembler.getGlobalScope());
    }

    /**
     * Returns the {@link MIPS32Assembler assembler} of this file.
     *
     * @return the {@link MIPS32Assembler assembler}.
     */
    public MIPS32Assembler getAssembler() {
        return assembler;
    }

    /**
     * Returns the name of this file.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link AssemblerScope scope} of this file.
     *
     * @return the {@link AssemblerScope scope}.
     */
    public AssemblerScope getScope() {
        return scope;
    }

    /**
     * Starts a {@link Macro macro} definition.
     *
     * @param line       the line starting the {@link Macro macro} definition.
     * @param name       the name of the {@link Macro macro}.
     * @param parameters the parameters of the {@link Macro macro}.
     * @throws AssemblerException when a {@link Macro macro} is already being defined.
     */
    public void startMacroDefinition(int line, String name, String[] parameters) {
        if (definingMacro != null) {
            throw new AssemblerException(line, "There's a macro already being defined!");
        }
        definingMacro = new Macro(name + "-" + parameters.length, name, parameters, name, line);
        macroCount = 1;
    }

    /**
     * Stops a {@link Macro macro} definition.
     *
     * @param line  the line finishing the {@link Macro macro} definition.
     * @param scope the {@link AssemblerScope scope} of the macro.
     * @throws AssemblerException when a {@link Macro macro} is not being defined.
     */
    public void stopMacroDefinition(int line, AssemblerScope scope) {
        if (definingMacro == null) {
            throw new AssemblerException(line, "There's no macro being defined!");
        }
        scope.addMacro(line, definingMacro);
        definingMacro = null;
        macroCount = 0;
    }

    /**
     * Returns whether a {@link Macro macro} is being defined.
     *
     * @return whether a {@link Macro macro} is being defined.
     */
    public boolean isMacroBeingDefined() {
        return definingMacro != null;
    }

    /**
     * Indicates that the given identifiers should be treated as global identifiers.
     * <p>
     * This method only have effect on the discovery step.
     *
     * @param identifiers the identifiers.
     */
    public void addGlobalIdentifiers(Collection<String> identifiers) {
        globalIdentifiers.addAll(identifiers);
    }

    /**
     * STEP 1 OF THE ASSEMBLER
     * <p>
     * Parsers the raw text of the file, discovering all the MIPS elements.
     * <p>
     * At the end, it adds the global {@link Label label}s and {@link Macro macro}s
     * into the global {@link AssemblerScope scope}.
     */
    public void discoverElements() {
        discoverElements(scope, StringUtils.multiSplit(rawData, "\n", "\r"), lines.size());
        for (String identifier : globalIdentifiers) {
            var macros = scope.getScopeMacros().values().stream()
                    .filter(it -> it.getOriginalName().equals(identifier)).toList();

            macros.forEach(it -> scope.getScopeMacros().remove(it.getName()));
            macros.forEach(it -> assembler.getGlobalScope().addMacro(it.getOriginLine(), it));

            var label = scope.getScopeLabels().remove(identifier);
            if (label != null) {
                assembler.getGlobalScope().addLabel(label.getOriginLine(), label);
            }
        }
    }

    /**
     * Discovers all the elements inside the given raw lines.
     * The lines are added to the lines list at the given start index.
     *
     * @param scope      the {@link AssemblerScope scope} of the discovery process.
     * @param rawLines   the raw lines.
     * @param startIndex the starting index.
     */
    public void discoverElements(AssemblerScope scope, List<String> rawLines, int startIndex) {
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

    /**
     * STEP 2 OF THE ASSEMBLER
     * <p>
     * Expands the macros calls and discovers the generated lines.
     */
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

                var childScope = new AssemblerScope(macro.get().getName(), macro.get(), scope);
                var lines = macro.get().getParsedLines(call.getParameters(), i);
                discoverElements(childScope, lines, i + 1);
            }
            if (line.getDirective() != null) {
                line.getDirective().runExpansion();
            }
        }
    }

    /**
     * STEP 3 OF THE ASSEMBLER
     * <p>
     * Assigns an address to all addresable lines.
     */
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
            throw new AssemblerException("Cannot assign an address to the following labels: "
                    + list.substring(1, list.length() - 1));
        }
    }

    /**
     * STEP 4 OF THE ASSEMBLER
     * <p>
     * Calculates the values of the instructions and compatible directives.
     */
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
