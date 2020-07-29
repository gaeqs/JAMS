package net.jamsimulator.jams.mips.simulation.event;

import net.jamsimulator.jams.event.Cancellable;
import net.jamsimulator.jams.mips.simulation.Simulation;

public class SimulationUndoStepEvent extends SimulationEvent {

	/**
	 * Creates the simulation event.
	 *
	 * @param simulation the {@link Simulation} tha created this event.
	 */
	protected SimulationUndoStepEvent(Simulation<?> simulation) {
		super(simulation);
	}

	public static class Before extends SimulationUndoStepEvent implements Cancellable {

		private boolean cancelled;

		/**
		 * Creates the simulation event.
		 *
		 * @param simulation the {@link Simulation} tha created this event.
		 */
		public Before(Simulation<?> simulation) {
			super(simulation);
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
		 * @param simulation the {@link Simulation} tha created this event.
		 */
		public After(Simulation<?> simulation) {
			super(simulation);
		}
	}
}
