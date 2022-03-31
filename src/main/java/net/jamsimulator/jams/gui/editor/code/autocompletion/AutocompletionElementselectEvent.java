/*
 *  MIT License
 *
 *  Copyright (c) 2022 Gael Rial Costas
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

package net.jamsimulator.jams.gui.editor.code.autocompletion;

import net.jamsimulator.jams.event.Event;

/**
 * Event triggered when an autocompletion element is selected.
 * <p>
 * This event must be invoked by the
 * {@link net.jamsimulator.jams.gui.editor.code.autocompletion.view.AutocompletionPopupView autocompletion popup view}.
 */
public class AutocompletionElementselectEvent extends Event {

    private final AutocompletionPopup popup;
    private final Object element;

    /**
     * Creates a new select event.
     *
     * @param popup   the popup invoking this event.
     * @param element the selected element.
     */
    public AutocompletionElementselectEvent(AutocompletionPopup popup, Object element) {
        this.popup = popup;
        this.element = element;
    }

    /**
     * Returns the popup invoking this event.
     *
     * @return the {@link AutocompletionPopup}.
     */
    public AutocompletionPopup getPopup() {
        return popup;
    }

    /**
     * Returns the new selected element.
     *
     * @return the selected element.
     */
    public Object getElement() {
        return element;
    }
}
