package net.jamsimulator.jams.mips.memory.event;

import net.jamsimulator.jams.event.Event;
import net.jamsimulator.jams.mips.memory.Memory;

/**
 * Represents a {@link Memory} related event.
 */
public class MemoryEvent extends Event {

	private Memory memory;

	/**
	 * Creates a memory event using a {@link Memory}.
	 *
	 * @param memory the {@link Memory}.
	 */
	public MemoryEvent(Memory memory) {
		this.memory = memory;
	}

	/**
	 * Returns the {@link Memory} of the event.
	 *
	 * @return the {@link Memory}.
	 */
	public Memory getMemory() {
		return memory;
	}
}
