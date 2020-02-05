package net.jamsimulator.jams.mips.memory.event;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.MemorySection;

/**
 * Represents a event invoked when a byte is get from a {@link Memory}.
 */
public class ByteGetEvent extends MemoryEvent {

	protected int address;

	private ByteGetEvent(Memory memory, int address) {
		super(memory);
		this.address = address;
	}

	public int getAddress() {
		return address;
	}

	/**
	 * This event is invoked before a byte is get from a memory.
	 */
	public static class Before extends ByteGetEvent {

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
	public static class After extends ByteGetEvent {

		private byte value;
		private MemorySection memorySection;

		public After(Memory memory, MemorySection memorySection, int address, byte value) {
			super(memory, address);
			this.value = value;
			this.memorySection = memorySection;
		}

		public MemorySection getMemorySection() {
			return memorySection;
		}

		public byte getValue() {
			return value;
		}

		public void setValue(byte value) {
			this.value = value;
		}
	}
}
