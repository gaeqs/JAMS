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

package net.jamsimulator.jams.gui.explorer;

import net.jamsimulator.jams.gui.ActionRegion;

import java.util.Optional;

/**
 * Represents an element inside an {@link ExplorerSection}.
 */
public interface ExplorerElement extends ActionRegion {

    /**
     * Returns the name of the element. This may be the name shown to the user.
     *
     * @return the name of the element.
     */
    String getName();


    /**
     * Returns the name that these elements is currently showing to the user.
     * This is the string that should be used for filters and lists.
     *
     * @return the visible name.
     */
    String getVisibleName();

    /**
     * Returns the {@link Explorer} of this element.
     *
     * @return the {@link Explorer}.
     */
    Explorer getExplorer();

    /**
     * Returns the parent {@link ExplorerSection}.
     *
     * @return the parent {@link ExplorerSection}.
     */
    Optional<? extends ExplorerSection> getParentSection();

    /**
     * Returns whether the element is selected. If true, the elements
     * should be shown in the GUI with a blue background.
     *
     * @return whether the element is selected.
     */
    boolean isSelected();

    /**
     * Selects this element.
     * <p>
     * This method only marks this element as selected.
     * If you want to select this element use {@link Explorer#selectElementAlone(ExplorerElement)}.
     *
     * @see #isSelected() .
     */
    void select();

    /**
     * Deselects this element.
     * <p>
     * This method only marks this element as not selected.
     * If you want to deselect this element use {@link Explorer#deselectAll()}
     * or {@link Explorer#addOrRemoveSelectedElement(ExplorerElement)}.
     *
     * @see #isSelected() .
     */
    void deselect();

    /**
     * Returns the next element in the explorer. This is the element
     * shown below this element in the GUI.
     *
     * @return the next element, if present.
     */
    Optional<ExplorerElement> getNext();

    /**
     * Returns the previous element in the explorer. This is the element
     * shown above this element in the GUI.
     *
     * @return the previous element, if present.
     */
    Optional<ExplorerElement> getPrevious();

    /**
     * Returns the y translation of this element inside the explorer.
     *
     * @return the y translation.
     */
    double getExplorerYTranslation();

    /**
     * Returns the height of this element.
     *
     * @return the height.
     */
    double getElementHeight();

    /**
     * Returns the total amount of children inside this element.
     *
     * @return the amount.
     */
    int getTotalElements();

    /**
     * Creates a context menu for the {@link Explorer} containing this element.
     */
    void createContextMenu(double screenX, double screenY);
}
