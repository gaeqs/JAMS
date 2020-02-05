package net.jamsimulator.jams.mips.memory.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.MemorySection;

/**
 * Represents a event invoked when a word is stored in a {@link Memory}.
 */
public class WordSetEvent extends MemoryEvent {

	protected int address;
	protected int value;
	protected boolean cancelled;

	private WordSetEvent(Memory memory, int address, int value) {
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
	 * This event is invoked before the a word is stored in a {@link Memory}.
	 */
	public static class Before extends WordSetEvent implements Cancellable {

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
	public static class After extends WordSetEvent {

		private MemorySection memorySection;

		public After(Memory memory, MemorySection memorySection, int address, int value) {
			super(memory, address, value);
			this.memorySection = memorySection;
		}

		public MemorySection getMemorySection() {
			return memorySection;
		}
	}
}
