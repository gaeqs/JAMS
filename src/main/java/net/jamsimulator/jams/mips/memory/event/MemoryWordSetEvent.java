/*
 * MIT License
 *
 * Copyright (c) 2020 Gael Rial Costas
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.jamsimulator.jams.mips.memory.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.MemorySection;

/**
 * Represents a event invoked when a word is stored in a {@link Memory}.
 */
public class MemoryWordSetEvent extends MemoryEvent {

	protected int address;
	protected int value;
	protected boolean cancelled;

	private MemoryWordSetEvent(Memory memory, int address, int value) {
		super(memory);
		this.address = address;
		this.value = value;
		this.cancelled = false;
	}

	public int getAddress() {
		return address;
	}

	public int getValue() {
		return value;
	}

	/**
	 * This event is invoked before a word is stored in a {@link Memory}.
	 */
	public static class Before extends MemoryWordSetEvent implements Cancellable {

		public Before(Memory memory, int address, int value) {
			super(memory, address, value);
		}

		public void setAddress(int address) {
			this.address = address;
		}

		public void setValue(int value) {
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
	 * This event is invoked after a word is stored in a {@link Memory}.
	 */
	public static class After extends MemoryWordSetEvent {

		private final MemorySection memorySection;
		private final int oldValue;

		public After(Memory memory, MemorySection memorySection, int address, int value, int oldValue) {
			super(memory, address, value);
			this.memorySection = memorySection;
			this.oldValue = oldValue;
		}

		public MemorySection getMemorySection() {
			return memorySection;
		}

		public int getOldValue() {
			return oldValue;
		}
	}
}
