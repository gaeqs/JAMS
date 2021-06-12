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
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

import java.util.LinkedList;

/**
 * Instances of this class collects all changes made in a step.
 */
public class StepChanges<Arch extends Architecture> {

    private final LinkedList<SimulationChange<? super Arch>> changes;
    private final Object lock = new Object();

    public StepChanges() {
        changes = new LinkedList<>();
    }

    public void addChange(SimulationChange<? super Arch> change) {
        synchronized (lock) {
            changes.addFirst(change);
        }
    }

    public void restore(MIPSSimulation<? extends Arch> simulation) {
        synchronized (lock) {
            changes.forEach(target -> target.restore(simulation));
            changes.clear();
        }
    }

    public void removeCacheChanges(Memory last) {
        synchronized (lock) {
            var iterator = changes.iterator();
            while (iterator.hasNext()) {
                var next = iterator.next();
                if (next instanceof SimulationChangeCacheOperation) iterator.remove();
                if (next instanceof SimulationChangeMemoryByte) ((SimulationChangeMemoryByte) next).setMemory(last);
                if (next instanceof SimulationChangeMemoryWord) ((SimulationChangeMemoryWord) next).setMemory(last);
            }
        }
    }

}
