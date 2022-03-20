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
import net.jamsimulator.jams.utils.Validate;

import java.util.*;

/**
 * Represents a scope in an assembler.
 * <p>
 * Scopes store the {@link Label label}s and {@link Macro macro}s in the scope.
 * <p>
 * Scopes search labels and macros on itself.
 * If the label or macro couldn't been found, it is seached in the parent scope.
 */
public class AssemblerScope {

    private final String name;
    private final AssemblerScope parent;
    private final Map<String, Label> labels;
    private final Map<String, Macro> macros;
    private final Macro originMacro;

    private final Set<AssemblerScope> children;

    /**
     * Creates a new scope.
     *
     * @param name        the name of the scope.
     * @param originMacro the macro that creates this scope. It can be null.
     * @param parent      the parent scope. It can be null.
     * @throws AssemblerException when this scope creates a cyclic macro dependency.
     */
    public AssemblerScope(String name, Macro originMacro, AssemblerScope parent) {
        Validate.notNull(name, "Name cannot be null!");
        this.name = name;
        this.parent = parent;
        this.labels = new HashMap<>();
        this.macros = new HashMap<>();
        this.originMacro = originMacro;

        this.children = new HashSet<>();

        if (originMacro != null) {
            AssemblerScope current = parent;
            while (current != null) {
                if (current.originMacro == originMacro) {
                    throw new AssemblerException(originMacro.getOriginLine(),
                            "Cyclic dependency found in macro '" + name + "' located at "
                                    + originMacro.getOriginFile() + ":" + originMacro.getOriginLine() + ".");
                }
                current = current.parent;
            }
        }

        if (parent != null) {
            parent.children.add(this);
        }
    }

    /**
     * Returns the name of this scope.
     *
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the {@link Macro macro} that generated this scope, if present.
     *
     * @return the {@link Macro macro}.
     */
    public Optional<Macro> getOriginMacro() {
        return Optional.ofNullable(originMacro);
    }

    /**
     * Returns the parent of this scope.
     *
     * @return the parent.
     */
    public Optional<AssemblerScope> getParent() {
        return Optional.ofNullable(parent);
    }

    /**
     * Returns the {@link Label label}s in this scope.
     * <p>
     * This {@link Map} is modifiable.
     * The key is the name of the {@link Label label}.
     *
     * @return the {@link Label label}s.
     */
    public Set<AssemblerScope> getChildren() {
        return Collections.unmodifiableSet(children);
    }

    public Map<String, Label> getScopeLabels() {
        return labels;
    }

    /**
     * Returns the {@link Macro macro}s in this scope.
     * <p>
     * This {@link Map} is modifiable.
     * The key is the name of the {@link Macro macro}.
     *
     * @return the {@link Macro macro}s.
     */
    public Map<String, Macro> getScopeMacros() {
        return macros;
    }

    /**
     * Finds a {@link Label label} that matches the given identifier.
     * <p>
     * If this scope doesn't contain the {@link Label label}, it is searched in the parent scope.
     * <p>
     * This method doesn't resolve special idenfiers. Use {@link #resolveLabel(int, String)} if you
     * want to resolve special identifiers.
     *
     * @param identifier the identifier.
     * @return the {@link Label label}, if present.
     */
    public Optional<Label> findLabel(String identifier) {
        var label = labels.get(identifier);
        if (label != null) return Optional.of(label);
        return parent != null ? parent.findLabel(identifier) : Optional.empty();
    }

    /**
     * Finds a {@link Macro macro} that matches the given identifier.
     * <p>
     * If this scope doesn't contain the {@link Macro macro}, it is searched in the parent scope.
     *
     * @param identifier the identifier.
     * @return the {@link Macro macro}, if present.
     */
    public Optional<Macro> findMacro(String identifier) {
        var macro = macros.get(identifier);
        if (macro != null) return Optional.of(macro);
        return parent != null ? parent.findMacro(identifier) : Optional.empty();
    }

    /**
     * Adds a {@link Label label} to this scope.
     *
     * @param line  the line where this method is executed.
     * @param label the {@link Label label}.
     * @throws AssemblerException when a {@link Label label} with the same identifier is already defined in this scope.
     */
    public void addLabel(int line, Label label) {
        var definedLabel = labels.get(label.getKey());
        if (definedLabel != null) {
            throw new AssemblerException(line, "The label "
                    + label.getKey() + " is already defined in the scope " + name + ".");
        }
        labels.put(label.getKey(), label);
    }

    /**
     * Adds a {@link Macro macro} to this scope.
     *
     * @param line  the line where this method is executed.
     * @param macro the {@link Macro macro}.
     * @throws AssemblerException when a {@link Macro macro} with the same identifier is already defined in this scope.
     */
    public void addMacro(int line, Macro macro) {
        var definedMacro = macros.get(macro.getName());
        if (definedMacro != null) {
            throw new AssemblerException(line, "The macro "
                    + macro.getName() + " is already defined in the scope " + name + ".");
        }
        macros.put(macro.getName(), macro);
    }

    /**
     * Returns the first {@link Label label} present after the given address.
     * <p>
     * This method is invoked to resolve the relative {@link Label label} '+'.
     *
     * @param address the address.
     * @return the {@link Label label}, if present.
     */
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

    /**
     * Returns the first {@link Label label} present before the given address.
     * <p>
     * This method is invoked to resolve the relative {@link Label label} '+'.
     *
     * @param address the address.
     * @return the {@link Label label}, if present.
     */
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

    /**
     * Returns the {@link Label label} that resolves the given identifier.
     * <p>
     * Unlike {@link #findLabel(String)}, this method resolves special idenfiers, such as '-' and '+'.
     *
     * @param address    the address of the line trying to resolve the idenfier. Used to resolver relative identifiers.
     * @param identifier the identifier to resolve.
     * @return the {@link Label label} if present.
     */
    public Optional<Label> resolveLabel(int address, String identifier) {
        return switch (identifier) {
            case "+" -> nextLabelTo(address);
            case "-" -> previousLabelTo(address);
            default -> findLabel(identifier);
        };
    }
}
