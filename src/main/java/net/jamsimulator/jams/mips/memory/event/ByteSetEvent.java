package net.jamsimulator.jams.mips.memory.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.MemorySection;

/**
 * Represents a event invoked when a byte is stored in a {@link Memory}.
 */
public class ByteSetEvent extends MemoryEvent {

	protected int address;
	protected byte value;
	protected boolean cancelled;

	private ByteSetEvent(Memory memory, int address, byte value) {
		super(memory);
		this.address = address;
		this.value = value;
		this.cancelled = false;
	}

	public int getAddress() {
		return address;
	}

	public byte getValue() {
		return value;
	}

	/**
	 * This event is invoked before the a byte is stored in a {@link Memory}.
	 */
	public static class Before extends ByteSetEvent implements Cancellable {

		public Before(Memory memory, int address, byte value) {
			super(memory, address, value);
		}

		public void setAddress(int address) {
			this.address = address;
		}

		public void setValue(byte value) {
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
	 * This event is invoked after a byte is stored in a {@link Memory}.
	 */
	public static class After extends ByteSetEvent {

		private MemorySection memorySection;

		public After(Memory memory, MemorySection memorySection, int address, byte value) {
			super(memory, address, value);
			this.memorySection = memorySection;
		}

		public MemorySection getMemorySection() {
			return memorySection;
		}
	}
}
