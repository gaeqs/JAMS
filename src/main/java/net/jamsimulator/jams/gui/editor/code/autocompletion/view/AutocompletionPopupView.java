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

package net.jamsimulator.jams.gui.editor.code.autocompletion.view;

import javafx.scene.Node;
import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionOption;
import net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionPopup;

import java.util.List;
import java.util.Optional;

/**
 * Represents the visual node of an {@link AutocompletionPopup}.
 */
public interface AutocompletionPopupView {

    /**
     * Populates this view with the given options.
     *
     * @param popup   the {@link AutocompletionPopup} requesting the operation.
     * @param options the options.
     */
    void showContents(AutocompletionPopup popup, List<AutocompletionOption<?>> options);

    /**
     * Returns this view as a JavaFX's {@link Node}.
     *
     * @return this view as a {@link Node}.
     */
    Node asNode();

    /**
     * Returns the selected element's replacement if present.
     *
     * @return the selected element's replacement.
     */
    Optional<String> getSelected();

    /**
     * Returns the selected element of this view if present.
     *
     * @return the selected eleent.
     */
    Optional<Object> getSelectedElement();

    /**
     * Selects the previous element of this popup.
     * <p>
     * This method invokes a cyclic selection.
     * <p>
     * This method must create a {@link
     * net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionElementselectEvent
     * AutocompletionElementselectEvent}.
     *
     * @param popup the {@link AutocompletionPopup} requesting this operation.
     */
    void moveUp(AutocompletionPopup popup);

    /**
     * Selects the next element of this popup.
     * <p>
     * This method invokes a cyclic selection.
     * <p>
     * This method must create a {@link
     * net.jamsimulator.jams.gui.editor.code.autocompletion.AutocompletionElementselectEvent
     * AutocompletionElementselectEvent}.
     *
     * @param popup the {@link AutocompletionPopup} requesting this operation.
     */
    void moveDown(AutocompletionPopup popup);

}
