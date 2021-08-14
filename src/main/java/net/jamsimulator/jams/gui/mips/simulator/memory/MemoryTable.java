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

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.utils.NumberRepresentation;

/**
 * Represents a table showing the contents of a memory.
 * <p>
 * This table can be a {@link SimpleMemoryTable} if the contents are from a simple memory or
 * a  {@link CacheMemoryTable} if the contents are from a cache.
 */
public interface MemoryTable {

    /**
     * Returns the {@link MIPSSimulation} that owns the represented memory.
     *
     * @return the simulation.
     */
    MIPSSimulation<?> getSimulation();

    /**
     * Returns the represented memory.
     *
     * @return the represented memory.
     */
    Memory getMemory();

    /**
     * Returns the amount of rows this table has.
     *
     * @return the amount of rows.
     */
    int getRows();

    /**
     * Returns the way the cells are represented inside this table.
     *
     * @return the representation of the cells inside this table.
     */
    NumberRepresentation getRepresentation();

    /**
     * Sets the way the cells are represented inside this table.
     *
     * @param representation the representation of the cells inside this table.
     */
    void setRepresentation(NumberRepresentation representation);

    /**
     * Moves to the next page.
     */
    void nextPage();

    /**
     * Moves to the previous page.
     */
    void previousPage();

    /**
     * The given runnable will run after a table population.
     * <p>
     * Table populations can be considered "table refreshes".
     *
     * @param listener the runnable.
     */
    void afterPopulate(Runnable listener);
}
