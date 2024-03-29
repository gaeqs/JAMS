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

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.MemorySection;

import java.util.Optional;

/**
 * Represents an event invoked when a byte is get from a {@link Memory}.
 */
public class MemoryByteGetEvent extends MemoryEvent {

    protected int address;

    private MemoryByteGetEvent(Memory memory, int address) {
        super(memory);
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    /**
     * This event is invoked before a byte is get from a memory.
     */
    public static class Before extends MemoryByteGetEvent {

        public Before(Memory memory, int address) {
            super(memory, address);
        }

        public void setAddress(int address) {
            this.address = address;
        }
    }

    /**
     * This event is invoked after a byte is get from a memory.
     */
    public static class After extends MemoryByteGetEvent {

        private byte value;
        private final MemorySection memorySection;

        public After(Memory memory, MemorySection memorySection, int address, byte value) {
            super(memory, address);
            this.value = value;
            this.memorySection = memorySection;
        }

        public Optional<MemorySection> getMemorySection() {
            return Optional.ofNullable(memorySection);
        }

        public byte getValue() {
            return value;
        }

        public void setValue(byte value) {
            this.value = value;
        }
    }
}
