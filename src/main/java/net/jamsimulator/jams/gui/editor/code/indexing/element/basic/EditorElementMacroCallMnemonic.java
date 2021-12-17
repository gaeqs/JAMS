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

package net.jamsimulator.jams.gui.editor.code.indexing.element.basic;

import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexStyleableElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElementImpl;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.ElementScope;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencingElement;

import java.util.Collection;
import java.util.Set;

/**
 * Represents the mnemonic of a macro call.
 */
public class EditorElementMacroCallMnemonic extends EditorIndexedElementImpl
        implements EditorIndexStyleableElement, EditorReferencingElement<EditorElementMacro> {

    public static final Set<String> STYLE = Set.of("macro-call");

    private final Set<EditorElementReference<EditorElementMacro>> references;

    public EditorElementMacroCallMnemonic(EditorIndex index, ElementScope scope, EditorIndexedParentElement parent,
                                          int start, String text) {
        super(index, scope, parent, start, text);
        references = Set.of(new EditorElementReference<>(EditorElementMacro.class, getIdentifier()));
    }

    @Override
    public Collection<String> getStyles() {
        return STYLE;
    }

    @Override
    public Set<EditorElementReference<EditorElementMacro>> getReferences() {
        return references;
    }
}
