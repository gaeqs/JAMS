package net.jamsimulator.jams.memory;

public class Mips32Memory extends Memory {

	public Mips32Memory() {
		super(false,
				new MemorySection("Kernel reserved 1", 0, 0x04000000),
				new MemorySection("Text segment", 0x04000000, 0x10000000 - 0x04000000),
				new MemorySection("Data", 0x10000000, 0x70000000),
				new MemorySection("Kernel text", 0x80000000, 0x10000000),
				new MemorySection("Kernel data", 0x90000000, 0xFFFF0000 - 0x90000000),
				new MemorySection("Memory mapped IO", 0xFFFF0000, 10, 10),
				new MemorySection("Kernel reserved 2", 0xFFFF0000 + 10, 0xFFFFFFFF - 0xFFFF0000 - 9));
	}
}
