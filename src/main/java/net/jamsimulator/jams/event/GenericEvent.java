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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

/**
 * Represents an {@link Event} with support for a generic value.
 *
 * <h2>Support:</h2>
 * <p>
 * This implementation only supports classes and interfaces as their types.
 * You cannot create a {@code GenericEvent<List<String>>}: you have to create a {@code GenericEvent<List>}.
 * Try not to use GenericEvent with generic elements; instead, create a new event.
 *
 * <h2>You can create the following listeners</h2>:
 *
 * <h3>{@code @Listener void myListener (MyGenericEvent<Foo>) {}}</h3>
 * Valid. Will only be called when MyGenericEvent has Foo as the type.
 * <p>
 * <p>
 * <h3>{@code @Listener void myListener (MyGenericEvent<? extends Foo>) {}}</h3>
 * Valid. Will only be called when MyGenericEvent has Foo or a subclass of Foo as the type.
 * <p>
 * <p>
 * <h3>{@code @Listener void myListener (MyGenericEvent<? super Foo>) {}}</h3>
 * Valid. Will only be called when MyGenericEvent has Foo or a superclass of Foo as the type.
 * <p>
 * <p>
 * <h3>{@code @Listener void myListener (MyGenericEvent<Foo<Bar>>) {}}</h3>
 * WARNING! Only Foo is checked. The listener will be invoked with any {@code Foo<T>}.
 * <p>
 * You can use {@code @Listener void myListener (MyGenericEvent<Foo<?>>) {}} without problems.
 *
 * @param <E> the type of the element.
 */
public class GenericEvent<E> extends Event {

    public Class<?> type;

    /**
     * Creates an event.
     * Send it to listeners through {@link EventBroadcast#callEvent(Event)}.
     */
    public GenericEvent(Class<E> type) {
        this.type = type;
    }

    /**
     * Returns the generic type of this event.
     *
     * @return the generic type.
     */
    public Class<?> getType() {
        return type;
    }

    @Override
    protected boolean suportsGenerics(Type[] generics) {
        // Raw type
        if (generics.length == 0) return true;
        if (generics.length > 1) return false;
        var first = generics[0];
        if (first.equals(type)) return true;

        if (first instanceof WildcardType wildcard) {
            return validLowerBounds(wildcard.getLowerBounds(), type)
                    && validUpperBounds(wildcard.getUpperBounds(), type);
        }

        if(first instanceof ParameterizedType parameterized) {
            return type.equals(parameterized.getRawType());
        }

        return false;
    }

    private static boolean validUpperBounds(Type[] bounds, Type managed) {
        if (bounds.length == 0) return true;
        if (bounds.length > 1) return false;
        return managed instanceof Class<?> mc && bounds[0] instanceof Class<?> tc && tc.isAssignableFrom(mc);
    }

    private static boolean validLowerBounds(Type[] bounds, Type managed) {
        if (bounds.length == 0) return true;
        if (bounds.length > 1) return false;
        return managed instanceof Class<?> mc && bounds[0] instanceof Class<?> tc && mc.isAssignableFrom(tc);
    }
}
