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

/**
 * Represents a reference to an element.
 *
 * @param <R>            the referenced element's type.
 * @param referencedType the referenced element's class.
 * @param identifier     the identifier of the referenced element.
 */
public record EditorElementReference<R extends EditorReferencedElement>(
        Class<R> referencedType, String identifier
) {

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

}
