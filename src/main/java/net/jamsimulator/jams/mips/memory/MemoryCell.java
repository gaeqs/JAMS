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

import java.util.Arrays;

/**
 * Represents a memory cell. A memory cell is a part of a {@link MemorySection} with a defined
 * length. It's responsible of storing part of the memory's data.
 * <p>
 * Addresses given to a memory cell must be relative.
 *
 * @see Memory
 * @see MemorySection
 */
public class MemoryCell {

	private byte[] data;

	/**
	 * Creates a memory cell using a length.
	 *
	 * @param length the length.
	 */
	public MemoryCell(int length) {
		data = new byte[length];
	}

	private MemoryCell(byte[] data) {
		this.data = data;
	}

	/**
	 * Returns the byte stored into the given relative address.
	 *
	 * @param address the relative address.
	 * @return the byte.
	 */
	public byte getByte(int address) {
		return data[address];
	}

	/**
	 * Stores the given byte into the given relative address.
	 *
	 * @param address the relative address.
	 * @param b       the byte.
	 * @return the old byte.
	 */
	public byte setByte(int address, byte b) {
		byte old = data[address];
		data[address] = b;
		return old;
	}

	/**
	 * Returns the word stored into the given relative address.
	 *
	 * @param address   the relative address.
	 * @param bigEndian whether the memory is big endian.
	 * @return the word.
	 */
	public int getWord(int address, boolean bigEndian) {
		byte b0 = data[address++];
		byte b1 = data[address++];
		byte b2 = data[address++];
		byte b3 = data[address];
		return bigEndian ? merge(b3, b2, b1, b0) : merge(b0, b1, b2, b3);
	}

	/**
	 * Stores the given word into the given relative address.
	 *
	 * @param address   the relative address.
	 * @param word      the word.
	 * @param bigEndian whether the memory is big endian.
	 * @return the old word.
	 */
	public int setWord(int address, int word, boolean bigEndian) {
		int old = getWord(address, bigEndian);
		byte[] array = split(word);
		if (bigEndian) {
			data[address++] = array[3];
			data[address++] = array[2];
			data[address++] = array[1];
			data[address] = array[0];
		} else {
			data[address++] = array[0];
			data[address++] = array[1];
			data[address++] = array[2];
			data[address] = array[3];
		}
		return old;
	}

	/**
	 * Creates a copy of this memory cell.
	 *
	 * @return the copy.
	 */
	public MemoryCell copy() {
		return new MemoryCell(Arrays.copyOf(data, data.length));
	}


	public static int merge(byte b0, byte b1, byte b2, byte b3) {
		int i = Byte.toUnsignedInt(b3);
		i <<= 8;
		i += Byte.toUnsignedInt(b2);
		i <<= 8;
		i += Byte.toUnsignedInt(b1);
		i <<= 8;
		return i + Byte.toUnsignedInt(b0);
	}

	public static byte[] split(int i) {
		byte[] array = new byte[4];
		array[0] = (byte) (i & 0xFF);
		i >>>= 8;
		array[1] = (byte) (i & 0xFF);
		i >>>= 8;
		array[2] = (byte) (i & 0xFF);
		i >>>= 8;
		array[3] = (byte) (i & 0xFF);
		return array;
	}

}
