/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.event;

import java.lang.reflect.Method;

/**
 * Represents an event broadcast. An event broadcast allows to send
 * events to all the registered {@link Listener} the broadcast has.
 * <p>
 * To register a listener use {@link #registerListener(Object, Method, boolean)} or {@link #registerListeners(Object, boolean)}.
 * To send an {@link Event} use {@link #callEvent(Event)}.
 * <p>
 * {@link Listener}s listening a superclass of the {@link Event} will also be called.
 */
public interface EventBroadcast {

    /**
     * Registers a listener of an instance. A listener is a non-static method with
     * only one parameter. This parameter must be a {@link Event} or any subclass.
     * The method must also have one {@link Listener} annotation.
     * <p>
     * <p>
     * If {@code weakReference} is true the instance will be store in a weak reference instead of a normal parameter.
     * This is useful if you want to register this such as buttons, or labels, allowing the GC to free memory easily
     * when those elements are no longer referenced in your code.
     * Set this boolean to {@code false} if the only reference of the instance is hold by this broadcast.
     *
     * @param instance          the instance.
     * @param method            the listener.
     * @param useWeakReferences whether the broadcast should use weak references to register the listener.
     * @return true whether the listener was registered.
     */
    boolean registerListener(Object instance, Method method, boolean useWeakReferences);

    /**
     * Registers all listeners of an instance.
     * <p>
     * This searches listener on all methods, including private ones. This also searches all superclasses' methods.
     * <p>
     * <p>
     * If {@code weakReference} is true the instance will be store in a weak reference instead of a normal parameter.
     * This is useful if you want to register this such as buttons, or labels, allowing the GC to free memory easily
     * when those elements are no longer referenced in your code.
     * Set this boolean to {@code false} if the only reference of the instance is hold by this broadcast.
     * <p>
     * See {@link #registerListener(Object, Method, boolean)} for more information.
     *
     * @param instance          the instance.
     * @param useWeakReferences whether the broadcast should use weak references to register the listeners.
     * @return the amount of registered listeners.
     * @see #registerListener(Object, Method, boolean)
     */
    int registerListeners(Object instance, boolean useWeakReferences);

    /**
     * Unregisters a listener of an instance.
     *
     * @param instance the instance.
     * @param method   the listener.
     * @return whether the listener was unregistered.
     */
    boolean unregisterListener(Object instance, Method method);

    /**
     * Unregisters all listeners of an instance.
     *
     * @param instance the instance.
     * @return the amount of unregistered listeners.
     */
    int unregisterListeners(Object instance);

    /**
     * Calls all listeners compatible with the given {@link Event}.
     * <p>
     * Events must match the same class. If it matches any superclass the event won't be invoked.
     *
     * @param event the {@link Event}.
     * @return the {@link Event}.
     */
    <T extends Event> T callEvent(T event);
}
