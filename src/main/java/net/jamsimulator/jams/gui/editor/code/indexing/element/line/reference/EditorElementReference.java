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

package net.jamsimulator.jams.gui.editor.code.indexing.element.line.reference;

import net.jamsimulator.jams.gui.editor.code.indexing.line.reference.EditorReferencedLineIndex;

import java.lang.ref.WeakReference;
import java.util.Optional;

public class EditorElementReference<Referenced extends EditorReferencedElement> {

    protected final Class<? extends EditorReferencedElement> referencedType;
    protected final String identifier;

    protected WeakReference<? extends Referenced> reference;

    public EditorElementReference(Class<? extends EditorReferencedElement> referencedType, String identifier) {
        this.referencedType = referencedType;
        this.identifier = identifier;
    }

    Optional<? extends Referenced> getReference(EditorReferencedLineIndex<?> index) {
        if (reference == null) {
            return searchAndReturn();
        }
        var referenced = reference.get();
        if (referenced == null || !referenced.isValid()) {
            return searchAndReturn();
        }

        return Optional.of(referenced);
    }

    protected WeakReference<? extends Referenced> searchReference() {
        // TODO
        return null;
    }

    private Optional<? extends Referenced> searchAndReturn() {
        reference = searchReference();
        if (reference == null) return Optional.empty();
        var referenced = reference.get();
        return referenced == null || !referenced.isValid() ? Optional.empty() : Optional.of(referenced);
    }

}
