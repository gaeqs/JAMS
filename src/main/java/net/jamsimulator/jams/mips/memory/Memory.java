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

package net.jamsimulator.jams.mips.memory;

import net.jamsimulator.jams.event.EventBroadcast;

import java.util.Optional;
import java.util.Set;

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
	 * THIS METHOD SHOULDN'T BE USED BY INSTRUCTIONS!
	 * <p>
	 * Returns the word stored in the given address.
	 *
	 * @param address      the address.
	 * @param callEvents   whether this method should call events. Events won't be called if {@link #areEventCallsEnabled()} is false.
	 * @param bypassCaches whether this method should bypass all caches.
	 * @return the word.
	 */
	int getWord(int address, boolean callEvents, boolean bypassCaches);

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
	 * Returns the next data address.
	 * This address is used to reserve more memory.
	 *
	 * @return the next data address.
	 */
	int getNextDataAddress();

	/**
	 * Sets the next data address.
	 * <p>
	 * WARNING! This method should only be used by simulations when they undo a memory reservation!
	 *
	 * @param nextDataAddress the next data address.
	 */
	void setNextDataAddress(int nextDataAddress);

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
	 * Returns the memory located at the next hierarchy level, if present.
	 * <p>
	 * If this memory is a the last hierarchy level this method returns {@code Optional.empty()}.
	 *
	 * @return the memory located at the next hierarchy level.
	 */
	Optional<Memory> getNextLevelMemory();

	/**
	 * Reserves 'length' bytes in the dynamic data section.
	 *
	 * @param length the amount of bytes to reserve.
	 * @return the first address of the reserved data.
	 */
	int allocateMemory(int length);

	/**
	 * Enables or disables event calls.
	 * <p>
	 * If this feature is disable the memory will work faster, but actions won't be able to be listened.
	 * <p>
	 * This state won't be registered by {@link #saveState()}, but it will be copied if you use {@link #copy()}.
	 *
	 * @param enable whether this feature should be enabled or disabled.
	 */
	void enableEventCalls(boolean enable);

	/**
	 * Returns whether event calls are enabled.
	 * <p>
	 * If this feature is disable the memory will work faster, but actions won't be able to be listened.
	 * <p>
	 * This state won't be registered by {@link #saveState()}, but it will be copied if you use {@link #copy()}.
	 *
	 * @return whether this feature is enabled.
	 * @see #enableEventCalls(boolean)
	 */
	boolean areEventCallsEnabled();

	/**
	 * Creates a deep copy of the memory.
	 *
	 * @return the copy.
	 */
	Memory copy();

	/**
	 * Saves the current state of the {@link Memory}.
	 * You can return to this state using {@link #restoreSavedState()}.
	 */
	void saveState();

	/**
	 * Returns this memory state to the latest state saved using {@link #saveState()}.
	 * If no saved states are found the memory will be wiped.
	 */
	void restoreSavedState();

	/**
	 * Returns all {@link MemorySection}s inside this memory. The returned {@link Set} is unmodifiable.
	 * <p>
	 * These sections shouldn't be used to modify the memory. Use the main memory instead.
	 *
	 * @return all {@link MemorySection}s inside an unmodifiable {@link Set}.
	 */
	Set<MemorySection> getMemorySections();

	/**
	 * Returns the name of the {@link MemorySection} that matches the given address.
	 *
	 * @param address the address.
	 * @return the name of the memory section.
	 * @throws IndexOutOfBoundsException if no section contains the address.
	 */
	String getMemorySectionName(int address);

	/**
	 * Returns the {@link MemorySection} that matches the given name, if present.
	 *
	 * @param name the name.
	 * @return the {@link MemorySection}, if present.
	 */
	Optional<MemorySection> getMemorySection(String name);

	/**
	 * Returns the {@link MemorySection} that matches the given address.
	 *
	 * @param address the address.
	 * @return the {@link MemorySection}.
	 * throws IndexOutOfBoundsException if no section contains the address.
	 */
	MemorySection getMemorySection(int address);
}
