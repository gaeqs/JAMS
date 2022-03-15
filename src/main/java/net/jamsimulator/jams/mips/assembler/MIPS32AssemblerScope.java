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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MIPS32AssemblerScope {

    private final String name;
    private final MIPS32AssemblerScope parent;
    private final Map<String, Label> labels;
    private final Map<String, Macro> macros;
    private final Macro originMacro;

    public MIPS32AssemblerScope(String name, Macro originMacro, MIPS32AssemblerScope parent) {
        this.name = name;
        this.parent = parent;
        this.labels = new HashMap<>();
        this.macros = new HashMap<>();
        this.originMacro = originMacro;

        if (originMacro != null) {
            MIPS32AssemblerScope current = parent;
            while (current != null) {
                if (current.originMacro == originMacro) {
                    throw new AssemblerException(originMacro.getOriginLine(),
                            "Cyclic dependency found in macro '" + name + "' located at "
                            + originMacro.getOriginFile() + ":" + originMacro.getOriginLine() + ".");
                }
                current = current.parent;
            }
        }
    }

    public String getName() {
        return name;
    }

    public Optional<Macro> getOriginMacro() {
        return Optional.ofNullable(originMacro);
    }

    public Optional<MIPS32AssemblerScope> getParent() {
        return Optional.ofNullable(parent);
    }

    public Map<String, Label> getScopeLabels() {
        return labels;
    }

    public Map<String, Macro> getScopeMacros() {
        return macros;
    }

    public Optional<Label> findLabel(String identifier) {
        var label = labels.get(identifier);
        if (label != null) return Optional.of(label);
        return parent != null ? parent.findLabel(identifier) : Optional.empty();
    }

    public Optional<Macro> findMacro(String identifier) {
        var macro = macros.get(identifier);
        if (macro != null) return Optional.of(macro);
        return parent != null ? parent.findMacro(identifier) : Optional.empty();
    }

    public void addLabel(int line, Label label) {
        var definedLabel = labels.get(label.getKey());
        if (definedLabel != null) {
            throw new AssemblerException(line, "The label "
                    + label.getKey() + " is already defined in the scope " + name + ".");
        }
        labels.put(label.getKey(), label);
    }

    public void addMacro(int line, Macro macro) {
        var definedMacro = macros.get(macro.getName());
        if (definedMacro != null) {
            throw new AssemblerException(line, "The macro "
                    + macro.getName() + " is already defined in the scope " + name + ".");
        }
        macros.put(macro.getName(), macro);
    }

    public Optional<Label> nextLabelTo(int address) {
        Label label = null;
        for (Label current : labels.values()) {
            if (Integer.compareUnsigned(current.getAddress(), address) > 0) {
                if (label == null || Integer.compareUnsigned(label.getAddress(), current.getAddress()) > 0) {
                    label = current;
                }
            }
        }
        return Optional.ofNullable(label);
    }

    public Optional<Label> previousLabelTo(int address) {
        Label label = null;
        for (Label current : labels.values()) {
            if (Integer.compareUnsigned(current.getAddress(), address) < 0) {
                if (label == null || Integer.compareUnsigned(label.getAddress(), current.getAddress()) < 0) {
                    label = current;
                }
            }
        }
        return Optional.ofNullable(label);
    }

    public Optional<Label> resolveLabel(int address, String identifier) {
        return switch (identifier) {
            case "+" -> nextLabelTo(address);
            case "-" -> previousLabelTo(address);
            default -> findLabel(identifier);
        };
    }
}
