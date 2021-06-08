package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.mips.simulation.MIPSSimulation;

public class SimulationUndoStepEvent extends SimulationEvent {

	private final long undoCycle;

	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link MIPSSimulation} thatcreated this event.
	 */
	protected SimulationUndoStepEvent(MIPSSimulation<?> simulation, long undoCycle) {
		super(simulation);
		this.undoCycle = undoCycle;
	}

	public long getUndoCycle() {
		return undoCycle;
	}

	public static class Before extends SimulationUndoStepEvent implements Cancellable {

		private boolean cancelled;

		/**
		 * Creates the simulation event.
		 *
		 * @param simulation the {@link MIPSSimulation} thatcreated this event.
		 */
		public Before(MIPSSimulation<?> simulation, long undoCycle) {
			super(simulation, undoCycle);
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


	public static class After extends SimulationUndoStepEvent {

		/**
		 * Creates the simulation event.
		 *
		 * @param simulation the {@link MIPSSimulation} thatcreated this event.
		 */
		public After(MIPSSimulation<?> simulation, long undoCycle) {
			super(simulation, undoCycle);
		}
	}
}
