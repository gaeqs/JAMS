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

import net.jamsimulator.jams.Jams;
import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.manager.event.ManagerElementRegisterEvent;
import net.jamsimulator.jams.manager.event.ManagerElementUnregisterEvent;
import net.jamsimulator.jams.manager.event.ManagerLoadEvent;
import net.jamsimulator.jams.manager.event.ManagerRequestingDefaultElementsEvent;
import net.jamsimulator.jams.utils.Labeled;
import net.jamsimulator.jams.utils.Validate;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;

/**
 * Represents a storage for instances of the selected type.
 * You can add, remove and get elements from this manager.
 * <p>
 * Elements stored by a manager must implement {@link Labeled}.
 * <p>
 * Every addition will call a register event, and every removal will call an unregister event.
 * <p>
 * Instances of this class are used to manage the storage of several kind of elements inside JAMS.
 *
 * @param <Type> the type of the managed elements.
 * @see DefaultValuableManager
 * @see SelectableManager
 */
public abstract class Manager<Type extends Labeled> extends HashSet<Type> implements EventBroadcast {

    public static <T extends Manager<?>> T get(Class<T> clazz) {
        return Jams.REGISTRY.get(clazz);
    }

    public static <T extends Labeled> Manager<T> of(Class<T> clazz) {
        return Jams.REGISTRY.of(clazz);
    }

    public static <T extends Labeled> DefaultValuableManager<T> ofD(Class<T> clazz) {
        return (DefaultValuableManager<T>) Jams.REGISTRY.of(clazz);
    }

    public static <T extends Labeled> SelectableManager<T> ofS(Class<T> clazz) {
        return (SelectableManager<T>) Jams.REGISTRY.of(clazz);
    }

    /**
     * The event broadcast of the manager. This allows the manager to call events.
     */
    protected final SimpleEventBroadcast broadcast;

    /**
     * The managed type of this manager.
     */
    protected final Class<Type> managedType;

    /**
     * Whether this manager should be loaded on the JavaXF thread.
     */
    protected final boolean loadOnFXThread;

    /**
     * Whether this manager is loaded.
     */
    protected boolean loaded;

    /**
     * Creates the manager.
     * These managers call events on addition and removal.
     */
    public Manager(Class<Type> managedType, boolean loadOnFXThread) {
        this.broadcast = new SimpleEventBroadcast();
        this.managedType = managedType;
        this.loadOnFXThread = loadOnFXThread;
        this.loaded = false;
    }

    /**
     * Returns whether this manager should be loaded on the JavaFX thread.
     *
     * @return whether this manager should be loaded on the JavaFX thread.
     */
    public boolean shouldLoadOnFXThread() {
        return loadOnFXThread;
    }

    /**
     * Returns the type of elements this manager manages.
     *
     * @return the type of elements.
     */
    public Class<Type> getManagedType() {
        return managedType;
    }

    /**
     * Returns whether this manager is loaded.
     *
     * @return whether this manager is loaded.
     */
    public boolean isLoaded() {
        return loaded;
    }

    /**
     * Loads this manager.
     */
    public void load() {
        if (loaded) throw new IllegalStateException("Manager " + this + " is already loaded!");
        callEvent(new ManagerLoadEvent.Before<>(this, managedType));
        loaded = true;
        loadDefaultElements();
        callEvent(new ManagerRequestingDefaultElementsEvent<>(this, managedType));
        callLoadAfterEvent();
    }

    protected void callLoadAfterEvent() {
        callEvent(new ManagerLoadEvent.After<>(this, managedType));
    }

    /**
     * Returns the elements that matches the given name, if present.
     *
     * @param name the name.
     * @return the element, if present.
     */
    public Optional<Type> get(String name) {
        if (!loaded) loadPanic();
        return stream().filter(t -> t.getName().equals(name)).findAny();
    }

    /**
     * Returns the element that matched the given name.
     * If the elements is not present in this manager, this method returns null.
     * <p>
     * For a safe-return method, use {@link #get(String)}.
     *
     * @param name the name.
     * @return the element, or null if not present.
     */
    public Type getOrNull(String name) {
        if (!loaded) loadPanic();
        return get(name).orElse(null);
    }

    /**
     * Attempts to register the given element.
     * If the element is null this method throws an {@link NullPointerException}.
     * If the element is already present or the register event is cancelled this method returns {@code false}.
     * If the operation was successful the method returns {@code true}.
     *
     * @param element the element to register.
     * @return whether the operation was successful.
     * @throws NullPointerException when the given element is null.
     * @see HashSet#add(Object)
     */
    @Override
    public boolean add(Type element) {
        Validate.notNull(element, "The element cannot be null!");
        if (!loaded) loadPanic();
        var before =
                callEvent(new ManagerElementRegisterEvent.Before<>(this, managedType, element));
        if (before.isCancelled()) return false;
        if (super.add(element)) {
            onElementAddition(element);
            callEvent(new ManagerElementRegisterEvent.After<>(this, managedType, element));
            return true;
        }
        return false;
    }

    /**
     * This method is called when the {@link #add(Labeled)}} execution was successful,
     * just before the after event is called.
     * <p>
     * You can override this method to perform any extra operation when an element is added.
     *
     * @param type the element that was added.
     */
    @SuppressWarnings("EmptyMethod")
    protected void onElementAddition(Type type) {
    }


    /**
     * Attempts to unregister the given element.
     * If the element is null this method throws a {@link NullPointerException}.
     * If the element type is invalid this method returns {@code false}.
     * If the element is not present or the unregister event is cancelled this method returns {@code false}.
     * If the operation was successful the method returns {@code true}.
     *
     * @param o the element to unregister.
     * @return whether the operation was successful.
     * @throws NullPointerException when the given element is null.
     * @see HashSet#remove(Object)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean remove(Object o) {
        Validate.notNull(o, "The element cannot be null!");
        if (!loaded) loadPanic();
        if (!contains(o)) return false;
        try {
            var before =
                    callEvent(new ManagerElementUnregisterEvent.Before<>(this, managedType, (Type) o));
            if (before.isCancelled()) return false;
            if (super.remove(o)) {
                onElementRemoval((Type) o);
                callEvent(new ManagerElementUnregisterEvent.After<>(this, managedType, (Type) o));
                return true;
            }
            return false;
        } catch (ClassCastException ex) {
            return false;
        }
    }

    /**
     * This method is called when the {@link #remove(Object)}} execution was successful,
     * just before the after event is called.
     * <p>
     * You can override this method to perform any extra operation when an element is removed.
     *
     * @param type the element that was added.
     */
    protected void onElementRemoval(Type type) {
    }

    /**
     * This method is called on construction.
     * Implement this method to add all default values of this manager.
     */
    protected abstract void loadDefaultElements();

    protected void loadPanic() {
        throw new IllegalStateException("Manager " + this + " is not loaded!");
    }

    //region BROADCAST

    @Override
    public boolean registerListener(Object instance, Method method, boolean useWeakReferences) {
        return broadcast.registerListener(instance, method, useWeakReferences);
    }

    @Override
    public int registerListeners(Object instance, boolean useWeakReferences) {
        return broadcast.registerListeners(instance, useWeakReferences);
    }

    @Override
    public boolean unregisterListener(Object instance, Method method) {
        return broadcast.unregisterListener(instance, method);
    }

    @Override
    public int unregisterListeners(Object instance) {
        return broadcast.unregisterListeners(instance);
    }

    @Override
    public <T extends Event> T callEvent(T event) {
        return broadcast.callEvent(event, this);
    }

    @Override
    public void transferListenersTo(EventBroadcast broadcast) {
        this.broadcast.transferListenersTo(broadcast);
    }

    //endregion
}
