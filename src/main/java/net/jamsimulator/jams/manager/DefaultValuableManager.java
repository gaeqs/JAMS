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

import net.jamsimulator.jams.manager.event.ManagerDefaultElementChangeEvent;
import net.jamsimulator.jams.utils.Validate;

import java.util.HashSet;

/**
 * Represents a {@link Manager} that has a default value.
 * <p>
 * The default value can be changed using {@link #setDefault(ManagerResource)}.
 * Implement {@link #loadDefaultElement()} to define the default value when the manager is created.
 *
 * @param <Type> the type of the managed elements.
 * @see Manager
 */
public abstract class DefaultValuableManager<Type extends ManagerResource> extends Manager<Type> {

    protected Type defaultValue;

    private boolean superclassLoaded = false;

    /**
     * Creates the manager.
     * These managers call events on addition, removal and default set.
     */
    public DefaultValuableManager(ResourceProvider provider, String name, Class<Type> managedType, boolean loadOnFXThread) {
        super(provider, name, managedType, loadOnFXThread);
    }

    /**
     * Returns the default element of this manager.
     * <p>
     * This element cannot be removed from the manager.
     *
     * @return the default element.
     */
    public Type getDefault() {
        if (!loaded) loadPanic();
        return defaultValue;
    }

    /**
     * Sets the default element of this manager.
     * <p>
     * If the given element is null, this method throws a {@link NullPointerException}.
     * If the given element is not present in this manager, this method throws an {@link IllegalArgumentException}.
     * <p>
     * If the element is already the default value or the change event is cancelled, this method returns {@code false}.
     * If the operation was successful, this method returns {@code true}.
     *
     * @param defaultValue the new default element.
     * @return whether the operation was successful.
     */
    public boolean setDefault(Type defaultValue) {
        Validate.notNull(defaultValue, "The default value cannot be null!");
        Validate.isTrue(contains(defaultValue), "The default value must be registered!");
        if (!loaded) loadPanic();

        if (defaultValue == this.defaultValue) return false;

        var before =
                callEvent(new ManagerDefaultElementChangeEvent.Before<>(this, managedType, this.defaultValue, defaultValue));
        if (before.isCancelled()) return false;

        var old = this.defaultValue;
        this.defaultValue = defaultValue;

        callEvent(new ManagerDefaultElementChangeEvent.After<>(this, managedType, old, defaultValue));
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
     *
     * @param o the element to register.
     * @return whether the operation was successful.
     * @throws NullPointerException when the given element is null.
     * @see HashSet#add(Object)
     */
    @Override
    public boolean remove(Object o) {
        if(super.remove(o)) {
            if (defaultValue.equals(o)) {
                defaultValue = stream().filter(it -> it != defaultValue).findFirst().orElse(null);
            }
            return true;
        }
        return false;
    }

    /**
     * This method is called on construction.
     * Implement this method to define the default value of this manager.
     *
     * @return the default element of this manager.
     */
    protected abstract Type loadDefaultElement();

    @Override
    public void load() {
        super.load();
        this.defaultValue = loadDefaultElement();
        callLoadAfterEvent();
    }

    @Override
    protected void callLoadAfterEvent() {
        if (!superclassLoaded) superclassLoaded = true;
        else super.callLoadAfterEvent();
    }
}
