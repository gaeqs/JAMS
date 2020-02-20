package net.jamsimulator.jams.mips.memory;

import net.jamsimulator.jams.event.EventBroadcast;

/**
 * Represents a memory from a simulator.
 */
public interface Memory extends EventBroadcast {

	/**
	 * Returns whether this memory is big endian.
	 * This value can be changed on runtime using {@link #setBigEndian(boolean)}.
	 *
	 * @return true if this memory is big endian. False if this memory is little endian.
	 */
	boolean isBigEndian();

	/**
	 * Sets the endianness of the memory. If the given boolean is true the memory
	 * will behave as a big endian memory. Else it will behave as a little endian memory.
	 *
	 * @param bigEndian whether the memory should behave as a big endian memory.
	 */
	void setBigEndian(boolean bigEndian);

	/**
	 * Returns the byte stored in the given address.
	 *
	 * @param address the address.
	 * @return the byte.
	 * @throws IndexOutOfBoundsException if no section contains the address.
	 */
	byte getByte(int address);

	/**
	 * Stores the given byte into the given address.
	 *
	 * @param address the address.
	 * @param b       the byte.
	 * @throws IndexOutOfBoundsException if no section contains the address.
	 */
	void setByte(int address, byte b);

	/**
	 * Returns the word stored in the given address.
	 *
	 * @param address the address.
	 * @return the word.
	 * @throws IllegalArgumentException  if the address is not aligned to words.
	 * @throws IndexOutOfBoundsException if no section contains the address.
	 */
	int getWord(int address);

	/**
	 * Stores the given word into the given address.
	 *
	 * @param address the address.
	 * @param word    the word.
	 * @throws IllegalArgumentException  if the address is not aligned to words.
	 * @throws IndexOutOfBoundsException if no section contains the address.
	 */
	void setWord(int address, int word);


	/**
	 * Returns the first text address assemblers should use.
	 *
	 * @return the first text address.
	 */
	int getFirstTextAddress();

	/**
	 * Returns the first data address assemblers should use.
	 *
	 * @return the first data address.
	 */
	int getFirstDataAddress();

	/**
	 * Returns the first kernel text address assemblers should use.
	 *
	 * @return the first kernel text address.
	 */
	int getFirstKernelTextAddress();


	/**
	 * Returns the first kernel data address assemblers should use.
	 *
	 * @return the first kernel data address.
	 */
	int getFirstKernelDataAddress();

	/**
	 * Returns the external address assemblers should use.
	 *
	 * @return the external kernel data address.
	 */
	int getFirstExternalAddress();

	/**
	 * Creates a deep copy of the memory.
	 *
	 * @return the copy.
	 */
	Memory copy();
}
