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

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class ListenerMethod {

    private static final AtomicLong ID_GENERATOR = new AtomicLong();

    private final long id = ID_GENERATOR.getAndIncrement();
    private final Class<? extends Event> event;
    private final boolean weakReference;

    private final Method method;
    private final Listener listener;
    private final boolean ignoreCancelled;
    private final Type[] generics;

    private final Object instance;
    private final WeakReference<Object> instanceWeakReference;

    ListenerMethod(Object instance, Method method, Class<? extends Event> event, Listener listener, boolean weakReference) {
        this.method = method;
        this.event = event;
        this.listener = listener;
        this.ignoreCancelled = listener.ignoreCancelled();
        this.weakReference = weakReference;

        if (weakReference) {
            this.instanceWeakReference = new WeakReference<>(instance);
            this.instance = null;
        } else {
            this.instance = instance;
            this.instanceWeakReference = null;
        }


        if (method.getGenericParameterTypes()[0] instanceof ParameterizedType parametrized) {
            generics = parametrized.getActualTypeArguments();
        } else {
            generics = new Type[0];
        }

    }

    public long getId() {
        return id;
    }

    Class<? extends Event> getEvent() {
        return event;
    }

    Listener getListener() {
        return listener;
    }

    boolean ignoresCancelledEvents() {
        return ignoreCancelled;
    }

    Method getMethod() {
        return method;
    }

    public Type[] getGenerics() {
        return generics;
    }

    boolean matches(Object instance, Method method) {
        //We want to check that it's the same instance, not an equivalent one.
        if (weakReference) {
            return this.method.equals(method) && instance == this.instanceWeakReference.get();
        }
        return this.method.equals(method) && instance == this.instance;
    }

    boolean isReferenceInvalid() {
        return weakReference && instanceWeakReference.get() == null;
    }

    void call(Event event) {
        try {
            if (weakReference) {
                var instance = instanceWeakReference.get();
                if (instance != null) {
                    method.invoke(instance, event);
                }
            } else {
                method.invoke(instance, event);
            }
        } catch (Exception e) {
            System.err.println("Error while calling listener " + method.getName() + "!");
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "ListenerMethod{" +
                "method=" + method +
                ", instance=" + instance +
                '}';
    }
}
