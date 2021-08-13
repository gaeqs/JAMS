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

/**
 * Represents an event invoked when the endianness of a {@link Memory} changes.
 */
public class MemoryEndiannessChange extends MemoryEvent {

    protected boolean newEndiannessBigEndian;

    private MemoryEndiannessChange(Memory memory, boolean newEndiannessBigEndian) {
        super(memory);
        this.newEndiannessBigEndian = newEndiannessBigEndian;
    }

    public boolean isNewEndiannessBigEndian() {
        return newEndiannessBigEndian;
    }

    /**
     * This event is invoked before the endianness of a  {@link Memory} changes.
     */
    public static class Before extends MemoryEndiannessChange implements Cancellable {

        private boolean cancelled;

        public Before(Memory memory, boolean newEndianness) {
            super(memory, newEndianness);
        }

        public void setNewEndiannessBigEndian(boolean bigEndian) {
            this.newEndiannessBigEndian = bigEndian;
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

    /**
     * This event is invoked after the endianness of a {@link Memory} changes.
     */
    public static class After extends MemoryEndiannessChange {

        public After(Memory memory, boolean newEndianness) {
            super(memory, newEndianness);
        }
    }
}
