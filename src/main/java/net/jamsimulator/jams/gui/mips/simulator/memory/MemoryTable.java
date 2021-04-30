package net.jamsimulator.jams.gui.mips.simulator.memory;

import net.jamsimulator.jams.mips.memory.Memory;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.NumberRepresentation;

/**
 * Represents a table showing the contents of a memory.
 * <p>
 * This table can be a {@link SimpleMemoryTable} if the contents are from a simple memory or
 * a  {@link CacheMemoryTable} if the contents are from a cache.
 */
public interface MemoryTable {

    /**
     * Returns the {@link Simulation} that owns the represented memory.
     *
     * @return the simulation.
     */
    Simulation<?> getSimulation();

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
     *
     * Table populations can be considered "table refreshes".
     *
     * @param listener the runnable.
     */
    void afterPopulate(Runnable listener);
}
