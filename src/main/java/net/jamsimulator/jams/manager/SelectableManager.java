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

import java.util.HashSet;

/**
 * Represents a {@link Manager} that has a default value and a selected value.
 * <p>
 * The default value can be changed using {@link #setDefault(ManagerResource)}.
 * The selected value can be changed using {@link #setSelected(ManagerResource)}.
 * Implement {@link #loadDefaultElement()} to define the default value when the manager is created.
 * Implement {@link #loadSelectedElement()} to define the selected value when the manager is created.
 *
 * @param <Type> the type of the managed elements.
 * @see Manager
 * @see DefaultValuableManager
 */
public abstract class SelectableManager<Type extends ManagerResource> extends DefaultValuableManager<Type> {

    protected Type selected;

    private boolean superclassLoaded = false;

    /**
     * Creates the manager.
     * These managers call events on addition, removal and default set. You must provide the builder for these events.
     */
    public SelectableManager(ResourceProvider provider, String name, Class<Type> managedType, boolean loadOnFXThread) {
        super(provider, name, managedType, loadOnFXThread);
    }

    /**
     * Returns the selected element of this manager.
     * <p>
     * This element cannot be removed from the manager.
     *
     * @return the selected element.
     */
    public Type getSelected() {
        if (!loaded) loadPanic();
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
        if (!loaded) loadPanic();

        if (selected == this.selected) return false;

        var before =
                callEvent(new ManagerSelectedElementChangeEvent.Before<>(this, managedType, this.selected, selected));
        if (before.isCancelled()) return false;

        var old = this.selected;
        this.selected = selected;

        callEvent(new ManagerSelectedElementChangeEvent.After<>(this, managedType, old, selected));
        return true;
    }

    /**
     * Attempts to unregister the given element.
     * <p>
     * If the element is null this method throws a {@link NullPointerException}.
     * If the element type is invalid this method returns {@code false}.
     * If the element is not present or the unregister event is cancelled this method returns {@code false}.
     * If the operation was successful the method returns {@code true}.
     * <p>
     * If the default element equals to the given element selects the first found element.
     * If the selected element equals to the given element selects the default value.
     *
     * @param o the element to register.
     * @return whether the operation was successful.
     * @throws NullPointerException when the given element is null.
     * @see HashSet#add(Object)
     */
    @Override
    public boolean remove(Object o) {
        if (super.remove(o)) {
            if (selected.equals(o)) {
                selected = defaultValue;
            }
            return true;
        }
        return false;
    }

    /**
     * This method is called on construction.
     * Implement this method to define the selected value of this manager.
     *
     * @return the selected element of this manager.
     */
    protected abstract Type loadSelectedElement();

    @Override
    public void load() {
        super.load();
        this.selected = loadSelectedElement();
        callLoadAfterEvent();
    }

    @Override
    protected void callLoadAfterEvent() {
        if (!superclassLoaded) superclassLoaded = true;
        else super.callLoadAfterEvent();
    }
}
