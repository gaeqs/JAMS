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

import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorLineChange;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.line.EditorIndexedLine;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class EditorLineIndex<Line extends EditorIndexedLine> implements EditorIndex {

    private final List<Line> lines = new ArrayList<>();

    private final Lock editLock = new ReentrantLock();
    private volatile boolean editing = false;

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
        if (text.isEmpty()) return;

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

    protected void editLine(int number, String text) {
        var old = lines.get(number);
        var line = generateNewLine(old.getStart(), number, text);
        lines.set(number, line);
        old.invalidate();

        int difference = line.getLength() - old.getLength();
        lines.listIterator(number + 1).forEachRemaining(it -> it.move(difference));
    }

    protected void removeLine(int number) {
        var line = lines.remove(number);
        line.invalidate();
        lines.listIterator(number).forEachRemaining(it ->
                it.movePositionAndNumber(-1, -line.getLength()));
    }

    protected void addLine(int number, String text) {
        int start = number == 0 ? 0 : lines.get(number - 1).getEnd() + 1;
        var line = generateNewLine(start, number, text);
        lines.add(number, line);
        lines.listIterator(number + 1).forEachRemaining(it ->
                it.movePositionAndNumber(1, line.getLength()));
    }

    protected abstract Line generateNewLine(int start, int index, String text);

}
