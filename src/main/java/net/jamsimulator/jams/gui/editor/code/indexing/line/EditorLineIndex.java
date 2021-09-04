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

package net.jamsimulator.jams.gui.editor.code.indexing.line;

import net.jamsimulator.jams.collection.Bag;
import net.jamsimulator.jams.gui.editor.code.CodeFileEditor;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorLineChange;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.line.EditorIndexedLine;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorElementReference;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorGlobalMarkerElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.reference.EditorReferencingElement;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

public abstract class EditorLineIndex<Line extends EditorIndexedLine> implements EditorIndex {

    private final CodeFileEditor editor;

    private final List<Line> lines = new ArrayList<>();
    private final Map<EditorElementReference<?>, Set<EditorReferencingElement>> referencingElements = new HashMap<>();
    private final Map<EditorElementReference<?>, Set<EditorReferencedElement>> referencedElements = new HashMap<>();
    private final Bag<String> globalIdentifiers = new Bag<>();

    private final Lock editLock = new ReentrantLock();
    private volatile boolean editing = false;

    public EditorLineIndex(CodeFileEditor editor) {
        this.editor = editor;
    }

    @Override
    public CodeFileEditor getEditor() {
        return editor;
    }

    @Override
    public void change(EditorLineChange change) {
        if (!editing) throw new IllegalStateException("Index is not in edit mode!");
        switch (change.type()) {
            case EDIT -> editLine(change.line(), change.text());
            case REMOVE -> removeLine(change.line());
            case ADD -> addLine(change.line(), change.text());
        }
    }

    @Override
    public void indexAll(String text) {
        lines.clear();
        if (text.isEmpty()) {
            lines.add(generateNewLine(0, 0, ""));
            return;
        }

        int start = 0;
        int end = 0;
        var builder = new StringBuilder();

        char c;
        while (text.length() > end) {
            c = text.charAt(end);
            if (c == '\n' || c == '\r') {
                lines.add(generateNewLine(start, lines.size(), builder.toString()));
                builder = new StringBuilder();
                start = end + 1;
            } else {
                builder.append(c);
            }
            end++;
        }

        if (end >= start) {
            lines.add(generateNewLine(start, lines.size(), builder.toString()));
        }

        lines.forEach(this::addReferences);
    }

    @Override
    public Optional<EditorIndexedElement> getElementAt(int position) {
        return lines.stream()
                .filter(it -> it.getStart() >= position && it.getEnd() < position)
                .findAny().flatMap(it -> it.getElementAt(position));
    }

    @Override
    public void startEditing() {
        editLock.lock();
        editing = true;
    }

    @Override
    public void finishEditing() {
        editing = false;
        editLock.unlock();
    }

    @Override
    public boolean isEditing() {
        return editing;
    }

    @Override
    public Stream<? extends EditorIndexedElement> elementStream() {
        return lines.stream().flatMap(EditorIndexedElement::elementStream);
    }

    @Override
    public <T extends EditorReferencedElement>
    Optional<T> getReferencedElement(EditorElementReference<T> reference) {
        return Optional.ofNullable((T) referencedElements.get(reference));
    }

    @Override
    public <T extends EditorReferencedElement>
    Set<EditorReferencingElement> getReferecingElements(EditorElementReference<T> reference) {
        var set = referencingElements.get(reference);
        if (set == null) return Collections.emptySet();
        return Set.copyOf(set);
    }

    protected void editLine(int number, String text) {
        var old = lines.get(number);
        var line = generateNewLine(old.getStart(), number, text);
        lines.set(number, line);
        old.invalidate();

        int difference = line.getLength() - old.getLength();
        lines.listIterator(number + 1).forEachRemaining(it -> it.move(difference));
        removedReferences(old);
        addReferences(line);
    }

    protected void removeLine(int number) {
        var line = lines.remove(number);
        line.invalidate();
        lines.listIterator(number).forEachRemaining(it ->
                it.movePositionAndNumber(-1, -line.getLength()));
        removedReferences(line);
    }

    protected void addLine(int number, String text) {
        int start = number == 0 ? 0 : lines.get(number - 1).getEnd() + 1;
        var line = generateNewLine(start, number, text);
        lines.add(number, line);
        lines.listIterator(number + 1).forEachRemaining(it ->
                it.movePositionAndNumber(1, line.getLength()));
        addReferences(line);
    }

    protected void removedReferences(Line line) {
        line.elementStream().forEach(element -> {
            if (element instanceof EditorReferencingElement referencing) {
                referencing.getReferences().forEach(reference -> {
                    var set = referencingElements.get(reference);
                    if (set != null) set.remove(referencing);
                });
            }
            if (element instanceof EditorReferencedElement referenced) {
                var set = referencedElements.get(referenced.getReference());
                if (set != null) set.remove(referenced);
            }
            if(element instanceof EditorGlobalMarkerElement marker) {
                globalIdentifiers.removeAll(marker.getGlobalIdentifiers());
            }
        });
    }

    protected void addReferences(Line line) {
        line.elementStream().forEach(element -> {
            if (element instanceof EditorReferencingElement referencing) {
                referencing.getReferences().forEach(reference -> {
                    referencingElements.computeIfAbsent(reference, it -> new HashSet<>()).add(referencing);
                });
            }
            if (element instanceof EditorReferencedElement referenced) {
                referencedElements.computeIfAbsent(referenced.getReference(), it -> new HashSet<>()).add(referenced);
            }
            if(element instanceof EditorGlobalMarkerElement marker) {
                globalIdentifiers.addAll(marker.getGlobalIdentifiers());
            }
        });
    }

    protected abstract Line generateNewLine(int start, int number, String text);
}
