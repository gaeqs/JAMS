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
import net.jamsimulator.jams.manager.Labeled;
import net.jamsimulator.jams.manager.Manager;

public class ManagerElementRegisterEvent<Type extends Labeled> extends ManagerEvent<Type> {

    protected final Type element;

    private ManagerElementRegisterEvent(Manager<Type> manager, Type element) {
        super(manager);
        this.element = element;
    }

    public Type getElement() {
        return element;
    }

    public static class Before<Type extends Labeled> extends ManagerElementRegisterEvent<Type> implements Cancellable {

        private boolean cancelled = false;

        public Before(Manager<Type> manager, Type element) {
            super(manager, element);
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    public static class After<Type extends Labeled> extends ManagerElementRegisterEvent<Type> {

        public After(Manager<Type> manager, Type element) {
            super(manager, element);
        }
    }
}
