package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

public class SimulationAddBreakpointEvent extends SimulationEvent {

    private final int address;

    public SimulationAddBreakpointEvent(MIPSSimulation<?> simulation, int address) {
        super(simulation);
        this.address = address;
    }

    public int getAddress() {
        return address;
    }
}
