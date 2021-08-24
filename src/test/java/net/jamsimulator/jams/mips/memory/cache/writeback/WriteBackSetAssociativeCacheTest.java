/*
 *  MIT License
 *
 *  Copyright (c) 2021 Gael Rial Costas
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package net.jamsimulator.jams.mips.memory.cache.writeback;

import net.jamsimulator.jams.manager.ResourceProvider;
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
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.MIPSSimulationData;
import net.jamsimulator.jams.mips.syscall.SimulationSyscallExecutions;
import net.jamsimulator.jams.utils.RawFileData;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WriteBackSetAssociativeCacheTest {

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
		WriteBackSetAssociativeCache cache = new WriteBackSetAssociativeCache(null, memory, 4, 4, 2, policy);
		cache.setByte(memory.getFirstDataAddress(), (byte) 10);
		assertEquals(1, cache.getStats().misses(), "Cache didn't miss!");
		byte b = cache.getByte(memory.getFirstDataAddress());
		assertEquals((byte) 10, b, "Byte is not equals!");
	}

	void words(CacheReplacementPolicy policy) {
		Memory memory = new MIPS32Memory();
		WriteBackSetAssociativeCache cache = new WriteBackSetAssociativeCache(null, memory, 4, 4, 2, policy);
		cache.setWord(memory.getFirstDataAddress(), 23573);
		assertEquals(1, cache.getStats().misses(), "Cache didn't miss!");
		int w = cache.getWord(memory.getFirstDataAddress());
		assertEquals(23573, w, "Byte is not equals!");
	}


	@Test
	void testSimpleProblem() {
		InstructionSet inst = new MIPS32r6InstructionSet(ResourceProvider.JAMS);
		DirectiveSet dir = new MIPS32DirectiveSet(ResourceProvider.JAMS);
		Registers reg = new MIPS32Registers();
		Cache mem = new WriteBackSetAssociativeCache(null, new MIPS32Memory(), 4, 8, 2, CacheReplacementPolicy.LRU);

		MIPS32Assembler assembler = new MIPS32Assembler(Collections.singleton(
				new RawFileData("test.asm", CacheTestsData.PROGRAM)), inst, dir, reg, mem, null);
		assembler.assemble();

		MIPSSimulationData data = new MIPSSimulationData(new SimulationSyscallExecutions(), new File(""), null, assembler.getOriginals(), assembler.getAllLabels(), false, false, true, true, true);
		MIPSSimulation<?> simulation = assembler.createSimulation(SingleCycleArchitecture.INSTANCE, data);

		mem = (Cache) simulation.getMemory();

		//mem.resetCache();
		simulation.executeAll();

		try {
			simulation.waitForExecutionFinish();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		CacheStats stats = mem.getStats();
		System.out.println(stats);
		System.out.print(stats.hits() * 100F / stats.operations());
		System.out.println("%");

		int current;
		for (int i = 0; i < 1024; i += 4) {
			current = 0;
			for (int j = i / 4; j < 256; j++) {
				current += j;
			}
			assertEquals(current, mem.getWord(0x10010000 + i, false, false, true));
		}

		assertEquals(33408, stats.operations());
		assertEquals(25152, stats.hits());
		assertEquals(8256, stats.misses());


	}

}