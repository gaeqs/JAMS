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

package net.jamsimulator.jams.manager;

import net.jamsimulator.jams.manager.event.ManagerSelectedElementChangeEvent;
import net.jamsimulator.jams.utils.Validate;

/**
 * Represents a {@link Manager} that has a default value and a selected value.
 * <p>
 * The default value can be changed using {@link #setDefault(Labeled)}.
 * The selected value can be changed using {@link #setSelected(Labeled)}.
 * Implement {@link #loadDefaultElement()} to define the default value when the manager is created.
 * Implement {@link #loadSelectedElement()} to define the selected value when the manager is created.
 *
 * @param <Type> the type of the managed elements.
 * @see Manager
 * @see DefaultValuableManager
 */
public abstract class SelectableManager<Type extends Labeled> extends DefaultValuableManager<Type> {

    protected Type selected;

    /**
     * Creates the manager.
     * These managers call events on addition, removal and default set. You must provide the builder for these events.
     */
    public SelectableManager() {
        this.selected = loadSelectedElement();
    }

    /**
     * Returns the selected element of this manager.
     * <p>
     * This element cannot be removed from the manager.
     *
     * @return the selected element.
     */
    public Type getSelected() {
        return selected;
    }

    /**
     * Sets the selected element of this manager.
     * <p>
     * If the given element is null, this method throws a {@link NullPointerException}.
     * If the given element is not present in this manager, this method throws an {@link IllegalArgumentException}.
     * <p>
     * If the element is already the default value or the change event is cancelled, this method returns {@code false}.
     * If the operation was successful, this method returns {@code true}.
     *
     * @param selected the new selected element.
     * @return whether the operation was successful.
     */
    public boolean setSelected(Type selected) {
        Validate.notNull(selected, "The default value cannot be null!");
        Validate.isTrue(contains(selected), "The default value must be registered!");

        if (selected == this.selected) return false;

        var before =
                callEvent(new ManagerSelectedElementChangeEvent.Before<>(this, this.selected, selected));
        if (before.isCancelled()) return false;

        var old = this.selected;
        this.selected = selected;

        callEvent(new ManagerSelectedElementChangeEvent.After<>(this, old, selected));
        return true;
    }

    /**
     * This method is called on construction.
     * Implement this method to define the selected value of this manager.
     *
     * @return the selected element of this manager.
     */
    protected abstract Type loadSelectedElement();
}
