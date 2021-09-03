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
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorLineChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class EditorLineIndex<Line extends EditorIndexedLine> implements EditorIndex {

    private final List<Line> lines;

    public EditorLineIndex() {
        this.lines = new ArrayList<>();
    }

    @Override
    public void change(EditorLineChange change) {
    }

    @Override
    public Optional<EditorIndexedElement> getElementAt(int position) {
        return lines.stream()
                .filter(it -> it.getStart() >= position && it.getEnd() < position)
                .findAny().flatMap(it -> it.getElementAt(position));
    }

    protected abstract Line generateNewLine(int start, int end, int index, String text);

}
