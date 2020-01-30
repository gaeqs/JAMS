package net.jamsimulator.jams.memory;

import org.junit.jupiter.api.Test;

class Mips32MemoryTest {

	@Test
	void testMemoryMap() {
		Mips32Memory memory = new Mips32Memory();
		memory.getSections().forEach(target -> {
			System.out.println("Section " + target.getName() + ": from " + Integer.toHexString(target.getFirstAddress())
					+ " to " + Integer.toHexString(target.getLastAddress()));
		});
	}

}