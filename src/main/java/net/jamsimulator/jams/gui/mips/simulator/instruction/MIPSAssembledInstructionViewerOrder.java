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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class manages the order different {@link MIPSAssembledInstructionViewerElement element}s are shown
 * in the instruction viewer.
 */
public class MIPSAssembledInstructionViewerOrder {

    public static final MIPSAssembledInstructionViewerOrder DEFAULT = new MIPSAssembledInstructionViewerOrder();

    static {
        DEFAULT.addElement(MIPSAssembledInstructionViewerElement.HEX_CODE);
        DEFAULT.addElement(MIPSAssembledInstructionViewerElement.DISASSEMBLED);
        DEFAULT.addElement(MIPSAssembledInstructionViewerElement.ORIGINAL);
    }

    private final List<MIPSAssembledInstructionViewerElement> elements;

    /**
     * Creates the instance of the manager.
     */
    public MIPSAssembledInstructionViewerOrder() {
        elements = new ArrayList<>(3);
    }

    /**
     * Returns an unmodifiable list containing the elements of this manager.
     *
     * @return the list.
     */
    public List<MIPSAssembledInstructionViewerElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    /**
     * Returns the amount of elements this manager has.
     *
     * @return the amount of elements.o
     */
    public int getSize() {
        return elements.size();
    }

    /**
     * Adds an element at the given index.
     *
     * @param index   the index.
     * @param element the element.
     */
    public void addElement(int index, MIPSAssembledInstructionViewerElement element) {
        elements.add(index, element);
    }

    /**
     * Adds an element at the end of this manager.
     *
     * @param element the element.
     */
    public void addElement(MIPSAssembledInstructionViewerElement element) {
        elements.add(element);
    }

    /**
     * Removes the element at the given index. from this manager.
     *
     * @param index the index.
     */
    public void removeElement(int index) {
        elements.remove(index);
    }

}
