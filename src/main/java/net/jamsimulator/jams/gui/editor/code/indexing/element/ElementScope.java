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

package net.jamsimulator.jams.gui.editor.code.indexing.element;

import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacro;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents the scope of an element.
 * <p>
 * An element with a GLOBAL scope can be referenced from all places inside the project.
 * <p>
 * An element with a FILE scope can be referenced from all places inside its file.
 * <p>
 * An element with a MACRO scope can only be reference from inside its macro.
 */
public record ElementScope(EditorElementMacro macro, ElementScope parent, String name, UUID scopeId) {

    public static final ElementScope INTERNAL = new ElementScope("Internal");
    public static final ElementScope GLOBAL = new ElementScope("Global");

    public ElementScope(String name) {
        this(null, null, name, UUID.randomUUID());
    }

    public ElementScope(EditorElementMacro macro, ElementScope parent, String name) {
        this(macro, parent, name, UUID.randomUUID());
    }

    public ElementScope(ElementScope parent, String name) {
        this(null, parent, name, UUID.randomUUID());
    }

    public String macroIdentifier() {
        return macro == null ? "" : macro.getIdentifier();
    }

    /**
     * Checks if this scope can be reached from the given scope.
     * <p>
     *
     * @param scope the scope that wants to reach.
     * @return whether this scope can be reached.
     */
    public boolean canBeReachedFrom(ElementScope scope) {
        if (scope.equals(INTERNAL)) return true;
        var current = scope;
        while (current != null) {
            if (equals(current)) return true;
            current = current.parent;
        }
        return false;
    }

    /**
     * Returns the difference between this scope's level and the given scope's level.
     * <p>
     * This method returns {@link Integer#MAX_VALUE} if the given scope is not a child
     * of this scope or this scope itself.
     *
     * @param scope the given scope.
     * @return the difference.
     */
    public int getScopeLayersDifference(ElementScope scope) {
        if (scope.equals(INTERNAL)) return Integer.MAX_VALUE;
        var current = scope;
        int count = 0;
        while (current != null) {
            if (equals(current)) return count;
            current = current.parent;
            count++;
        }
        return Integer.MAX_VALUE;
    }

    /**
     * Returns the human name of this scope.
     * <p>
     * This name doesn't affect any behavior.
     *
     * @return the human name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the full human name of this scope.
     * <p>
     * The full name is the concatenaiton of the parent's name with this scope's name.
     *
     * @return the full name.
     */
    public String getFullName() {
        if (parent == null) return getName();
        return parent.getFullName() + " > " + getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementScope that = (ElementScope) o;
        return scopeId.equals(that.scopeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scopeId);
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
