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

package net.jamsimulator.jams.gui.project.bottombar;

import javafx.scene.Node;
import net.jamsimulator.jams.manager.Labeled;

/**
 * Represents an element inside a {@link ProjectBottomBar}.
 * <p>
 * This interface extends {@link Labeled}. You must provide an immutable name
 * to this element for a proper behaviour.
 */
public interface ProjectBottomBarElement extends Labeled {

    /**
     * Returns the position where the element will be shown inside the {@link ProjectBottomBar}.
     * <p>
     * This value must be immutable. Changes to the value returned by this method
     * retult in unpredictable behaviours.
     *
     * @return the position.
     */
    ProjectBottomBarPosition getPosition();

    /**
     * Returns the priority of the element in the {@link ProjectBottomBar}. Elements with
     * higher priorities will be shown first.
     * <p>
     * This value must be immutable. Changes to the value returned by this method
     * retult in unpredictable behaviours.
     *
     * @return the priority.
     */
    int getPriority();

    /**
     * Return this element as a {@link Node}.
     * <p>
     * This method is used by the {@link ProjectBottomBar} to add
     * this element to the JavaFX scene.
     *
     * @return this element as a {@link Node}.
     */
    Node asNode();

}
