package net.jamsimulator.jams.mips.memory;

import net.jamsimulator.jams.event.SimpleEventCaller;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;

/**
 * Represents a simple memory. A simple memory is formed by several {@link MemorySection},
 * which are responsible of storing data in their {@link MemoryCell}s.
 * <p>
 * Once created you cannot add or remove {@link MemorySection}s.
 *
 * @see net.jamsimulator.jams.event.EventCaller
 * @see MemorySection
 * @see MemoryCell
 */
public class SimpleMemory extends SimpleEventCaller implements Memory {


	private Map<String, MemorySection> sections;
	private boolean bigEndian;

	/**
	 * Creates a simple memory using a list of {@link MemorySection}s and a boolean representing whether
	 * this memory is bigEndian.
	 *
	 * @param sections  the {@link MemorySection}s.
	 * @param bigEndian whether this memory is big endian.
	 */
	public SimpleMemory(List<MemorySection> sections, boolean bigEndian) {
		Validate.notNull(sections, "There must be at least one memory section!");
		Validate.isTrue(!sections.isEmpty(), "There must be at least one memory section!");
		this.sections = new HashMap<>();
		this.bigEndian = bigEndian;

		sections.forEach(target -> this.sections.put(target.getName(), target));
	}

	/**
	 * Creates a simple memory using an array of {@link MemorySection}s and a boolean representing whether
	 * this memory is bigEndian.
	 *
	 * @param sections  the {@link MemorySection}s.
	 * @param bigEndian whether this memory is big endian.
	 */
	public SimpleMemory(boolean bigEndian, MemorySection... sections) {
		Validate.isTrue(sections.length > 0, "There must be at least one memory section!");
		this.bigEndian = bigEndian;
		this.sections = new HashMap<>();
		for (MemorySection section : sections) {
			this.sections.put(section.getName(), section);
		}
	}

	/**
	 * Returns a immutable list with all memory sections.
	 *
	 * @return thee immutable list.
	 */
	public List<MemorySection> getSections() {
		return new ArrayList<>(sections.values());
	}

	public boolean isBigEndian() {
		return bigEndian;
	}

	public void setBigEndian(boolean bigEndian) {
		this.bigEndian = bigEndian;
	}

	public byte getByte(int address) {
		return getSectionOrThrowException(address).getByte(address);
	}

	public void setByte(int address, byte b) {
		getSectionOrThrowException(address).setByte(address, b);
	}

	public int getWord(int address) {
		if (address % 4 != 0) throw new IllegalArgumentException("Address " + address + " is not aligned.");
		return getSectionOrThrowException(address).getWord(address, bigEndian);
	}

	public void setWord(int address, int word) {
		if (address % 4 != 0) throw new IllegalArgumentException("Address " + address + " is not aligned.");
		getSectionOrThrowException(address).setWord(address, word, bigEndian);
	}


	private MemorySection getSectionOrThrowException(int address) {
		//Filters sections
		Optional<MemorySection> optional = sections.values().stream().filter(target -> target.isInside(address)).findAny();
		//If not present throw exception.
		if (!optional.isPresent())
			throw new IndexOutOfBoundsException("Memory section not found for address " + address + ".");
		return optional.get();
	}
}
