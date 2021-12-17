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

package net.jamsimulator.jams.gui.editor.code.indexing.inspection;

import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacro;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.manager.ManagerResource;
import net.jamsimulator.jams.manager.ResourceProvider;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collections;
import java.util.Set;

/**
 * Represents the base class for generators of {@link Inspection}s.
 * <p>
 * Children of this class must implement {@link #inspectImpl(EditorIndexedElement)}.
 * This method must inspect the element and return the found {@link Inspection}s.
 * <p>
 * Inspectors are binded to one type of {@link EditorIndexedElement}, represented by {@link #getElementType()}.
 * Only elements that are children of this class will be sent to the inspector.
 *
 * @param <T> the type of the element to inspect.
 */
public abstract class Inspector<T extends EditorIndexedElement> implements ManagerResource {

    protected final ResourceProvider provider;
    protected final String name;
    protected final Class<T> elementType;

    /**
     * Creates the inspector.
     *
     * @param provider    the provider providing this inspector.
     * @param name        the name of the inspection. This must be unique and not null!
     * @param elementType the type of the element to inspect.
     */
    public Inspector(ResourceProvider provider, String name, Class<T> elementType) {
        Validate.notNull(provider, "Provider cannot be null!");
        Validate.notNull(name, "Name cannot be null!");
        Validate.notNull(elementType, "Element type cannot be null!");
        this.provider = provider;
        this.name = name;
        this.elementType = elementType;
    }

    @Override
    public ResourceProvider getResourceProvider() {
        return provider;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the type of the element this inspector inspects.
     *
     * @return the type of the element this inspector inspects.
     */
    public Class<T> getElementType() {
        return elementType;
    }

    /**
     * Inspects the given element.
     * <p>
     * This method returns an empty set if the given element is null.
     * <p>
     * The returned set may be immutable!
     *
     * @param element the element.
     * @return the inspections.
     */
    public Set<Inspection> inspect(EditorIndexedElement element) {
        if (element == null) return Collections.emptySet();
        try {
            var scope = element.getReferencingScope();
            if (element.getIdentifier().startsWith("%") && scope.type() == ElementScope.Type.MACRO) {

                // If the element is inside a macro we must check if it is any of the parameters.
                var reference = new EditorElementReference<>(EditorElementMacro.class, scope.macroIdentifier());
                var macro = element.getIndex().getReferencedElement(reference, scope);

                if (macro.isPresent()) {
                    if (macro.get().getParameters().contains(element.getIdentifier())) {
                        // If the element is a macro parameter, do nothing.
                        return Collections.emptySet();
                    }
                }
            }

            return inspectImpl((T) element);
        } catch (ClassCastException ex) {
            return Collections.emptySet();
        }
    }

    /**
     * The inspector's implementation. Children must override this method.
     *
     * @param element the element to inspect.
     * @return the inspections.
     * @see #inspect(EditorIndexedElement)
     */
    protected abstract Set<Inspection> inspectImpl(T element);

}
