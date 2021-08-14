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

import java.util.Arrays;
import java.util.Optional;

/**
 * Enumeration containing all the types of representation an instruction has.
 */
public enum MIPSAssembledInstructionViewerElement {

    DISASSEMBLED(30, "SIMULATION_INSTRUCTION_VIEWER_DISASSEMBLED"),
    HEX_CODE(10, "SIMULATION_INSTRUCTION_VIEWER_HEXADECIMAL"),
    ORIGINAL(50, "SIMULATION_INSTRUCTION_VIEWER_ORIGINAL");

    private final int maximumLength;
    private final String languageNode;

    MIPSAssembledInstructionViewerElement(int maximumLength, String languageNode) {
        this.maximumLength = maximumLength;
        this.languageNode = languageNode;
    }

    /**
     * Returns the element that matches the given name, if present.
     *
     * @param name the name.
     * @return the elemennt if present.
     */
    public static Optional<MIPSAssembledInstructionViewerElement> getByName(String name) {
        return Arrays.stream(values()).filter(target -> target.name().equals(name)).findAny();
    }

    /**
     * Returns the maximum text length of this representation.
     *
     * @return the maximum length.
     */
    public int getMaximumLength() {
        return maximumLength;
    }

    /**
     * Returns the language node of this representation.
     *
     * @return the language node.
     */
    public String getLanguageNode() {
        return languageNode;
    }
}
