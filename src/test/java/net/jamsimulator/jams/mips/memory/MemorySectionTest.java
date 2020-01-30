package net.jamsimulator.jams.mips.memory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MemorySectionTest {

	static MemorySection section;

	@BeforeAll
	static void initialize() {
		section = new MemorySection("test", 0x01000000, 1024, 64);
	}

	@Test
	void memoryTest() {
		section.setWord(0x010000F0, -178892, false);
		assertEquals(-178892, section.getWord(0x010000F0, false),
				"Error while getting memory data.");
	}

}