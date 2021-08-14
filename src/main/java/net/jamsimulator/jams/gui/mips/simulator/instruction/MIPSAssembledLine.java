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

package net.jamsimulator.jams.gui.mips.simulator.instruction;

import java.util.Optional;

/**
 * Represents a line inside a {@link MIPSAssembledCodeViewer}.
 * <p>
 * Developers shouldn't store instances of this class, as they will be deleted
 * when the user changes the style of the viewer.
 */
public class MIPSAssembledLine {

    private final int line;
    private final int address;
    private final boolean hasCode;
    private int code;

    /**
     * Creates the line with an address and an instruction code.
     * <p>
     * This constructor should be used by instructions.
     *
     * @param line    the index of the line.
     * @param address the address.
     * @param code    the instruction code.
     */
    public MIPSAssembledLine(int line, int address, int code) {
        this.line = line;
        this.address = address;
        this.code = code;
        hasCode = true;
    }

    /**
     * Creates the line without address and code.
     * <p>
     * This constructor should be used by labels.
     *
     * @param line the index of the line.
     */
    public MIPSAssembledLine(int line) {
        this.line = line;
        this.address = -1;
        this.code = -1;
        hasCode = false;
    }

    /**
     * Returns the index of the line.
     *
     * @return the index.
     */
    public int getLine() {
        return line;
    }

    /**
     * Returns the addres of the line, if present.
     *
     * @return the address if present.
     */
    public Optional<Integer> getAddress() {
        return hasCode ? Optional.of(address) : Optional.empty();
    }

    /**
     * Returns the instruction code of the line, if present.
     *
     * @return the code if present.
     */
    public Optional<Integer> getCode() {
        return hasCode ? Optional.of(code) : Optional.empty();
    }

    public void setCode(int code) {
        this.code = code;
    }
}
