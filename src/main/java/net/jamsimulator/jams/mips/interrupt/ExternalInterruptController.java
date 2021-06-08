package net.jamsimulator.jams.mips.interrupt;

import net.jamsimulator.jams.mips.simulation.MIPSSimulation;
import net.jamsimulator.jams.utils.Validate;

import java.util.TreeSet;

public class ExternalInterruptController {

    private final TreeSet<Integer> pendingInterrupts;
    private MIPSInterruptException softwareInterrupt;

    public ExternalInterruptController() {
        pendingInterrupts = new TreeSet<>();
    }

    public boolean hasPendingInterrupts() {
        return pendingInterrupts.size() > 0;
    }

    public boolean isRequestingInterrupts(MIPSSimulation<?> simulation) {
        return pendingInterrupts.size() > 0 && simulation.areMIPSInterruptsEnabled() &&
                simulation.getIPLevel() < pendingInterrupts.last();
    }

    public void addRequest(int level) {
        Validate.isTrue(level > 1 && level < 64,
                "Hardware levels must be between 2 and 63! (Value 1 is reserved for software instructions.)");
        pendingInterrupts.add(level);
    }

    public void addSoftwareRequest(MIPSInterruptException softwareInterrupt) {
        Validate.notNull(softwareInterrupt, "Interrupt cannot be null!");
        this.softwareInterrupt = softwareInterrupt;
        pendingInterrupts.add(1);
    }

    public MIPSInterruptException getSoftwareInterrupt() {
        return softwareInterrupt;
    }

    public int getRequestedIPL() {
        return pendingInterrupts.pollLast();
    }

    public void reset() {
        pendingInterrupts.clear();
        softwareInterrupt = null;
    }
}
