package net.jamsimulator.jams.mips.memory;

import net.jamsimulator.jams.utils.Validate;

import java.util.*;

/**
 * Represents a memory from a simulator. A memory is formed by several {@link MemorySection},
 * which are responsible of storing data in their {@link MemoryCell}s.
 * <p>
 * Once creates you cannot remove or create {@link MemorySection}s.
 *
 * @see MemorySection
 * @see MemoryCell
 */
public class Memory {

    private Map<String, MemorySection> sections;
    private boolean bigEndian;

    /**
     * Creates a memory using a list of {@link MemorySection}s and a boolean representing whether
     * this memory is bigEndian.
     *
     * @param sections  the {@link MemorySection}s.
     * @param bigEndian whether this memory is big endian.
     */
    public Memory(List<MemorySection> sections, boolean bigEndian) {
        Validate.notNull(sections, "There must be at least one memory section!");
        Validate.isTrue(!sections.isEmpty(), "There must be at least one memory section!");
        this.sections = new HashMap<>();
        this.bigEndian = bigEndian;

        sections.forEach(target -> this.sections.put(target.getName(), target));
    }

    /**
     * Creates a memory using an array of {@link MemorySection}s and a boolean representing whether
     * this memory is bigEndian.
     *
     * @param sections  the {@link MemorySection}s.
     * @param bigEndian whether this memory is big endian.
     */
    public Memory(boolean bigEndian, MemorySection... sections) {
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

    /**
     * Returns whether this memory is big endian.
     * This value can be changed on runtime using {@link #setBigEndian(boolean)}.
     *
     * @return true if this memory is big endian. False if this memory is little endian.
     */
    public boolean isBigEndian() {
        return bigEndian;
    }

    /**
     * Sets the endianness of the memory. If the given boolean is true the memory
     * will behave as a big endian memory. Else it will behave as a little endian memory.
     *
     * @param bigEndian whether the memory should behave as a big endian memory.
     */
    public void setBigEndian(boolean bigEndian) {
        this.bigEndian = bigEndian;
    }

    /**
     * Returns the byte stored in the given address.
     *
     * @param address the address.
     * @return the byte.
     * @throws IndexOutOfBoundsException if no section contains the address.
     */
    public byte getByte(int address) {
        return getSectionOrThrowException(address).getByte(address);
    }

    /**
     * Stores the given byte into the given address.
     *
     * @param address the address.
     * @param b       the byte.
     * @throws IndexOutOfBoundsException if no section contains the address.
     */
    public void setByte(int address, byte b) {
        getSectionOrThrowException(address).setByte(address, b);
    }

    /**
     * Returns the word stored in the given address.
     *
     * @param address the address.
     * @return the word.
     * @throws IllegalArgumentException  if the address is not aligned to words.
     * @throws IndexOutOfBoundsException if no section contains the address.
     */
    public int getWord(int address) {
        if (address % 4 != 0) throw new IllegalArgumentException("Address " + address + " is not aligned.");
        return getSectionOrThrowException(address).getWord(address, bigEndian);
    }

    /**
     * Stores the given word into the given address.
     *
     * @param address the address.
     * @param word    the word.
     * @throws IllegalArgumentException  if the address is not aligned to words.
     * @throws IndexOutOfBoundsException if no section contains the address.
     */
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
