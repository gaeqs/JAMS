package net.jamsimulator.jams.mips.simulation.pipelined.event;

import net.jamsimulator.jams.mips.simulation.pipelined.Pipeline;
import net.jamsimulator.jams.mips.simulation.pipelined.PipelinedSimulation;

/**
 * This event is called when a {@link Pipeline} shifts its instructions.
 */
public class PipelineShiftEvent extends PipelinedSimulationEvent {

	protected final long cycle;
	protected final Pipeline pipeline;
	private final int shiftAmount;
	protected int toFetch;

	private PipelineShiftEvent(PipelinedSimulation simulation, Pipeline pipeline, int toFetch, int shiftAmount) {
		super(simulation);
		this.cycle = simulation.getCycles();
		this.pipeline = pipeline;
		this.shiftAmount = shiftAmount;
		this.toFetch = toFetch;
	}

	/**
	 * Returns the cycle being executed on the simulation when the pipeline is being shifted.
	 *
	 * @return the cycle.
	 */
	public long getCycle() {
		return cycle;
	}

	/**
	 * Returns the {@link Pipeline} that is shifting.
	 *
	 * @return the {@link Pipeline}.
	 */
	public Pipeline getPipeline() {
		return pipeline;
	}

	/**
	 * Returns the address of the next instruction to fetch.
	 *
	 * @return the address of the next instruction to fetch.
	 */
	public int getInstructionToFetch() {
		return toFetch;
	}

	/**
	 * Returns the amount of instructions being shifted.
	 *
	 * @return the amount of instructions being shifted.
	 */
	public int getShiftAmount() {
		return shiftAmount;
	}

	/**
	 * This event is called before a {@link Pipeline}'s shift.
	 */
	public static class Before extends PipelineShiftEvent {

		public Before(PipelinedSimulation simulation, Pipeline status, int toFetch, int shiftAmount) {
			super(simulation, status, toFetch, shiftAmount);
		}

		/**
		 * Sets the address of the next instruction to fetch.
		 *
		 * @param toFetch the address.
		 */
		public void setInstructionToFetch(int toFetch) {
			this.toFetch = toFetch;
		}
	}

	/**
	 * This event is called after a {@link Pipeline}'s shift.
	 */
	public static class After extends PipelineShiftEvent {

		public After(PipelinedSimulation simulation, Pipeline status, int toFetch, int shiftAmount) {
			super(simulation, status, toFetch, shiftAmount);
		}
	}
}
