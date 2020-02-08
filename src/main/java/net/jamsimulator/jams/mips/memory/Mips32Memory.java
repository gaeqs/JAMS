package net.jamsimulator.jams.mips.memory;

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

	public Mips32Memory() {
		super(false,
				new MemorySection("Kernel reserved 1", KERNEL_RESERVED_1, TEXT),
				new MemorySection("Text", TEXT, DATA - TEXT),
				new MemorySection("Data", DATA, 0x70000000),
				new MemorySection("Kernel text", KERNEL_TEXT, 0x10000000),
				new MemorySection("Kernel data", KERNEL_DATA, MEMORY_MAPPED_IO - KERNEL_DATA),
				new MemorySection("Memory mapped IO", MEMORY_MAPPED_IO, 10, 10),
				new MemorySection("Kernel reserved 2", KERNEL_RESERVED_2, 0xFFFFFFFF - 0xFFFF0000 - 9));
	}
}
