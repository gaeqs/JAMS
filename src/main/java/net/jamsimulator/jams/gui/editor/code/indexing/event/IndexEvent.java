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

package net.jamsimulator.jams.gui.editor.code.indexing.event;

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.gui.editor.code.indexing.EditorIndex;

/**
 * Base class for an event related to a {@link EditorIndex}.
 */
public class IndexEvent extends Event {

    /**
     * The editor index related to this event.
     */
    protected final EditorIndex index;

    /**
     * Creates the event.
     *
     * @param index the {@link EditorIndex}.
     */
    public IndexEvent(EditorIndex index) {
        this.index = index;
    }

    /**
     * Returns the {@link EditorIndex} related to this event.
     *
     * @return the {@link EditorIndex}.
     */
    public EditorIndex getIndex() {
        return index;
    }
}
