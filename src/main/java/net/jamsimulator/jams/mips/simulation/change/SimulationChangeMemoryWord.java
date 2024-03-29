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

package net.jamsimulator.jams.mips.simulation.change;

import net.jamsimulator.jams.mips.architecture.Architecture;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.memory.cache.Cache;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

/**
 * A {@link SimulationChange} that registers a word change inside the {@link MIPSSimulation}'s {@link net.jamsimulator.jams.mips.memory.Memory}.
 */
public class SimulationChangeMemoryWord extends SimulationChange<Architecture> {

    private final int address, old;
    private final long operations, hits;
    private Memory memory;

    public SimulationChangeMemoryWord(Memory memory, int address, int old) {
        this.memory = memory;
        this.address = address;
        this.old = old;

        if (memory instanceof Cache) {
            var stats = ((Cache) memory).getStats();
            operations = stats.operations();
            hits = stats.hits();
        } else {
            operations = hits = 0;
        }
    }

    public void setMemory(Memory memory) {
        this.memory = memory;
    }

    @Override
    public void restore(MIPSSimulation<? extends Architecture> simulation) {
        memory.setWord(address, old);
        if (memory instanceof Cache) {
            ((Cache) memory).forceStats(operations, hits);
        }
    }
}
