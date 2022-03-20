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

package net.jamsimulator.jams.gui.editor.code.indexing.element.reference;

import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Represents a relative reference to an element.
 * <p>
 * This reference uses its relative position instead of an identifier to resolve the referenced element.
 *
 * @param <R> the type of the referenced element.
 */
public class EditorElementRelativeReference<R extends EditorReferencedElement> extends EditorElementReference<R> {

    private final Type type;
    private final EditorIndexedElement element;

    /**
     * Creates a new relative reference.
     *
     * @param referencedType the type of the referenced element.
     * @param type           the type of the relative reference.
     * @param element        the element creating this referenced.
     */
    public EditorElementRelativeReference(Class<R> referencedType, Type type, EditorIndexedElement element) {
        super(referencedType, type.identifier);
        this.type = type;
        this.element = element;
    }

    /**
     * Returns the {@link Type type} of the relative reference.
     *
     * @return the {@link Type type}.
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the {@link EditorIndexedElement element} that is holding this reference.
     *
     * @return the {@link  EditorIndexedElement element}.
     */
    public EditorIndexedElement getElement() {
        return element;
    }

    /**
     * Returns the predicate used to filter elements based to this reference position.
     *
     * @return the predicate.
     */
    public Predicate<EditorReferencedElement> getFilterPredicate() {
        return element -> getType().getFilterPredicate().test(element, this);
    }

    public enum Type {
        PREVIOUS(
                "-",
                (it, relative) -> it.getStart() < relative.getElement().getStart(),
                Comparator.comparingInt(EditorReferencedElement::getStart).reversed()
        ),
        NEXT(
                "+",
                (it, relative) -> it.getStart() > relative.getElement().getStart(),
                Comparator.comparingInt(EditorReferencedElement::getStart)
        );

        private final String identifier;
        private final BiPredicate<EditorReferencedElement, EditorElementRelativeReference<?>> filterPredicate;
        private final Comparator<EditorReferencedElement> comparator;

        Type(String identifier, BiPredicate<EditorReferencedElement, EditorElementRelativeReference<?>> filterPredicate, Comparator<EditorReferencedElement> comparator) {
            this.identifier = identifier;
            this.filterPredicate = filterPredicate;
            this.comparator = comparator;
        }

        public String getIdentifier() {
            return identifier;
        }

        public BiPredicate<EditorReferencedElement, EditorElementRelativeReference<?>> getFilterPredicate() {
            return filterPredicate;
        }

        public Comparator<EditorReferencedElement> getComparator() {
            return comparator;
        }

        public static Optional<Type> getByIdentifier(String identifier) {
            return Arrays.stream(values()).filter(it -> it.identifier.equals(identifier)).findAny();
        }

    }

}
