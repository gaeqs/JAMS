package net.jamsimulator.jams.mips.memory.event;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.MemorySection;

/**
 * Represents a event invoked when a word is get from a {@link Memory}.
 */
public class WordGetEvent extends MemoryEvent {

	protected int address;

	private WordGetEvent(Memory memory, int address) {
		super(memory);
		this.address = address;
	}

	public int getAddress() {
		return address;
	}

	/**
	 * This event is invoked before a word is get from a memory.
	 */
	public static class Before extends WordGetEvent {

		public Before(Memory memory, int address) {
			super(memory, address);
		}

		public void setAddress(int address) {
			this.address = address;
		}
	}

	/**
	 * This event is invoked after a word is get from a memory.
	 */
	public static class After extends WordGetEvent {

		private int value;
		private MemorySection memorySection;

		public After(Memory memory, MemorySection memorySection, int address, int value) {
			super(memory, address);
			this.value = value;
			this.memorySection = memorySection;
		}

		public MemorySection getMemorySection() {
			return memorySection;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}
	}
}
