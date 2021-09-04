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
import net.jamsimulator.jams.utils.Validate;

public class EditorIndexedElementImpl implements EditorIndexedElement {

    protected final EditorIndex index;
    protected final String text;
    protected int start;

    protected boolean valid;

    public EditorIndexedElementImpl(EditorIndex index, int start, String text) {
        Validate.notNull(index, "Index cannot be null!");
        Validate.notNull(text, "Text cannot be null!");
        Validate.isTrue(start >= 0, "Start cannot be negative!");
        this.index = index;
        this.start = start;
        this.text = text;
        this.valid = true;
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
    public void move(int offset) {
        Validate.isTrue(start + offset >= 0, "Resulted start cannot be negative!");
        start += offset;
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
}
