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

package net.jamsimulator.jams.gui.editor.code.indexing.element.line;

import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexStyleableElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedElement;
import net.jamsimulator.jams.gui.editor.code.indexing.element.EditorIndexedParentElementImpl;
import net.jamsimulator.jams.gui.util.EasyStyleSpansBuilder;
import net.jamsimulator.jams.utils.Validate;

import java.util.Comparator;

public class EditorIndexedLine extends EditorIndexedParentElementImpl {

    protected int number;

    public EditorIndexedLine(EditorIndex index, int start, int number, String text) {
        super(index, null, start, text);
        Validate.isTrue(number >= 0, "Index cannot be negative!");
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void moveNumber(int offset) {
        Validate.isTrue(number + offset >= 0, "Resulted index cannot be negative!");
        number += offset;
    }

    public void movePositionAndNumber(int numberOffset, int positionOffset) {
        moveNumber(numberOffset);
        move(positionOffset);
    }

    public void addStyles(EasyStyleSpansBuilder builder, int offset) {
        elementStream()
                .filter(it -> it instanceof EditorIndexStyleableElement)
                .sorted(Comparator.comparingInt(EditorIndexedElement::getStart))
                .forEach(it -> builder.add(it.getStart() - offset, it.getLength(),
                        ((EditorIndexStyleableElement) it).getStyles()));
    }
}
