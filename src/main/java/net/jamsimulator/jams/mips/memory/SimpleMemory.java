package net.jamsimulator.jams.mips.memory;

import net.jamsimulator.jams.event.EventBroadcast;
import net.jamsimulator.jams.event.SimpleEventBroadcast;
import net.jamsimulator.jams.mips.memory.event.ByteGetEvent;
import net.jamsimulator.jams.mips.memory.event.ByteSetEvent;
import net.jamsimulator.jams.mips.memory.event.WordGetEvent;
import net.jamsimulator.jams.mips.memory.event.WordSetEvent;
import net.jamsimulator.jams.utils.Validate;

import java.util.*;

/**
 * Represents a simple memory. A simple memory is formed by several {@link MemorySection},
 * which are responsible of storing data in their {@link MemoryCell}s.
 * <p>
 * Once created you cannot add or remove {@link MemorySection}s.
 *
 * @see EventBroadcast
 * @see MemorySection
 * @see MemoryCell
 */
public class SimpleMemory extends SimpleEventBroadcast implements Memory {


	protected Map<String, MemorySection> sections;
	protected boolean bigEndian;
	private int firstTextAddress, firstDataAddress, firstKernelTextAddress, firstKernelDataAddress, firstExternalAddress;

	/**
	 * Creates a simple memory using a list of {@link MemorySection}s and a boolean representing whether
	 * this memory is bigEndian.
	 *
	 * @param sections  the {@link MemorySection}s.
	 * @param bigEndian whether this memory is big endian.
	 */
	public SimpleMemory(List<MemorySection> sections, boolean bigEndian, int firstTextAddress, int firstDataAddress,
						int firstKernelTextAddress, int firstKernelDataAddress, int firstExternalAddress) {
		Validate.notNull(sections, "There must be at least one memory section!");
		Validate.isTrue(!sections.isEmpty(), "There must be at least one memory section!");
		this.sections = new HashMap<>();
		this.bigEndian = bigEndian;

		this.firstTextAddress = firstTextAddress;
		this.firstDataAddress = firstDataAddress;
		this.firstKernelTextAddress = firstKernelTextAddress;
		this.firstKernelDataAddress = firstKernelDataAddress;
		this.firstExternalAddress = firstExternalAddress;

		sections.forEach(target -> this.sections.put(target.getName(), target));
	}

	/**
	 * Creates a simple memory using an array of {@link MemorySection}s and a boolean representing whether
	 * this memory is bigEndian.
	 *
	 * @param sections  the {@link MemorySection}s.
	 * @param bigEndian whether this memory is big endian.
	 */
	public SimpleMemory(boolean bigEndian, int firstTextAddress, int firstDataAddress, int firstKernelTextAddress,
						int firstKernelDataAddress, int firstExternalAddress, MemorySection... sections) {
		Validate.isTrue(sections.length > 0, "There must be at least one memory section!");
		this.bigEndian = bigEndian;
		this.sections = new HashMap<>();

		this.firstTextAddress = firstTextAddress;
		this.firstDataAddress = firstDataAddress;
		this.firstKernelTextAddress = firstKernelTextAddress;
		this.firstKernelDataAddress = firstKernelDataAddress;
		this.firstExternalAddress = firstExternalAddress;

		for (MemorySection section : sections) {
			this.sections.put(section.getName(), section);
		}
	}

	protected SimpleMemory(Map<String, MemorySection> sections, boolean bigEndian, int firstTextAddress,
						   int firstDataAddress, int firstKernelTextAddress, int firstKernelDataAddress, int firstExternalAddress) {
		Validate.notNull(sections, "There must be at least one memory section!");
		Validate.isTrue(!sections.isEmpty(), "There must be at least one memory section!");
		this.sections = sections;
		this.bigEndian = bigEndian;

		this.firstTextAddress = firstTextAddress;
		this.firstDataAddress = firstDataAddress;
		this.firstKernelTextAddress = firstKernelTextAddress;
		this.firstKernelDataAddress = firstKernelDataAddress;
		this.firstExternalAddress = firstExternalAddress;
	}

	/**
	 * Returns a immutable list with all memory sections.
	 *
	 * @return thee immutable list.
	 */
	public List<MemorySection> getSections() {
		return new ArrayList<>(sections.values());
	}

	@Override
	public boolean isBigEndian() {
		return bigEndian;
	}

	@Override
	public void setBigEndian(boolean bigEndian) {
		this.bigEndian = bigEndian;
	}

	@Override
	public byte getByte(int address) {
		//Invokes the before event.
		ByteGetEvent.Before before = callEvent(new ByteGetEvent.Before(this, address));

		//Refresh data.
		address = before.getAddress();

		//Gets the section and the byte.
		MemorySection section = getSectionOrThrowException(address);
		byte b = section.getByte(address);

		//Invokes the after event.
		return callEvent(new ByteGetEvent.After(this, section, address, b)).getValue();
	}

	@Override
	public void setByte(int address, byte b) {
		//Invokes the before event.
		ByteSetEvent.Before before = callEvent(new ByteSetEvent.Before(this, address, b));
		if (before.isCancelled()) return;

		//Refresh data.
		address = before.getAddress();
		b = before.getValue();

		//Gets the section and sets the byte.
		MemorySection section = getSectionOrThrowException(address);
		section.setByte(address, b);

		//Invokes the after event.
		callEvent(new ByteSetEvent.After(this, section, address, b));
	}

	@Override
	public int getWord(int address) {
		//Invokes the before event.
		WordGetEvent.Before before = callEvent(new WordGetEvent.Before(this, address));

		//Refresh data.
		address = before.getAddress();

		if (address % 4 != 0) throw new IllegalArgumentException("Address " + address + " is not aligned.");

		//Gets the section and the word.
		MemorySection section = getSectionOrThrowException(address);
		int word = section.getWord(address, bigEndian);

		//Invokes the after event.
		return callEvent(new WordGetEvent.After(this, section, address, word)).getValue();
	}

	@Override
	public void setWord(int address, int word) {
		//Invokes the before event.
		WordSetEvent.Before before = callEvent(new WordSetEvent.Before(this, address, word));
		if (before.isCancelled()) return;

		//Refresh data.
		address = before.getAddress();
		word = before.getValue();

		if (address % 4 != 0) throw new IllegalArgumentException("Address " + address + " is not aligned.");

		//Gets the section and sets the word.
		MemorySection section = getSectionOrThrowException(address);
		section.setWord(address, word, bigEndian);

		//Invokes the after event.
		callEvent(new WordSetEvent.After(this, section, address, word));
	}

	@Override
	public int getFirstTextAddress() {
		return firstTextAddress;
	}

	@Override
	public int getFirstDataAddress() {
		return firstDataAddress;
	}

	@Override
	public int getFirstKernelTextAddress() {
		return firstKernelTextAddress;
	}

	@Override
	public int getFirstKernelDataAddress() {
		return firstKernelDataAddress;
	}

	@Override
	public int getFirstExternalAddress() {
		return firstExternalAddress;
	}

	@Override
	public Memory copy() {
		HashMap<String, MemorySection> sections = new HashMap<>();
		this.sections.forEach((name, section) -> sections.put(name, section.copy()));
		return new SimpleMemory(sections, bigEndian, firstTextAddress, firstDataAddress, firstKernelTextAddress, firstKernelDataAddress, firstExternalAddress);
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
