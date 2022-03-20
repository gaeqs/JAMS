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
import net.jamsimulator.jams.gui.editor.code.indexing.element.basic.EditorElementMacro;
import net.jamsimulator.jams.gui.editor.code.indexing.element.metadata.Metadata;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.inspection.Inspector;
import net.jamsimulator.jams.utils.Validate;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditorIndexedElementImpl implements EditorIndexedElement {

    protected final EditorIndexedParentElement parent;

    protected final EditorIndex index;
    protected final String text;

    protected ElementScope scope;
    protected int start;

    protected boolean valid;
    protected Metadata metadata;

    public EditorIndexedElementImpl(EditorIndex index, ElementScope scope, EditorIndexedParentElement parent,
                                    int start, String text) {
        Validate.notNull(index, "Index cannot be null!");
        Validate.notNull(text, "Text cannot be null!");
        Validate.isTrue(start >= 0, "Start cannot be negative!");
        Validate.notNull(scope, "Scope cannot be null!");
        this.index = index;
        this.scope = scope;
        this.parent = parent;
        this.start = start;
        this.text = text;
        this.valid = true;
        this.metadata = Metadata.EMPTY;
    }

    @Override
    public EditorIndex getIndex() {
        return index;
    }

    @Override
    public String getIdentifier() {
        return text;
    }

    @Override
    public int getStart() {
        return start;
    }

    @Override
    public int getEnd() {
        return start + getLength();
    }

    @Override
    public int getLength() {
        return text.length();
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public ElementScope getReferencedScope() {
        if (ElementScope.GLOBAL.equals(scope.parent()) && this instanceof EditorReferencedElement) {
            return index.isIdentifierGlobal(getIdentifier()) ? ElementScope.GLOBAL : scope;
        }
        return scope;
    }

    @Override
    public ElementScope getReferencingScope() {
        return scope;
    }

    @Override
    public Optional<EditorIndexedParentElement> getParent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public <T extends EditorIndexedParentElement> Optional<T> getParentOfType(Class<T> type) {
        Validate.notNull(type, "Type cannot be null!");
        if (parent == null) return Optional.empty();
        if (type.isInstance(parent)) return Optional.of((T) parent);
        return parent.getParentOfType(type);
    }

    @Override
    public int indexInParent() {
        return parent == null ? -1 : parent.indexOf(this);
    }

    @Override
    public boolean isMacroParameter() {
        if (scope.macroIdentifier().isEmpty() || !getIdentifier().startsWith("%")) return false;
        var reference = new EditorElementReference<>(EditorElementMacro.class, scope.macroIdentifier());
        var macro = index.getReferencedElement(reference, scope);
        return macro.isPresent() && macro.get().getParameterNames().contains(getIdentifier());
    }

    @Override
    public void move(int offset) {
        Validate.isTrue(start + offset >= 0, "Resulted start cannot be negative!");
        start += offset;
    }

    @Override
    public void changeScope(ElementScope scope) {
        Validate.notNull(scope, "Scope cannot be null!");
        this.scope = scope;
    }

    @Override
    public void invalidate() {
        valid = false;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public int compareTo(EditorIndexedElement o) {
        return Integer.compare(getStart(), o.getStart());
    }

    @Override
    public Stream<? extends EditorIndexedElement> elementStream() {
        return Stream.of(this);
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public Metadata inspect(Collection<? extends Inspector<?>> inspectors) {
        var set = inspectors.stream()
                .filter(i -> i.getElementType().isInstance(this))
                .flatMap(i -> i.inspect(this).stream())
                .collect(Collectors.toSet());
        metadata = new Metadata(set);
        return metadata;
    }
}
