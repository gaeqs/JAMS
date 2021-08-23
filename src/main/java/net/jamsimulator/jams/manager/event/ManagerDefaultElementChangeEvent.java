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

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.utils.Labeled;
import net.jamsimulator.jams.manager.Manager;
import net.jamsimulator.jams.utils.Validate;

public class ManagerDefaultElementChangeEvent<Type extends Labeled> extends ManagerEvent<Type> {

    protected final Type oldElement;
    protected Type newElement;

    private ManagerDefaultElementChangeEvent(Manager<Type> manager, Class<Type> type, Type oldElement, Type newElement) {
        super(manager, type);
        this.oldElement = oldElement;
        this.newElement = newElement;
    }

    public Type getOldElement() {
        return oldElement;
    }

    public Type getNewElement() {
        return newElement;
    }

    public static class Before<E extends Labeled> extends ManagerDefaultElementChangeEvent<E> implements Cancellable {

        private boolean cancelled = false;

        public Before(Manager<E> manager, Class<E> type, E oldElement, E newElement) {
            super(manager, type, oldElement, newElement);
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        public void setNewElement(E element) {
            Validate.isTrue(manager.contains(element), "The new element must be inside the manager!");
            this.newElement = element;
        }
    }

    public static class After<E extends Labeled> extends ManagerDefaultElementChangeEvent<E> {
        public After(Manager<E> manager, Class<E> type, E oldElement, E newElement) {
            super(manager, type, oldElement, newElement);
        }
    }
}
