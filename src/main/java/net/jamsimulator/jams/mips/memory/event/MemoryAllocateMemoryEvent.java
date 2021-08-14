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

package net.jamsimulator.jams.mips.memory.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.mips.memory.Memory;

public class MemoryAllocateMemoryEvent extends MemoryEvent {

    protected int oldCurrentData, first, length;

    private MemoryAllocateMemoryEvent(Memory memory, int oldCurrentData, int first, int length) {
        super(memory);
        this.oldCurrentData = oldCurrentData;
        this.first = first;
        this.length = length;
    }

    public int getOldCurrentData() {
        return oldCurrentData;
    }

    public int getNewCurrentData() {
        return first + length;
    }

    public int getFirst() {
        return first;
    }

    public int getLength() {
        return length;
    }

    public static class Before extends MemoryAllocateMemoryEvent implements Cancellable {

        private boolean cancelled;

        public Before(Memory memory, int oldCurrentData, int first, int length) {
            super(memory, oldCurrentData, first, length);
        }

        public void setLength(int length) {
            this.length = length;
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


    public static class After extends MemoryAllocateMemoryEvent {

        public After(Memory memory, int oldCurrentData, int first, int length) {
            super(memory, oldCurrentData, first, length);
        }
    }
}
