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
import net.jamsimulator.jams.mips.memory.MemorySection;

import java.util.Optional;

/**
 * Represents an event invoked when a halfword is stored in a {@link Memory}.
 */
public class MemoryHalfwordSetEvent extends MemoryEvent {

    protected int address;
    protected short value;
    protected boolean cancelled;

    private MemoryHalfwordSetEvent(Memory memory, int address, short value) {
        super(memory);
        this.address = address;
        this.value = value;
        this.cancelled = false;
    }

    public int getAddress() {
        return address;
    }

    public short getValue() {
        return value;
    }

    /**
     * This event is invoked before a halfword is stored in a {@link Memory}.
     */
    public static class Before extends MemoryHalfwordSetEvent implements Cancellable {

        public Before(Memory memory, int address, short value) {
            super(memory, address, value);
        }

        public void setAddress(int address) {
            this.address = address;
        }

        public void setValue(short value) {
            this.value = value;
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
     * This event is invoked after a halfword is stored in a {@link Memory}.
     */
    public static class After extends MemoryHalfwordSetEvent {

        private final MemorySection memorySection;
        private final short oldValue;

        public After(Memory memory, MemorySection memorySection, int address, short value, short oldValue) {
            super(memory, address, value);
            this.memorySection = memorySection;
            this.oldValue = oldValue;
        }

        public Optional<MemorySection> getMemorySection() {
            return Optional.ofNullable(memorySection);
        }

        public short getOldValue() {
            return oldValue;
        }
    }
}
