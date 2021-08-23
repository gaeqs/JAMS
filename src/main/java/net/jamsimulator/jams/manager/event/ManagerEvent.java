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

package net.jamsimulator.jams.manager.event;

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.manager.Labeled;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.utils.Validate;

import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

public class ManagerEvent<T extends Labeled> extends Event {

    protected final Manager<T> manager;

    public ManagerEvent(Manager<T> manager) {
        Validate.notNull(manager, "Manager cannot be null!");
        this.manager = manager;
    }

    public Manager<T> getManager() {
        return manager;
    }

    @Override
    protected boolean suportsGenerics(Type[] generics) {
        // Raw type
        if (generics.length == 0) return true;
        if (generics.length > 1) return false;
        var type = generics[0];
        var managed = manager.getManagedType();
        if (type.equals(managed)) return true;

        return type instanceof WildcardType wildcard
                && wildcard.getLowerBounds().length == 0
                && validUpperBounds(wildcard.getUpperBounds(), managed);
    }

    private boolean validUpperBounds(Type[] bounds, Type managed) {
        return bounds.length == 0
                || bounds.length <= 1 && (bounds[0].equals(Object.class)
                || managed instanceof Class<?> mc && bounds[0] instanceof Class<?> tc && tc.isAssignableFrom(mc));
    }
}
