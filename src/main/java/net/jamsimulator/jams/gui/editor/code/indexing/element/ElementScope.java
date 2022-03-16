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
public record ElementScope(String macroIdentifier, ElementScope parent, UUID scopeId) {

    public static final ElementScope INTERNAL = new ElementScope();
    public static final ElementScope GLOBAL = new ElementScope();

    public ElementScope() {
        this("", null, UUID.randomUUID());
    }

    public ElementScope(String macroIdentifier, ElementScope parent) {
        this(macroIdentifier, parent, UUID.randomUUID());
    }

    public ElementScope(ElementScope parent) {
        this("", parent, UUID.randomUUID());
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

}
