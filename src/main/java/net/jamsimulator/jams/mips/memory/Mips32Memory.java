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

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a MIPS32 memory managed by a {@link SimpleMemory}.
 * This memory has all the sections a MIPS memory would have.
 */
public class Mips32Memory extends SimpleMemory {

	public static final int KERNEL_RESERVED_1 = 0;
	public static final int TEXT = 0x04000000;
	public static final int DATA = 0x10000000;
	public static final int KERNEL_TEXT = 0x80000000;
	public static final int KERNEL_DATA = 0x90000000;
	public static final int MEMORY_MAPPED_IO = 0xFFFF0000;
	public static final int KERNEL_RESERVED_2 = 0xFFFF0000 + 10;

	public static final int EXTERNAL = DATA;
	public static final int STATIC_DATA = 0x10010000;
	public static final int HEAP = 0x10010000;


	public Mips32Memory() {
		super(false,
				Mips32Memory.TEXT, Mips32Memory.STATIC_DATA, Mips32Memory.KERNEL_TEXT,
				Mips32Memory.KERNEL_DATA, Mips32Memory.EXTERNAL,
				new MemorySection("Kernel reserved 1", KERNEL_RESERVED_1, TEXT),
				new MemorySection("Text", TEXT, DATA - TEXT),
				new MemorySection("Data", DATA, 0x70000000),
				new MemorySection("Kernel text", KERNEL_TEXT, 0x10000000),
				new MemorySection("Kernel data", KERNEL_DATA, MEMORY_MAPPED_IO - KERNEL_DATA),
				new MemorySection("Memory mapped IO", MEMORY_MAPPED_IO, 10, 10),
				new MemorySection("Kernel reserved 2", KERNEL_RESERVED_2, 0xFFFFFFFF - 0xFFFF0000 - 9));
	}

	private Mips32Memory(Map<String, MemorySection> sections, boolean bigEndian) {
		super(sections, bigEndian, Mips32Memory.TEXT, Mips32Memory.STATIC_DATA, Mips32Memory.KERNEL_TEXT,
				Mips32Memory.KERNEL_DATA, Mips32Memory.EXTERNAL);
	}


	@Override
	public Memory copy() {
		HashMap<String, MemorySection> sections = new HashMap<>();
		this.sections.forEach((name, section) -> sections.put(name, section.copy()));
		return new Mips32Memory(sections, bigEndian);
	}
}
