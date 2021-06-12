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

import net.jamsimulator.jams.mips.simulation.file.event.SimulationFileWriteEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;

public class OutSimulationFile implements SimulationFile {

    protected final SimulationFiles files;
    protected final int id;
    protected final ArrayList<Byte> data;
    protected final File file;

    public OutSimulationFile(SimulationFiles files, int id, File file, boolean append) throws IOException {
        this.files = files;
        this.id = id;
        this.data = new ArrayList<>();
        this.file = file;

        if (append) {
            for (byte b : Files.readAllBytes(file.toPath())) {
                data.add(b);
            }
        }
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
        throw new RuntimeException("Couldn't read from this file.");
    }

    @Override
    public void write(byte[] bytes) {
        SimulationFileWriteEvent.Before before = files.callEvent(new SimulationFileWriteEvent.Before(this, bytes));
        if (before.isCancelled()) return;
        bytes = before.getData();

        for (byte b : bytes) {
            data.add(b);
        }

        files.callEvent(new SimulationFileWriteEvent.After(this, bytes));
    }

    @Override
    public void revert(int bytes) {
        Iterator<Byte> iterator = data.iterator();
        int remaining = data.size() - bytes;

        while (iterator.hasNext()) {
            iterator.next();
            if (remaining > 0) {
                remaining--;
            } else {
                iterator.remove();
            }
        }
    }

    @Override
    public void close() throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        for (byte b : data) {
            out.write(b);
        }
        out.close();
    }
}
