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

package net.jamsimulator.jams.mips.simulation.file;

import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileReadEvent;

public class ConsoleInSimulationFile implements SimulationFile {

    protected final SimulationFiles files;
    protected final int id;
    protected final MIPSSimulation<?> simulation;

    public ConsoleInSimulationFile(SimulationFiles files, int id, MIPSSimulation<?> simulation) {
        this.files = files;
        this.id = id;
        this.simulation = simulation;
    }

    @Override
    public SimulationFiles getFiles() {
        return files;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public byte[] read(int bytes) {

        SimulationFileReadEvent.Before before = files.callEvent(new SimulationFileReadEvent.Before(this, bytes));
        if (before.isCancelled()) return new byte[0];
        bytes = before.getAmount();

        if (bytes < 0) {
            throw new RuntimeException("Invalid amount: " + bytes);
        }
        byte[] array = new byte[bytes];
        int current = 0;

        char c;
        while (current < array.length) {
            c = simulation.popCharOrLock();
            array[current++] = (byte) c;
        }

        files.callEvent(new SimulationFileReadEvent.After(this, bytes, array));

        return array;
    }

    @Override
    public void write(byte[] bytes) {
        throw new RuntimeException("Couldn't write to this file.");
    }

    @Override
    public void revert(int bytes) {

    }

    @Override
    public void close() {
        throw new RuntimeException("This file cannot be closed.");
    }
}
