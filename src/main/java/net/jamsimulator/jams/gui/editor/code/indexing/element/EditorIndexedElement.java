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

import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.element.metadata.Metadata;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Represents an element inside a {@link EditorIndex}.
 * <p>
 * Elements occupy space inside the {@link EditorIndex}: they have a start index and a length.
 * <p>
 * Elements must be immutable! The only parameter that may change are the position, the metadata and the scope.
 * Indices must provide a new element and invalidate this one when this element is modifiedd
 * <p>
 * A basic implementation can be found at {@link EditorIndexedElementImpl}.
 * Look at {@link EditorIndexedParentElement}, {@link EditorIndexStyleableElement},
 * {@link net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencedElement EditorReferencedElement}
 * and {@link net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencingElement EditorReferencingElement}
 * for advanced uses.
 */
public interface EditorIndexedElement extends Comparable<EditorIndexedElement> {

    /**
     * Returns the {@link EditorIndex} this element is located at.
     *
     * @return the {@link EditorIndex}.
     */
    EditorIndex getIndex();

    /**
     * Returns the identifier of the element. The identifier is a string that represents the element.
     * It doesn't include type operators such as labels' ":" or directives' ".".
     * <p>
     * Examples:
     * <p>
     * For a label "xyz:" the identifier will be "xyz".
     * <p>
     * For a directive ".xyz" the identifier will be "xyz.
     *
     * @return the identifier.
     */
    String getIdentifier();

    /**
     * Returns the start position of this element inside the whole {@link EditorIndex}.
     * This index is inclusive.
     *
     * @return the start position as a global view.
     */
    int getStart();

    /**
     * Returns the end position of this element inside the whole {@link EditorIndex}.
     * This index is exclusive and should be equals to {@code getStart() + getLength()}.
     *
     * @return the end position as a global view.
     */
    int getEnd();

    /**
     * Returns the length of the element.
     *
     * @return the length.
     */
    int getLength();

    /**
     * Returns the text of the element.
     *
     * @return the text.
     */
    String getText();

    /**
     * Returns the scope of the element as a
     * {@link net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencedElement
     * referenced element}.
     * <p>
     * This is equals to {@link #getReferencingScope()} but considering the element may be global.
     *
     * @return the scope as a referenced element.
     */
    ElementScope getReferencedScope();

    /**
     * Returns the scope of the element as a
     * {@link net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencingElement
     * referencing element}.
     * <p>
     * <p>
     * This method should return the normal scope value.
     * The scope shouldn't be GLOBAl. This scope is reserved only for referenced elements.
     *
     * @return the scope as a referencing element.
     */
    ElementScope getReferencingScope();

    /**
     * Returns the {@link EditorIndexedParentElement parent} of this element if present.
     *
     * @return the parent if present.
     */
    Optional<EditorIndexedParentElement> getParent();

    /**
     * Returns the first parent in this element's hierarchy that matches the given type if present.
     *
     * @param type the type to match.
     * @param <T>  the type to match.
     * @return the parent if present.
     */
    <T extends EditorIndexedParentElement> Optional<T> getParentOfType(Class<T> type);

    /**
     * Returns the index of this element in its parent.
     * This method returns -1 if this method has no parent.
     *
     * @return the index or -1.
     */
    int indexInParent();

    /**
     * Returns whether this element represents a macro parameter.
     *
     * @return whether this element represents a macro parameter.
     */
    boolean isMacroParameter();

    /**
     * Moves the start of this element the given offset.
     *
     * @param offset the offset.
     */
    void move(int offset);

    /**
     * Changes the scope of the element.
     *
     * @param scope the scope.
     */
    void changeScope(ElementScope scope);

    /**
     * Invalidates this element.
     * This marks the element as not usable.
     * <p>
     * This should be used only by the index!
     */
    void invalidate();

    /**
     * Returns whether this element is still valid.
     *
     * @return whether this element is still valid.
     */
    boolean isValid();

    /**
     * Creates a new {@link Stream} that iterates this element an all its children.
     *
     * @return the {@link Stream}.
     */
    Stream<? extends EditorIndexedElement> elementStream();

    /**
     * Returns the {@link Metadata} of this element.
     * This metadata is mutable.
     *
     * @return the metadata.
     */
    Metadata getMetadata();

    /**
     * Inspects this element using the given inspectors.
     * <p>
     * The old inspections are deleted when this method is called, and a new metadata is generated.
     *
     * @param inspectors the inspectors to use.
     * @return the new metadata.
     */
    Metadata inspect(Collection<? extends Inspector<?>> inspectors);
}
