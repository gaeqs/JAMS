package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.mips.simulation.Simulation;
import net.jamsimulator.jams.utils.Validate;

public class SimulationCycleEvent extends SimulationEvent {

    protected final long cycle;

    SimulationCycleEvent(Simulation<?> simulation, long cycle) {
        super(simulation);
        Validate.notNull(simulation, "Simulation cannot be null!");
        this.cycle = cycle;
    }

    public long getCycle() {
        return cycle;
    }

    public static class Before extends SimulationCycleEvent implements Cancellable {

        private boolean cancelled;

        public Before(Simulation<?> simulation, long cycle) {
            super(simulation, cycle);
        }

        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }
    }

    public static class After extends SimulationCycleEvent {

        public After(Simulation<?> simulation, long cycle) {
            super(simulation, cycle);
        }
    }

}
