package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.Simulation;

public class SimulationRemoveBreakpointEvent extends SimulationEvent {

    private final int address;

    public SimulationRemoveBreakpointEvent(Simulation<?> simulation, int address) {
        super(simulation);
        this.address = address;
    }

    public int getAddress() {
        return address;
    }
}
