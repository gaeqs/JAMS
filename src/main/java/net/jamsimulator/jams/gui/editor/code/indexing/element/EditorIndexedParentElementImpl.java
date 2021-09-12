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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class EditorIndexedParentElementImpl extends EditorIndexedElementImpl implements EditorIndexedParentElement {

    protected final List<EditorIndexedElement> elements;

    public EditorIndexedParentElementImpl(EditorIndex index, EditorIndexedParentElement parent,
                                          int start, String text) {
        super(index, parent, start, text);
        this.elements = new ArrayList<>();
    }

    public Optional<? extends EditorIndexedElement> getElementAt(int position) {
        return elements.stream().filter(it -> it.getStart() >= position && it.getEnd() < position).findAny();
    }

    @Override
    public int indexOf(EditorIndexedElement element) {
        return elements.indexOf(element);
    }

    @Override
    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        elements.forEach(EditorIndexedElement::invalidate);
    }

    @Override
    public void move(int offset) {
        super.move(offset);
        elements.forEach(child -> child.move(offset));
    }

    @Override
    public Stream<? extends EditorIndexedElement> elementStream() {
        return Stream.concat(Stream.of(this), elements.stream().flatMap(EditorIndexedElement::elementStream));
    }
}
