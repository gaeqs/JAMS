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

package net.jamsimulator.jams.gui.editor.code.indexing.element.reference;

import java.util.Objects;

/**
 * Represents a reference to an element.
 */
public class EditorElementReference<R extends EditorReferencedElement> {
    private final Class<R> referencedType;
    private final String identifier;

    /**
     * @param referencedType the referenced element's class.
     * @param identifier     the identifier of the referenced element.
     */
    public EditorElementReference(
            Class<R> referencedType, String identifier
    ) {
        this.referencedType = referencedType;
        this.identifier = identifier;
    }

    /**
     * Represents if this reference can hold the given reference.
     * <p>
     * This means that the given reference has the same identifier
     * and this referenced type is a supertype of the given reference's referenced type.
     *
     * @param potentialChild the reference to check.
     * @return whether the given reference is a child of this reference.
     */
    public boolean isChild(EditorElementReference<?> potentialChild) {
        return referencedType.isAssignableFrom(potentialChild.referencedType)
                && identifier.equals(potentialChild.identifier);
    }

    /**
     * Returns the type of the element this reference is pointing.
     *
     * @return the type of the element.
     */
    public Class<R> getReferencedType() {
        return referencedType;
    }

    /**
     * Returns the identifier of the element this reference is pointing.
     *
     * @return the identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (EditorElementReference) obj;
        return Objects.equals(this.referencedType, that.referencedType) &&
                Objects.equals(this.identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(referencedType, identifier);
    }

    @Override
    public String toString() {
        return "EditorElementReference[" +
                "referencedType=" + referencedType + ", " +
                "identifier=" + identifier + ']';
    }


}
