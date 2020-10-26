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

/**
 * Represents a MIPS32 memory managed by a {@link SimpleMemory}.
 * This memory has all the sections a MIPS memory would have.
 */
public class MIPS32Memory extends SimpleMemory {

	public static final int KERNEL_RESERVED_1 = 0;
	public static final int TEXT = 0x00400000;
	public static final int DATA = 0x10000000;
	public static final int KERNEL_TEXT = 0x80000000;
	public static final int KERNEL_DATA = 0x90000000;
	public static final int MEMORY_MAPPED_IO = 0xFFFF0000;
	public static final int KERNEL_RESERVED_2 = 0xFFFF0000 + 10;

	public static final int EXTERNAL = DATA;
	public static final int STATIC_DATA = 0x10010000;
	public static final int HEAP = 0x10010000;
	public static final int STACK = 0x7fffeffc;

	public static final int EXCEPTION_HANDLER = 0x80000180;

	public static final String KERNEL_RESERVED_1_NAME = "Kernel reserved 1";
	public static final String TEXT_NAME = "Text";
	public static final String DATA_NAME = "Data";
	public static final String KERNEL_TEXT_NAME = "Kernel text";
	public static final String KERNEL_DATA_NAME = "Kernel data";
	public static final String MEMORY_MAPPED_IO_NAME = "Memory mapped IO";
	public static final String KERNEL_RESERVED_2_NAME = "Kernel reserved 2";


	public MIPS32Memory() {
		super(false,
				MIPS32Memory.TEXT, MIPS32Memory.STATIC_DATA, MIPS32Memory.KERNEL_TEXT,
				MIPS32Memory.KERNEL_DATA, MIPS32Memory.EXTERNAL,
				new MemorySection(KERNEL_RESERVED_1_NAME, KERNEL_RESERVED_1, TEXT),
				new MemorySection(TEXT_NAME, TEXT, DATA - TEXT),
				new MemorySection(DATA_NAME, DATA, 0x70000000),
				new MemorySection(KERNEL_TEXT_NAME, KERNEL_TEXT, 0x10000000),
				new MemorySection(KERNEL_DATA_NAME, KERNEL_DATA, MEMORY_MAPPED_IO - KERNEL_DATA),
				new MemorySection(MEMORY_MAPPED_IO_NAME, MEMORY_MAPPED_IO, 10, 10),
				new MemorySection(KERNEL_RESERVED_2_NAME, KERNEL_RESERVED_2, 0xFFFFFFFF - 0xFFFF0000 - 9));
	}

	private MIPS32Memory(MemorySection[] sections, boolean bigEndian) {
		super(sections, bigEndian, MIPS32Memory.TEXT, MIPS32Memory.STATIC_DATA, MIPS32Memory.KERNEL_TEXT,
				MIPS32Memory.KERNEL_DATA, MIPS32Memory.EXTERNAL);
	}


	@Override
	public Memory copy() {
		MemorySection[] sections = new MemorySection[this.sections.length];
		for (int i = 0; i < this.sections.length; i++) {
			sections[i] = this.sections[i].copy();
		}

		MIPS32Memory memory = new MIPS32Memory(sections, bigEndian);
		memory.savedNextDataAddress = nextDataAddress;
		memory.nextDataAddress = nextDataAddress;
		memory.eventCallsEnabled = eventCallsEnabled;
		return memory;
	}
}
