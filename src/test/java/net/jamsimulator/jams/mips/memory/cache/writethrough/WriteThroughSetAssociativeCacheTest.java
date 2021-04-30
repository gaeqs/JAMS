package net.jamsimulator.jams.mips.memory.cache.writethrough;

import net.jamsimulator.jams.mips.architecture.SingleCycleArchitecture;
import net.jamsimulator.jams.mips.assembler.MIPS32Assembler;
import net.jamsimulator.jams.mips.directive.set.DirectiveSet;
import net.jamsimulator.jams.mips.directive.set.MIPS32DirectiveSet;
import net.jamsimulator.jams.mips.instruction.set.InstructionSet;
import net.jamsimulator.jams.mips.instruction.set.MIPS32r6InstructionSet;
import net.jamsimulator.jams.mips.memory.MIPS32Memory;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.memory.cache.CacheReplacementPolicy;
import net.jamsimulator.jams.mips.memory.cache.CacheStats;
import net.jamsimulator.jams.mips.memory.cache.CacheTestsData;
import net.jamsimulator.jams.mips.register.MIPS32Registers;
import net.jamsimulator.jams.mips.register.Registers;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.mips.simulation.SimulationData;
import net.jamsimulator.jams.mips.syscall.SimulationSyscallExecutions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WriteThroughSetAssociativeCacheTest {

	@Test
	void bytesLRU() {
		bytes(CacheReplacementPolicy.LRU);
	}

	@Test
	void bytesRandom() {
		bytes(CacheReplacementPolicy.RANDOM);
	}

	@Test
	void bytesFIFO() {
		bytes(CacheReplacementPolicy.FIFO);
	}

	@Test
	void wordsLRU() {
		words(CacheReplacementPolicy.LRU);
	}

	@Test
	void wordsRandom() {
		words(CacheReplacementPolicy.RANDOM);
	}

	@Test
	void wordsFIFO() {
		words(CacheReplacementPolicy.FIFO);
	}


	void bytes(CacheReplacementPolicy policy) {
		Memory memory = new MIPS32Memory();
		WriteThroughSetAssociativeCache cache = new WriteThroughSetAssociativeCache(null, memory, 4, 4, 2, policy);
		cache.setByte(memory.getFirstDataAddress(), (byte) 10);
		assertEquals(1, cache.getStats().getMisses(), "Cache didn't miss!");
		byte b = cache.getByte(memory.getFirstDataAddress());
		assertEquals((byte) 10, b, "Byte is not equals!");
	}

	void words(CacheReplacementPolicy policy) {
		Memory memory = new MIPS32Memory();
		WriteThroughSetAssociativeCache cache = new WriteThroughSetAssociativeCache(null, memory, 4, 4, 2, policy);
		cache.setWord(memory.getFirstDataAddress(), 23573);
		assertEquals(1, cache.getStats().getMisses(), "Cache didn't miss!");
		int w = cache.getWord(memory.getFirstDataAddress());
		assertEquals(23573, w, "Byte is not equals!");
	}


	@Test
	void testSimpleProblem() {
		InstructionSet inst = MIPS32r6InstructionSet.INSTANCE;
		DirectiveSet dir = MIPS32DirectiveSet.INSTANCE;
		Registers reg = new MIPS32Registers();
		Cache mem = new WriteThroughSetAssociativeCache(null, new MIPS32Memory(), 4, 8, 2, CacheReplacementPolicy.LRU);

		MIPS32Assembler assembler = new MIPS32Assembler(Collections.singletonMap("test.asm", CacheTestsData.PROGRAM), inst, dir, reg, mem, null);
		assembler.assemble();

		SimulationData data = new SimulationData(new SimulationSyscallExecutions(), new File(""), null, assembler.getOriginals(), assembler.getAllLabels(), false, false, true, true, true);
		Simulation<?> simulation = assembler.createSimulation(SingleCycleArchitecture.INSTANCE, data);

		mem = (Cache) simulation.getMemory();

		//mem.resetCache();
		simulation.executeAll();

		simulation.waitForExecutionFinish();

		CacheStats stats = mem.getStats();
		System.out.println(stats);
		System.out.print(stats.getHits() * 100F / stats.getOperations());
		System.out.println("%");

		int current;
		for (int i = 0; i < 1024; i += 4) {
			current = 0;
			for (int j = i / 4; j < 256; j++) {
				current += j;
			}
			assertEquals(current, mem.getWord(0x10010000 + i, false, false, true));
		}

		assertEquals(33408, stats.getOperations());
		assertEquals(25152, stats.getHits());
		assertEquals(8256, stats.getMisses());


	}

}