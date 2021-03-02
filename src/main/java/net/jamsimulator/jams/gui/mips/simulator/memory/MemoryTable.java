package net.jamsimulator.jams.gui.mips.simulator.memory;

import net.jamsimulator.jams.mips.simulation.Simulation;

public interface MemoryTable {

    Simulation<?> getSimulation();

    int getRows();

    MemoryRepresentation getRepresentation();

    void setRepresentation(MemoryRepresentation representation);

    void nextPage();

    void previousPage();
}
