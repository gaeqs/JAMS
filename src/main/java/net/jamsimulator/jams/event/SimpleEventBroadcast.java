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

package net.jamsimulator.jams.event;

import java.lang.reflect.Method;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Represents a simple event caller. An event caller allows to send
 * events to all the registered {@link Listener} the caller has.
 * <p>
 * To register a listener use {@link #registerListener(Object, Method, boolean)} or {@link #registerListeners(Object, boolean)}.
 * To send an {@link Event} use {@link #callEvent(Event)}.
 * <p>
 * {@link Listener}s listening a superclass of the {@link Event} will also be called.
 * <p>
 * This broadcast is thread-safe: you can register/unregister listeners and call events using several threads.
 */
public class SimpleEventBroadcast implements EventBroadcast {

    private final SortedSet<ListenerMethod> registeredListeners;

    /**
     * Creates an event caller.
     */
    @SuppressWarnings("ComparatorMethodParameterNotUsed")
    public SimpleEventBroadcast() {
        registeredListeners = new ConcurrentSkipListSet<>(((o1, o2) -> {
            int val = o2.getListener().priority() - o1.getListener().priority();
            //Avoids override. Listeners registered first have priority.
            return val == 0 ? -1 : val;
        }));
    }

    @SuppressWarnings("unchecked")
    public boolean registerListener(Object instance, Method method, boolean useWeakReferences) {
        if (method.getParameterCount() != 1) return false;
        var clazz = method.getParameters()[0].getType();
        if (!Event.class.isAssignableFrom(clazz)) return false;
        var type = (Class<? extends Event>) clazz;

        var annotations = method.getAnnotationsByType(Listener.class);
        if (annotations.length != 1) return false;
        var annotation = annotations[0];
        method.setAccessible(true);

        registeredListeners.add(new ListenerMethod(instance, method, type, annotation, useWeakReferences));
        return true;
    }

    public int registerListeners(Object instance, boolean useWeakReferences) {
        int amount = 0;

        Class<?> c = instance.getClass();
        while (c != null) {
            for (Method declaredMethod : c.getDeclaredMethods()) {
                if (registerListener(instance, declaredMethod, useWeakReferences))
                    amount++;
            }
            c = c.getSuperclass();
        }
        return amount;
    }

    @Override
    public boolean unregisterListener(Object instance, Method method) {
        if (method.getParameterCount() != 1) return false;
        var clazz = method.getParameters()[0].getType();
        if (!Event.class.isAssignableFrom(clazz)) return false;
        if (method.getAnnotationsByType(Listener.class).length != 1) return false;
        return registeredListeners.removeIf(it -> it.isReferenceInvalid() || it.matches(instance, method));
    }

    @Override
    public int unregisterListeners(Object instance) {
        int amount = 0;
        for (Method declaredMethod : instance.getClass().getDeclaredMethods()) {
            if (unregisterListener(instance, declaredMethod))
                amount++;
        }
        return amount;
    }

    @Override
    public <T extends Event> T callEvent(T event) {
        return callEvent(event, this);
    }


    /**
     * Calls the given event setting the caller as the given broadcast.
     * This is useful for broadcasts that can't extend this class and use
     * it as a parameter.
     *
     * @param event     the event to call.
     * @param broadcast the broadcast.
     * @param <T>       the type of event.
     * @return the given event.
     * @see #callEvent(Event)
     */
    public <T extends Event> T callEvent(T event, EventBroadcast broadcast) {
        event.setCaller(broadcast);
        var iterator = registeredListeners.iterator();

        while (iterator.hasNext()) {
            var method = iterator.next();
            if (method.isReferenceInvalid()) iterator.remove();
            else if ((method.ignoresCancelledEvents() || !(event instanceof Cancellable c) || !c.isCancelled())
                    && method.getEvent().isInstance(event)
                    && event.suportsGenerics(method.getGenerics())) {
                method.call(event);
            }
        }

        return event;
    }

    /**
     * Removes all listeners from this broadcast.
     */
    public void clear() {
        registeredListeners.clear();
    }

}
