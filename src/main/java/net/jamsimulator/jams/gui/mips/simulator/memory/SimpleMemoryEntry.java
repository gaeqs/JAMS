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

package net.jamsimulator.jams.gui.mips.simulator.memory;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.utils.NumberRepresentation;
import net.jamsimulator.jams.utils.StringUtils;

/**
 * Represents a row inside a {@link SimpleMemoryTable}.
 */
public class SimpleMemoryEntry {

    private final Memory memory;
    private final int address;
    private final NumberRepresentation representation;

    private StringProperty pAddress, p0, p4, p8, pC;

    public SimpleMemoryEntry(Memory memory, int address, NumberRepresentation representation) {
        this.memory = memory;
        this.address = address;
        this.representation = representation;
    }

    public Memory getMemory() {
        return memory;
    }

    public int getAddress() {
        return address;
    }

    public NumberRepresentation getRepresentation() {
        return representation;
    }

    public StringProperty addressProperty() {
        if (pAddress == null) {
            pAddress = new SimpleStringProperty(null, "address", "0x" + StringUtils.addZeros(Integer.toHexString(address), 8));
        }

        return pAddress;
    }

    public StringProperty p0Property() {
        if (p0 == null) {
            p0 = new SimpleStringProperty(null, "p0", represent(address));
        }

        return p0;
    }

    public StringProperty p4Property() {
        if (p4 == null) {
            p4 = new SimpleStringProperty(null, "p4", represent(address + 4));
        }

        return p4;
    }

    public StringProperty p8Property() {
        if (p8 == null) {
            p8 = new SimpleStringProperty(null, "p8", represent(address + 8));
        }

        return p8;
    }

    public StringProperty pCProperty() {
        if (pC == null) {
            pC = new SimpleStringProperty(null, "pC", represent(address + 12));
        }

        return pC;
    }

    public void refresh() {
        update(address, 0);
        update(address + 4, 4);
        update(address + 8, 8);
        update(address + 12, 12);
    }

    public void update(int address, int offset) {
        String data = represent(address);
        switch (offset) {
            case 0 -> {
                if (p0 == null) break;
                p0.setValue(data);
            }
            case 4 -> {
                if (p4 == null) break;
                p4.setValue(data);
            }
            case 8 -> {
                if (p8 == null) break;
                p8.setValue(data);
            }
            case 12 -> {
                if (pC == null) break;
                pC.setValue(data);
            }
        }
    }

    private String represent(int address) {
        return representation.represent(memory, address);
    }
}
