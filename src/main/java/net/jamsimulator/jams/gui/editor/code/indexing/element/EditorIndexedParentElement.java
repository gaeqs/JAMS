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

import java.util.List;
import java.util.Optional;

/**
 * Represents a {@link EditorIndexedElement} that is the parent of other {@link EditorIndexedElement}s.
 */
public interface EditorIndexedParentElement extends EditorIndexedElement {

    /**
     * Returns the element that represents the given global position.
     *
     * @param position the position.
     * @return the element if present.
     */
    Optional<? extends EditorIndexedElement> getElementAt(int position);

    /**
     * Returns an immutable copy of the list with the elements inside this parent.
     *
     * @return the new immutable list.
     */
    List<EditorIndexedElement> getElements();

    /**
     * Returns the element located at the given index inside this parent.
     *
     * @param index the index.
     * @return the element.
     * @throws IndexOutOfBoundsException if the index is out of range.
     */
    EditorIndexedElement getElement(int index);

    /**
     * Returns the index of the given element inside this parent.
     * <p>
     * This method returns -1 if not found.
     *
     * @param element the element.
     * @return the index or -1 if not found.
     */
    int indexOf(EditorIndexedElement element);

    /**
     * Returns whether this parent has no children.
     *
     * @return whether this parent has no children.
     */
    boolean isEmpty();

    /**
     * Returns the amount of elements this parent has.
     *
     * @return the amount of elements this parent has.
     */
    int size();

}
